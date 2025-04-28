use anyhow::{Result, bail};
use tracing::instrument;

use crate::error::{guarded, guarded_unit, UnitResult};
use crate::layout::WidthDemand;
use crate::library::SkiaTreeLibrary;
use crate::node::{get_node_property, NodeElement, NodeKey, NodesContext, set_node_property};
use crate::slot_maps::{KeyExt, SlotMapExt};
use crate::style::{Align, LARGE_SIZE};

pub struct Table {
    pub columns: Vec<Column>,
    pub rows: Vec<Row>
}

impl Table {
    fn columns(&mut self) -> &mut Vec<Column> {
        &mut self.columns
    }

    fn rows(&mut self) -> &mut Vec<Row> {
        &mut self.rows
    }
}

impl Default for Table {
    fn default() -> Self {
        Table {
            columns: vec![Column::default()],
            rows: vec![]
        }
    }
}

pub struct ColumnStyle {
    pub left: i32,

    pub right: i32,

    pub min_width: i32,

    pub max_width: i32,

    pub align: Align
}

impl Default for ColumnStyle {
    fn default() -> Self {
        ColumnStyle {
            left: 0,
            right: 0,
            min_width: 0,
            max_width: LARGE_SIZE,
            align: Align::Center
        }
    }
}

pub struct Column {
    pub style: ColumnStyle,

    pub width_demand: WidthDemand,

    pub width_demand_including_margin: WidthDemand,

    pub width: i32,

    pub width_including_margin: i32,
}

impl Default for Column {
    fn default() -> Self {
        Column {
            style: ColumnStyle::default(),
            width_demand: WidthDemand::default(),
            width_demand_including_margin: WidthDemand::default(),
            width: 0,
            width_including_margin: 0,
        }
    }
}

pub struct RowStyle {
    pub top: i32,

    pub bottom: i32,

    pub min_height: i32,

    pub max_height: i32,

    pub top_stroke_width: f32,

    pub bottom_stroke_width: f32,
}

impl Default for RowStyle {
    fn default() -> Self {
        RowStyle {
            top: 0,
            bottom: 0,
            min_height: 0,
            max_height: LARGE_SIZE,
            top_stroke_width: 0.0,
            bottom_stroke_width: 0.0
        }
    }
}

pub struct Row {
    pub style: RowStyle,

    pub min_height: i32,

    pub min_height_including_margin: i32,

    pub y: i32
}

impl Default for Row {
    fn default() -> Self {
        Row {
            style: RowStyle::default(),
            min_height: 0,
            min_height_including_margin: 0,
            y: 0
        }
    }
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_get_column_count(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.table.as_ref().map_or(0, |table| table.columns.len() as i32)
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_set_column_count(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_count: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        if column_count < 1 {
            bail!("Cannot set column count to {column_count}");
        }
        node.table
            .get_or_insert_with(|| Box::new(Table::default()))
            .columns.resize_with(column_count as usize, Column::default);
        Ok(())
    })
}

fn access_lane<L: Default, F: FnOnce(&mut Table) -> &mut Vec<L>>(node: &mut NodeElement, get_lanes: F, index: i32) -> &mut L {
    let index = index as usize;
    let table = node.table.get_or_insert_with(|| Box::new(Table::default()));
    let lane = get_lanes(table);
    if lane.len() <= index {
        lane.resize_with(index + 1, L::default);
    }
    &mut lane[index]
}

fn get_lane_property<L: Default, LF: FnOnce(&mut Table) -> &mut Vec<L>, T, F: FnOnce(&L) -> T>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    get_lanes: LF,
    index: i32,
    error_value: T,
    f: F
) -> T {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded(error_value, || {
        let mut node = skiatree.nodes.acc_mut(node_key)?;
        let lane = access_lane(&mut node, get_lanes, index);
        Ok(f(lane))
    })
}

fn set_lane_property<L: Default, LF: FnOnce(&mut Table) -> &mut Vec<L>, F: FnOnce(&mut L) -> Result<()>>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    get_lanes: LF,
    index: i32,
    f: F
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded_unit(|| {
        let mut node = skiatree.nodes.acc_mut(node_key)?;
        let lane = access_lane(&mut node, get_lanes, index);
        f(lane)?;
        drop(node);
        skiatree.propagate_needs_layout(node_key)
    })
}

fn get_lane_enum_property<L: Default, LF: FnOnce(&mut Table) -> &mut Vec<L>, T, F: FnOnce(&L) -> T>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    get_lanes: LF,
    index: i32,
    f: F
) -> i32 {
    get_lane_property(skiatree, node_key, get_lanes, index, -1, |lane| {
        let enum_value = f(lane);
        let ordinal = unsafe { *(&enum_value as *const T as *const u8) };
        ordinal as i32
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_get_left(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::columns, column_index, i32::MIN, |column| {
        column.style.left
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_set_left(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32,
    left: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::columns, column_index, |column| {
        column.style.left = left;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_get_right(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::columns, column_index, i32::MIN, |column| {
        column.style.right
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_set_right(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32,
    right: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::columns, column_index, |column| {
        column.style.right = right;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_get_min_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::columns, column_index, i32::MIN, |column| {
        column.style.min_width
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_set_min_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32,
    min_width: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::columns, column_index, |column| {
        column.style.min_width = min_width;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_get_max_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::columns, column_index, i32::MIN, |column| {
        column.style.max_width
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_set_max_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32,
    max_width: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::columns, column_index, |column| {
        column.style.max_width = max_width;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_get_align(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32
) -> i32 {
    get_lane_enum_property(skiatree, node_key, Table::columns, column_index, |column| {
        column.style.align
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_column_set_align(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    column_index: i32,
    ordinal: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::columns, column_index, |column| {
        column.style.align = Align::from_ordinal(ordinal)?;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_get_top(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::rows, row_index, i32::MIN, |row| {
        row.style.top
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_set_top(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32,
    top: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::rows, row_index, |column| {
        column.style.top = top;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_get_bottom(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::rows, row_index, i32::MIN, |row| {
        row.style.bottom
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_set_bottom(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32,
    bottom: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::rows, row_index, |column| {
        column.style.bottom = bottom;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_get_min_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::rows, row_index, i32::MIN, |row| {
        row.style.min_height
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_set_min_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32,
    min_height: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::rows, row_index, |column| {
        column.style.min_height = min_height;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_get_max_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32
) -> i32 {
    get_lane_property(skiatree, node_key, Table::rows, row_index, i32::MIN, |row| {
        row.style.max_height
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_set_max_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32,
    max_height: i32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::rows, row_index, |column| {
        column.style.max_height = max_height;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_get_top_stroke_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32
) -> f32 {
    get_lane_property(skiatree, node_key, Table::rows, row_index, f32::NAN, |row| {
        row.style.top_stroke_width
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_set_top_stroke_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32,
    top_stroke_width: f32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::rows, row_index, |column| {
        column.style.top_stroke_width = top_stroke_width;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_get_bottom_stroke_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32
) -> f32 {
    get_lane_property(skiatree, node_key, Table::rows, row_index, f32::NAN, |row| {
        row.style.bottom_stroke_width
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_table_row_set_bottom_stroke_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    row_index: i32,
    bottom_stroke_width: f32
) -> UnitResult {
    set_lane_property(skiatree, node_key, Table::rows, row_index, |column| {
        column.style.bottom_stroke_width = bottom_stroke_width;
        Ok(())
    })
}
