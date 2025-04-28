use std::cell::{RefCell, RefMut};
use std::fmt::Debug;
use std::io::{BufWriter, Write};
use std::ops::{Add, AddAssign};

use anyhow::Result;
use itertools::Itertools;
use nalgebra::clamp;
use slotmap::SlotMap;
use smallvec::SmallVec;
use tracing::instrument;

use crate::error::{guarded_unit, UnitResult};
use crate::geo::Vec2i;
use crate::layout::event::LayoutEventCollector;
use crate::library::SkiaTreeLibrary;
use crate::log::log;
use crate::node::{children_mut_of, NodeElement, NodeKey};
use crate::slot_maps::{KeyExt, SlotMapExt};
use crate::style::{Align, Flow, Visibility};
use crate::table::Row;

mod enums;
pub mod event;
mod geo;

#[derive(Clone, Copy, Default, Debug)]

/// 
/// Invariant: min_width <= relaxed_with
pub struct WidthDemand {
    pub min_width: i32,
    pub relaxed_width: i32
}

impl WidthDemand {
    pub fn enlarged(mut self, amount: i32) -> WidthDemand {
        self.min_width += amount;
        self.relaxed_width += amount;
        self
    }

    pub fn max(mut self, rhs: WidthDemand) -> WidthDemand {
        self.min_width = self.min_width.max(rhs.min_width);
        self.relaxed_width = self.relaxed_width.max(rhs.relaxed_width);
        self
    }

    pub fn clamp(self, min: i32, max: i32) -> WidthDemand {
        WidthDemand {
            min_width: clamp(self.min_width, min, max),
            relaxed_width: clamp(self.relaxed_width, min, max)
        }
    }
}

impl Add<i32> for WidthDemand {
    type Output = WidthDemand;

    fn add(self, rhs: i32) -> Self::Output {
        WidthDemand {
            min_width: self.min_width + rhs,
            relaxed_width: self.relaxed_width + rhs
        }
    }
}

impl AddAssign for WidthDemand {
    fn add_assign(&mut self, rhs: Self) {
        self.min_width += rhs.min_width;
        self.relaxed_width += rhs.relaxed_width;
    }
}

impl NodeElement {
    #[instrument(skip(nodes, collector))]
    pub fn update_layout(
        &mut self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        provided_size: Vec2i,
        collector: &mut LayoutEventCollector
    ) {
        self.update_width_demand(nodes);
        self.update_actual_width(nodes, provided_size.x, collector);
        self.update_min_height(nodes, collector);
        self.update_actual_height_and_set_relative_position(nodes, provided_size.y, collector);
    }

    /// Width needed for hgaps, but only if all children are placed horizontally next to each other.
    #[instrument]
    fn horizontal_hgap_demand(&self) -> i32 {
        if self.children.is_empty() {
            0
        } else {
            (self.children.len() - 1) as i32 * self.style.hgap
        }
    }

    #[instrument]
    fn update_width_demand(&mut self, nodes: &SlotMap<NodeKey, RefCell<NodeElement>>) {
        if !self.is_width_demand_dirty {
            return;
        }
        self.is_width_demand_dirty = false;
        let mut result = self.core.width_demand();
        match self.style.flow {
            Flow::LeftToRight => {
                for mut child in self.children_mut(nodes) {
                    child.update_width_demand(nodes);
                    result += child.width_demand_including_margin;
                }
                result = result.enlarged(self.horizontal_hgap_demand());
            },
            Flow::LeftToRightThenTopToBottom | Flow::LeftToRightThenBottomToTop => {
                for mut child in self.children_mut(nodes) {
                    child.update_width_demand(nodes);
                    result.min_width = result.min_width.max(child.width_demand_including_margin.min_width);
                    result.relaxed_width += child.width_demand_including_margin.relaxed_width;
                }
                result.relaxed_width += self.horizontal_hgap_demand();

            },
            Flow::None | Flow::Vertical => {
                for mut child in self.children_mut(nodes) {
                    child.update_width_demand(nodes);
                    result = result.max(child.width_demand_including_margin);
                }
            },
            Flow::Table => {
                if let Some(ref mut table) = self.table {
                    let column_count = table.columns.len();
                    for column in &mut table.columns {
                        column.width_demand = WidthDemand::default();
                    }
                    for row in &children_mut_of!(self, nodes).chunks(column_count) {
                        for (column_index, mut child) in row.enumerate() {
                            let column = &mut table.columns[column_index];
                            child.update_width_demand(nodes);
                            column.width_demand = column.width_demand.max(child.width_demand_including_margin)
                        }
                    }
                    for column in &mut table.columns {
                        column.width_demand = column.width_demand.clamp(column.style.min_width, column.style.max_width);
                        column.width_demand_including_margin = column.width_demand.enlarged(column.style.left + column.style.right);
                        result += column.width_demand_including_margin;
                    }
                    result = result.enlarged((column_count - 1) as i32 * self.style.hgap);
                } else {
                    log!("Node {:?} has flow=Table but no table data", self.key);
                }
            }
        }
        self.width_demand = result.clamp(self.style.min_width, self.style.max_width);
        self.width_demand_including_margin = self.width_demand.enlarged(self.style.left + self.style.right);
    }

    #[instrument(skip(nodes, preprocess, f))]
    fn iterate_row_wise<P: FnMut(&mut NodeElement), F: FnMut(i32, &mut SmallVec<[RefMut<NodeElement>; 16]>)>(
        &self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        available_width: i32,
        mut preprocess: P,
        mut f: F
    ) {
        let mut cursor_x = 0;
        let mut row: SmallVec<[RefMut<NodeElement>; 16]> = SmallVec::new();
        for mut child in self.children_mut(nodes) {
            preprocess(&mut child);
            if cursor_x == 0 {
                cursor_x = child.width_including_margin();
            } else {
                let new_cursor_x = cursor_x + self.style.hgap + child.width_including_margin();
                if new_cursor_x <= available_width {
                    cursor_x = new_cursor_x;
                } else {
                    f(cursor_x, &mut row);
                    row.clear();
                    cursor_x = child.width_including_margin();
                }
            }
            row.push(child);
        }
        if !row.is_empty() {
            f(cursor_x, &mut row);
        }
    }

    #[instrument(skip(nodes, collector))]
    fn update_actual_width(
        &mut self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        provided_width: i32,
        collector: &mut LayoutEventCollector,
    ) -> bool {
        if provided_width == self.last_provided_width_parameter_for_actual_width {
            return false;
        }
        self.last_provided_width_parameter_for_actual_width = provided_width;
        let mut is_height_invalidated = false;
        let width_demand = self.width_demand;
        let available_width =
            (provided_width - self.style.left - self.style.right)
                .max(width_demand.min_width)
                .clamp(self.style.min_width, self.style.max_width);

        let mut actual_content_width = 0;
        let actual_width = match self.style.flow {
            Flow::LeftToRight => {
                let mut optional_width = (available_width - width_demand.min_width).max(0);
                for mut child  in self.children_mut(nodes) {
                    let needed_width = child.width_demand_including_margin.min_width;
                    let provided_width = needed_width + optional_width;
                    is_height_invalidated |= child.update_actual_width(nodes, provided_width, collector);
                    let taken_optional_width = (child.width_including_margin() - needed_width).max(0);
                    optional_width = (optional_width - taken_optional_width).max(0);
                    if actual_content_width == 0 {
                        actual_content_width = child.width_including_margin();
                    } else {
                        actual_content_width += self.style.hgap + child.width_including_margin();
                    }
                }
                if self.style.horizontal_align == Align::Stretch { provided_width } else { actual_content_width }
            }
            Flow::LeftToRightThenTopToBottom | Flow::LeftToRightThenBottomToTop => {
                self.iterate_row_wise(
                    nodes,
                    available_width,
                    |child| {
                        child.update_actual_width(nodes, available_width, collector);
                    },
                    |row_width, _| {
                        actual_content_width = actual_content_width.max(row_width);
                    }
                );
                if self.style.horizontal_align == Align::Stretch { provided_width } else { actual_content_width }
            }
            Flow::Table => {
                if let Some(ref mut table) = self.table {
                    let column_count = table.columns.len();
                    let mut optional_width = (available_width - width_demand.min_width).max(0);
                    for (column_index, column) in &mut table.columns.iter_mut().enumerate() {
                        let needed_width = column.width_demand_including_margin.min_width;
                        let provided_width = needed_width + optional_width;
                        let mut taken_optional_width = 0;
                        column.width = column.width_demand.min_width;
                        for mut child in self.children.iter().chunks(column_count).into_iter()
                            .flat_map(|chunk| chunk.dropping(column_index).next())
                            .copied().map(|key| nodes[key].borrow_mut())
                        {
                            is_height_invalidated |= child.update_actual_width(nodes, provided_width, collector);
                            taken_optional_width = taken_optional_width.max(child.width_including_margin() - needed_width);
                            column.width = column.width.max(child.width_including_margin());
                        }
                        column.width_including_margin = column.width + column.style.left + column.style.right;
                        optional_width = (optional_width - taken_optional_width).max(0);
                        if actual_content_width == 0 {
                            actual_content_width = column.width_including_margin;
                        } else {
                            actual_content_width += self.style.hgap + column.width_including_margin;
                        }
                    }
                }
                if self.style.horizontal_align == Align::Stretch { provided_width } else { actual_content_width }
            }
            _ => {
                let actual_width = width_demand.min_width.max(if self.style.horizontal_align == Align::Stretch {
                    available_width
                } else {
                    available_width.min(width_demand.relaxed_width)
                });
                self.core.set_actual_width(nodes, actual_width);

                for mut child in self.children_mut(nodes) {
                    is_height_invalidated |= child.update_actual_width(nodes, actual_width, collector);
                    actual_content_width = actual_content_width.max(child.width_including_margin());
                }

                actual_width
            }
        };

        if self.content_size.x != actual_content_width || self.size.x != actual_width {
            collector.on_resize(self);
            self.content_size.x = actual_content_width;
            self.size.x = actual_width;
            is_height_invalidated = true;
        }
        if is_height_invalidated {
            self.is_min_height_dirty = true;
            self.last_provided_height_parameter_for_actual_height = -1;
        }
        is_height_invalidated
    }

    #[instrument(skip(nodes, collector))]
    fn update_min_height(
        &mut self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        collector: &mut LayoutEventCollector
    ) {
        if !self.is_min_height_dirty {
            return;
        }
        self.is_min_height_dirty = false;
        let mut result = 0;
        match self.style.flow {
            Flow::None | Flow::LeftToRight => {
                for mut child in self.children_mut(nodes) {
                    child.update_min_height(nodes, collector);
                    result = result.max(child.min_height_including_margin);
                }
            },
            Flow::Vertical => {
                for mut child in self.children_mut(nodes) {
                    child.update_min_height(nodes, collector);
                    result += child.min_height_including_margin;
                }
                if !self.children.is_empty() {
                    result += (self.children.len() - 1) as i32 * self.style.vgap;
                }
            },
            Flow::LeftToRightThenTopToBottom | Flow::LeftToRightThenBottomToTop => {
                self.iterate_row_wise(
                    nodes,
                    self.content_size.x,
                    |child| { child.update_min_height(nodes, collector); },
                    |_, row| {
                        if result != 0 {
                            result += self.style.vgap;
                        }
                        result += row.iter()
                            .map(|child| child.min_height_including_margin)
                            .max().unwrap_or(0);
                    }
                );
            },
            Flow::Table => {
                if let Some(ref mut table) = self.table {
                    let column_count = table.columns.len();
                    let row_count = self.children.len().div_ceil(column_count);
                    if table.rows.len() < row_count {
                        table.rows.resize_with(row_count, Row::default);
                    }
                    for (row_index, (row, row_data)) in children_mut_of!(self, nodes)
                        .chunks(column_count).into_iter()
                        .zip(table.rows.iter_mut())
                        .enumerate()
                    {
                        let mut row_min_height = 0;
                        for mut child in row {
                            child.update_min_height(nodes, collector);
                            row_min_height = row_min_height.max(child.min_height_including_margin);
                        }

                        if row_data.style.min_height > row_data.style.max_height {
                            log!(
                                "row_data.style.min_height={} > row_data.style.max_height={} for row {row_index} in {}",
                                row_data.style.min_height,
                                row_data.style.max_height,
                                self.debug_name
                            );
                            row_data.min_height = row_data.style.min_height;
                        } else {
                            row_data.min_height = row_min_height.clamp(self.style.min_height, self.style.max_height);
                        }
                        row_data.min_height_including_margin = row_data.min_height + row_data.style.top + row_data.style.bottom;

                        if row_index > 0 {
                            result += self.style.vgap;
                        }
                        result += row_data.min_height_including_margin;
                    }
                }
            }
        }
        result = result.max(self.core.min_height());
        if self.content_size.y != result {
            collector.on_resize(self);
            self.content_size.y = result;
        }
        if self.style.min_height > self.style.max_height {
            log!(
                "style.min_height={} > style.max_height={} for {}",
                self.style.min_height,
                self.style.max_height,
                self.debug_name
            );
            self.min_height = self.style.min_height;
        } else {
            self.min_height = result.clamp(self.style.min_height, self.style.max_height);
        }
        self.min_height_including_margin = self.min_height + self.style.top + self.style.bottom;
        // log!(r#"compute_min_height("{}") -> min_height={}"#, self.debug_name, self.min_height);
    }

    #[instrument(skip(nodes, collector))]
    fn update_actual_height_and_set_relative_position(
        &mut self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        provided_height: i32,
        collector: &mut LayoutEventCollector,
    ) {
        if self.last_provided_height_parameter_for_actual_height == provided_height {
            return;
        }
        self.last_provided_height_parameter_for_actual_height = provided_height;

        let actual_height = self.min_height.max(if self.style.vertical_align == Align::Stretch {
            provided_height - self.style.top - self.style.bottom
        } else {
            0
        });
        if self.size.y != actual_height {
            collector.on_resize(self);
            self.size.y = actual_height;
        }
        match self.style.flow {
            Flow::None => {
                for mut child in self.children_mut(nodes) {
                    if child.style.visibility == Visibility::Collapsed { continue; }
                    child.update_actual_height_and_set_relative_position(nodes, actual_height, collector);
                    child.set_relative_position(0, 0, self.size);
                }
            },
            Flow::LeftToRight => {
                let mut cursor_x = 0;
                for mut child in self.children_mut(nodes) {
                    if child.style.visibility == Visibility::Collapsed { continue; }
                    child.update_actual_height_and_set_relative_position(nodes, actual_height, collector);
                    child.set_relative_position(cursor_x, 0, self.size);
                    cursor_x += child.width_including_margin() + self.style.hgap;
                }
            },
            Flow::Vertical => {
                let mut cursor_y = self.style.layout_translation.y;
                for mut child in self.children_mut(nodes) {
                    if child.style.visibility == Visibility::Collapsed { continue; }
                    child.update_actual_height_and_set_relative_position(nodes, actual_height, collector);
                    child.set_relative_position(0, cursor_y, self.size);
                    cursor_y += child.style.top + child.size.y + child.style.bottom + self.style.vgap;
                }
            },
            flow @ Flow::LeftToRightThenTopToBottom | flow @ Flow::LeftToRightThenBottomToTop => {
                let mut cursor_y = 0;
                self.iterate_row_wise(
                    nodes,
                    self.content_size.x,
                    |_| {},
                    |row_width, row| {
                        let row_height = row.iter()
                            .map(|child| child.min_height_including_margin)
                            .max().unwrap_or(0);
                        let mut cursor_x = match row[0].style.horizontal_align {
                            Align::Min | Align::Stretch => 0,
                            Align::Center => (self.content_size.x - row_width) / 2,
                            Align::Max => self.content_size.x - row_width
                        };
                        let cy = if flow == Flow::LeftToRightThenTopToBottom {
                            cursor_y
                        } else {
                            self.content_size.y - cursor_y - row_height
                        };
                        for child in row {
                            child.update_actual_height_and_set_relative_position(nodes, row_height, collector);
                            let child_width = child.width_including_margin();
                            child.set_relative_position(cursor_x, cy, Vec2i::new(child_width, row_height));
                            cursor_x += child.width_including_margin() + self.style.hgap;
                        }
                        cursor_y += row_height + self.style.vgap;
                    }
                );
            }
            Flow::Table => {
                if let Some(ref mut table) = self.table {
                    let mut cursor_y = self.style.layout_translation.y;
                    let column_count = table.columns.len();
                    let row_count = self.children.len().div_ceil(column_count);
                    if table.rows.len() < row_count {
                        table.rows.resize_with(row_count, Row::default);
                    }
                    for (row, row_data) in children_mut_of!(self, nodes)
                        .chunks(column_count).into_iter()
                        .zip(table.rows.iter_mut())
                    {
                        let mut cursor_x = self.style.layout_translation.x;
                        for (column_index, mut child) in row.enumerate() {
                            let column = &table.columns[column_index];
                            row_data.y = cursor_y + row_data.style.top;
                            child.update_actual_height_and_set_relative_position(nodes, row_data.min_height, collector);
                            child.set_relative_position(
                                cursor_x + column.style.left,
                                row_data.y,
                                Vec2i::new(column.width, row_data.min_height)
                            );
                            cursor_x += column.width_including_margin + self.style.hgap;
                        }
                        cursor_y += row_data.min_height_including_margin + self.style.vgap;
                    }
                }
            }
        }
    }

    #[instrument]
    fn set_relative_position(&mut self, cursor_x: i32, cursor_y: i32, parent_size: Vec2i) {
        self.relative_position = Vec2i::new(
            cursor_x
                + self.style.left
                + self.style.horizontal_align.compute_offset(
                    parent_size.x - self.style.left - self.style.right,
                    self.size.x
                ),
            cursor_y
                + self.style.top
                + self.style.vertical_align.compute_offset(
                    parent_size.y - self.style.top - self.style.bottom,
                    self.size.y
                )
        );
    }

    pub fn dump<W: Write>(&self, nodes: &SlotMap<NodeKey, RefCell<NodeElement>>, depth: usize, writer: &mut BufWriter<W>) -> Result<()> {
        writeln!(
            writer,
            "{:width$}* {} (size=({}, {}), demand={:?}, left={}, right={}, align={:?}, style.min_width={}, style.max_width={}, computed minHeight={}, last_provided_width_parameter_for_actual_width={})",
            "",
            self.debug_name,
            self.size.x, self.size.y,
            self.width_demand,
            self.style.left,
            self.style.right,
            self.style.horizontal_align,
            self.style.min_width,
            self.style.max_width,
            self.min_height,
            self.last_provided_width_parameter_for_actual_width,
            width = 4 * depth
        )?;
        // self.last_parent_size_parameter_for_layout
        for &child_key in &self.children {
            nodes[child_key].borrow().dump(nodes, depth + 1, writer)?;
        }
        Ok(())
    }
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_force_layout(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    size: Vec2i
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded_unit(|| {
        let mut collector = LayoutEventCollector::new();
        skiatree.nodes.acc_mut(node_key)?.update_layout(&skiatree.nodes, size, &mut collector);
        collector.dispatch(skiatree);
        Ok(())
    })
}
