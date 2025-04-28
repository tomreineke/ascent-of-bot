use std::cell::RefCell;

use anyhow::Result;
use enumset::{EnumSet, EnumSetType};
use num::Zero;
use skia_safe::{Canvas, Color, Surface, IRect, Point, Paint, Color4f};
use slotmap::{Key, SlotMap};
use tracing::instrument;

use crate::error::{guarded_unit, UnitResult};
use crate::forest::Forest;
use crate::geo::{Vec2i, SkiaConvertible, RectExt, IRectExt};
use crate::layout::event::LayoutEventCollector;
use crate::library::SkiaTreeLibrary;
use crate::node::core::NodeCore;
use crate::node::{NodeElement, NodeKey};
use crate::style::{Visibility, Flow};

#[derive(EnumSetType, Debug)]
#[enumset(repr = "u8")]
pub enum InputStateFlag {
    Hovered,
    Pressed,
    Selected
}

#[repr(C)]
#[derive(Clone, Copy, PartialEq, Eq, Debug)]
pub struct InputState(pub EnumSet<InputStateFlag>);

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_draw_on_surface(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    surface: *mut Surface,
) -> UnitResult {
    // return UnitResult::Success;
    let skiatree = unsafe { &mut *skiatree };
    let forest = unsafe { &*forest }.borrow_mut();
    let surface = unsafe { &mut *surface };
    guarded_unit(|| {
        let mut collector = LayoutEventCollector::new();
        let nodes = &skiatree.nodes;
        let input_state = EnumSet::empty();
        let size = Vec2i::new(surface.width(), surface.height());
        let canvas = surface.canvas();
        canvas.clear(Color::TRANSPARENT);
        //let red = Paint::new(Color4f::from(Color::RED), None);
        // canvas.draw_line(Point::new(0.0, 0.0), Point::new(size.x as f32, size.y as f32), &red);
        for &root_key in forest.layers.iter().filter(|&&n| !n.is_null()) {
            if !root_key.is_null() {
                let mut root = nodes[root_key].borrow_mut();
                root.update_layout(nodes, size, &mut collector);
                // let mut writer = BufWriter::new(std::fs::OpenOptions::new()
                //     .write(true)
                //     .append(true)
                //     .open(r#"C:\Projects\Hypogean\Saved\Logs\skiadump.txt"#)?);
                // root.dump(nodes, 0, &mut writer)?;
                root.draw(nodes, &forest, canvas, input_state, size)?;
            }
        }
        collector.dispatch(skiatree);
        Ok(())
    })
}

enum NodeIterationState {
    Opening(NodeKey),
    Closing(NodeKey)
}

impl NodeElement {
    fn draw(
        &mut self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        forest: &Forest,
        canvas: &mut Canvas,
        mut input_state: EnumSet<InputStateFlag>,
        parent_size: Vec2i
    ) -> Result<()> {
        if self.style.visibility != Visibility::Visible {
            return Ok(());
        }
        let parent_bounds = IRect::from_pos_size(Vec2i::zero(), parent_size);
        let own_bounds = IRect::from_pos_size(self.relative_position, self.size);
        if !own_bounds.intersects(&parent_bounds) {
            return Ok(());
        }
        if self.hover_tag.is_hovered() {
            input_state.insert(InputStateFlag::Hovered);
        } else if !self.inherits_input_state {
            input_state.remove(InputStateFlag::Hovered);
        }
        if forest.is_pressed(self.key) {
            input_state.insert(InputStateFlag::Pressed);
        } else if !self.inherits_input_state {
            input_state.remove(InputStateFlag::Pressed);
        }
        if self.is_selected {
            input_state.insert(InputStateFlag::Selected);
        } else if !self.inherits_input_state {
            input_state.remove(InputStateFlag::Selected);
        }
        canvas.save();
        if self.style.is_scroll_viewport {
            canvas.clip_rect(own_bounds.to_float(), None, None);
        }
        canvas.translate((self.relative_position + self.visual_translation[input_state]).to_skia());
    
        let background_style = &mut self.background[input_state];
        background_style.draw_on(canvas, self.size)?;
        self.core.draw(nodes, forest, canvas, self.size, input_state)?;
    
        let len = self.children.len();
        let drawn_children = match self.style.flow {
            Flow::Vertical if len > 10 => {
                fn find_index(
                    node: &NodeElement,
                    nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
                    value: i32,
                    extract: impl Fn(&NodeElement) -> i32
                ) -> usize {
                    let index = node.children.binary_search_by_key(&value, |&n| extract(&nodes[n].borrow()));//.unwrap_or_else(|x| x);
                    index.unwrap_or_else(|x| x)
                }
                let start = find_index(self, nodes, 0, |n| n.relative_position.y)
                    .saturating_sub(1)
                    .clamp(0, len - 1);
                let end = find_index(self, nodes, self.size.y, |n| n.relative_position.y + n.size.y)
                    .clamp(start, len - 1);
                &self.children[start..=end]
            }
            _ => &self.children[..]
        };
        if let Some(ref table) = self.table {
            let mut paint = Paint::default();
            paint.set_color4f(Color4f::new(1.0, 1.0, 1.0, 1.0), None);
            for row in &table.rows {
                if row.style.top_stroke_width > 0.0 {
                    paint.set_stroke_width(row.style.top_stroke_width);
                    canvas.draw_line(Point::new(0.0, row.y as f32), Point::new(self.size.x as f32, row.y as f32), &paint);
                }
                if row.style.bottom_stroke_width > 0.0 {
                    paint.set_stroke_width(row.style.bottom_stroke_width);
                    canvas.draw_line(Point::new(0.0, (row.y + row.min_height) as f32), Point::new(self.size.x as f32, (row.y + row.min_height) as f32), &paint);
                }
            }
        }
        for &child_key in drawn_children {
            let mut child = nodes[child_key].borrow_mut();
            child.draw(nodes, forest, canvas, input_state, self.size)?;
        }
        canvas.restore();
        Ok(())
    }
    
}

impl NodeCore {
    pub fn draw(
        &self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        forest: &Forest,
        canvas: &mut Canvas,
        size: Vec2i,
        input_state: EnumSet<InputStateFlag>
    ) -> Result<()> {
        match *self {
            NodeCore::Null => (),
            NodeCore::Paragraph(ref paragraph) => {
                let paragraph = RefCell::borrow_mut(paragraph);
                paragraph.paragraph.paint(canvas, Point::new(0.0, 0.0));
                for &child_key in &paragraph.nodes {
                    nodes[child_key].borrow_mut().draw(nodes, forest, canvas, input_state, size)?;
                }
            },
        }
        Ok(())
    }
}
