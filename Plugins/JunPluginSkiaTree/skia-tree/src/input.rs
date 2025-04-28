use std::cell::RefCell;
use std::mem::replace;
use std::time::Instant;

use enumset::{EnumSet, EnumSetType};
use num::Zero;
use skia_safe::IRect;
use slotmap::Key;
use tracing::instrument;

use crate::ffi::FfiSlice;
use crate::forest::{Forest, Tooltippable};
use crate::geo::{IRectExt, RectExt, Vec2i};
use crate::library::SkiaTreeLibrary;
use crate::node::{NodeKey, NodeKeyExt};
use crate::style::Visibility;

#[derive(Clone, Copy, PartialEq, Eq)]
#[repr(u8)]
pub enum HoverTag {
    Unhovered,
    First,
    Second
}

impl HoverTag {
    pub fn is_hovered(self) -> bool {
        match self {
            HoverTag::Unhovered => false,
            HoverTag::First | HoverTag::Second => true
        }
    }

    pub fn other(self) -> HoverTag {
        match self {
            HoverTag::Unhovered => HoverTag::Unhovered,
            HoverTag::First => HoverTag::Second,
            HoverTag::Second => HoverTag::First
        }
    }
}

type ButtonChangeFunction = extern "C" fn(
    receiver: u64,
    position: Vec2i,
    hovered: u64,
    key_index: i32,
    modifiers: EnumSet<Modifier>
) -> bool;

#[repr(C)]
pub struct InputUpCalls {
    key_pressed: ButtonChangeFunction,
    key_released: ButtonChangeFunction,
    primary_pressed: extern "C" fn(u64, Vec2i, EnumSet<Modifier>) -> bool,
    primary_released: extern "C" fn(u64, Vec2i, u64, EnumSet<Modifier>),
    mouse_move: extern "C" fn(u64, Vec2i, EnumSet<Modifier>, bool) -> bool,
    mouse_drag: extern "C" fn(u64, Vec2i, u64, EnumSet<Modifier>),
    mouse_wheel: extern "C" fn(u64, Vec2i, f32, EnumSet<Modifier>) -> bool,

    hover_changed: extern "C" fn(*const u64, usize, *const u64, usize),

    has_tooltip: extern "C" fn(u64) -> bool,
    show_tooltip: extern "C" fn(u64, mouse_position: Vec2i),
    hide_tooltip: extern "C" fn(u64),

    pub resized: extern "C" fn(*const u64, usize),
}

#[derive(EnumSetType, Debug)]
#[enumset(repr = "u8")]
pub enum Modifier {
    Shift,
    Control,
    Alt,
    Command
}

#[derive(Clone)]
pub enum HitModel {
    None,
    Rect {
        border: IRect
    },
    RoundedRect {
        border: IRect,
        corner_radius: i32
    },
}

impl Default for HitModel {
    fn default() -> Self {
        HitModel::Rect { border: IRect::new_empty() }
    }
}

impl HitModel {
    pub fn is_hit(&self, bound: IRect, position: Vec2i) -> bool {
        match *self {
            HitModel::None => false,
            HitModel::Rect { border } => bound.offset_rect(border).contains_point(position),
            HitModel::RoundedRect { border, corner_radius } => {
                let effective_rect = bound.offset_rect(border);
                if !effective_rect.contains_point(position) { return false; }
                let x = position.x;
                let y = position.y;
                let dx = if x < effective_rect.left + corner_radius {
                    effective_rect.left + corner_radius - x
                } else if x < effective_rect.right - corner_radius {
                    return true;
                } else  {
                    x - (effective_rect.right - corner_radius)
                };
                let dy = if y < effective_rect.top + corner_radius {
                    effective_rect.top + corner_radius - y
                } else if y < effective_rect.bottom - corner_radius {
                    return true;
                } else {
                    y - (effective_rect.bottom - corner_radius)
                };
                dx * dx + dy * dy <= corner_radius * corner_radius
            }
        }
    }
}

fn hit_test(skiatree: &SkiaTreeLibrary, forest: &Forest, position: Vec2i) -> Vec<NodeKey> {
    fn test(skiatree: &SkiaTreeLibrary, node_key: NodeKey, position: Vec2i, result: &mut Vec<NodeKey>) -> bool {
        let node = skiatree.nodes[node_key].borrow();
        let relative_position = position - node.relative_position;
        let is_hit = node.style.visibility == Visibility::Visible && node.hit_model.is_hit(
            IRect::from_pos_size(Vec2i::zero(), node.size),
            relative_position
        );
        if is_hit {
            result.push(node_key);
            for &child_key in node.core.children().iter().rev() {
                if test(skiatree, child_key, relative_position, result) { return true; }
            }
            for &child_key in node.children.iter().rev() {
                if test(skiatree, child_key, relative_position, result) { return true; }
            }
            return node.consumes_hover;
        }
        false
    }
    let mut result = vec![];
    for &root in forest.layers.iter().rev().filter(|&&n| !n.is_null()) {
        if test(skiatree, root, position, &mut result) {
            return result;
        }
    }
    result
}

fn update_hover(skiatree: &mut SkiaTreeLibrary, forest: &mut Forest, position: Option<Vec2i>) {
    let new_hover = if let Some(position) = position {
        forest.last_mouse_position = position;
        hit_test(skiatree, forest, position)
    } else {
        vec![]
    };
    let last_tag = forest.last_hover_tag;
    let current_tag = last_tag.other();
    let mut unhovered: Vec<NodeKey> = vec![];
    let mut newly_hovered: Vec<NodeKey> = vec![];
    for &node_key in &new_hover {
        let mut node = skiatree.nodes[node_key].borrow_mut();
        if node.hover_tag == HoverTag::Unhovered {
            newly_hovered.push(node_key);
        }
        node.hover_tag = current_tag;
    }
    for &node_key in &forest.hovered_nodes {
        let mut node = skiatree.nodes[node_key].borrow_mut();
        if node.hover_tag == last_tag {
            unhovered.push(node_key);
            node.hover_tag = HoverTag::Unhovered;
        }
    }
    for &node_key in &unhovered {
        if forest.tooltippable.uses_node(node_key) {
            skiatree.replace_tooltippable(&mut forest.tooltippable, Tooltippable::None);
        }
    }
    for &node_key in &newly_hovered {
        let has_tooltip = (skiatree.input_upcalls.has_tooltip)(node_key.as_ffi());
        if has_tooltip {
            skiatree.replace_tooltippable(
                &mut forest.tooltippable,
                Tooltippable::Candidate { node: node_key, time: Instant::now() }
            );
        }
    }
    if !unhovered.is_empty() || !newly_hovered.is_empty() {
        (skiatree.input_upcalls.hover_changed)(
            unhovered.as_ptr_or_null() as *const u64,
            unhovered.len(),
            newly_hovered.as_ptr_or_null() as *const u64,
            newly_hovered.len()
        );
    }
    forest.hovered_nodes = new_hover;
    forest.last_hover_tag = current_tag;
}

impl SkiaTreeLibrary {
    fn replace_tooltippable(&self, current_tooltippable: &mut Tooltippable, new_tooltippable: Tooltippable) {
        let prev_tooltip = replace(current_tooltippable,new_tooltippable);
        if let Tooltippable::Shown { node } = prev_tooltip {
            (self.input_upcalls.hide_tooltip)(node.as_ffi());
        }
    }

    pub fn handle_tooltip_timer(&self, forest: &RefCell<Forest>) {
        let now = Instant::now();
        let mut forest = forest.borrow_mut();
        match forest.tooltippable {
            Tooltippable::None | Tooltippable::Shown { .. } => (),
            Tooltippable::Candidate { node, time } => {
                if now > time + self.tooltip_delay {
                    forest.tooltippable = Tooltippable::Shown { node };
                    let mouse_position = forest.last_mouse_position;
                    drop(forest);
                    (self.input_upcalls.show_tooltip)(node.as_ffi(), mouse_position);
                }
            }
        }
    }
}


#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_key_down(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    key_index: i32,
    modifiers: EnumSet<Modifier>
) -> bool {
    let skiatree = unsafe { &*skiatree };
    let forest = unsafe { &*forest }.borrow();
    input_key_changed(
        &forest,
        forest.last_mouse_position,
        key_index,
        modifiers, 
        skiatree.input_upcalls.key_pressed
    )
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_key_up(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    key_index: i32,
    modifiers: EnumSet<Modifier>
) -> bool {
    let skiatree = unsafe { &*skiatree };
    let forest = unsafe { &*forest }.borrow();
    input_key_changed(
        &forest,
        forest.last_mouse_position,
        key_index,
        modifiers,
        skiatree.input_upcalls.key_released
    )
}

fn input_key_changed(
    forest: &Forest,
    position: Vec2i,
    key_index: i32,
    modifiers: EnumSet<Modifier>,
    changed: ButtonChangeFunction
) -> bool {
    let hovered_node = forest.hovered_node().unwrap_or(NodeKey::null()).as_ffi();
    let notify = |node_key: NodeKey| -> bool {
        changed(node_key.as_ffi(), position, hovered_node, key_index, modifiers)
    };
    if let Some(pressed_node) = forest.pressed_node {
        if notify(pressed_node) { return true; }
    }
    for &node_key in forest.hovered_nodes.iter().rev() {
        if notify(node_key) { return true; }
    }
    false
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_double_click(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    position: Vec2i,
    button_index: i32,
    modifiers: EnumSet<Modifier>
) -> bool {
    mouse_button_down(skiatree, forest, position, button_index, modifiers, true)
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_mouse_button_down(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    position: Vec2i,
    button_index: i32,
    modifiers: EnumSet<Modifier>
) -> bool {
    mouse_button_down(skiatree, forest, position, button_index, modifiers, false)
}

fn mouse_button_down(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    position: Vec2i,
    button_index: i32,
    modifiers: EnumSet<Modifier>,
    _is_double: bool
) -> bool {
    let skiatree = unsafe { &mut *skiatree };
    let mut forest = unsafe { &*forest }.borrow_mut();
    update_hover(skiatree, &mut forest, Some(position));
    if button_index == skiatree.primary_button_index {
        let notify = |node_key: NodeKey| -> bool {
            (skiatree.input_upcalls.primary_pressed)(node_key.as_ffi(), position, modifiers)
        };
        for &node_key in forest.hovered_nodes.iter().rev() {
            if notify(node_key) {
                forest.pressed_node = Some(node_key);
                return true;
            }
        }
        // false
    } else {
        input_key_changed(
            &forest,
            position,
            button_index,
            modifiers,
            skiatree.input_upcalls.key_pressed
        );
    }
    // log!("mouse_button_down(position={position:?}, button_index={button_index}, modifiers={modifiers:?})");
    true
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_mouse_button_up(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    position: Vec2i,
    button_index: i32,
    modifiers: EnumSet<Modifier>
) -> bool {
    let skiatree = unsafe { &mut *skiatree };
    let mut forest = unsafe { &*forest }.borrow_mut();
    update_hover(skiatree, &mut forest, Some(position));
    if button_index == skiatree.primary_button_index {
        if let Some(pressed_node) = forest.pressed_node.take() {
            (skiatree.input_upcalls.primary_released)(
                pressed_node.as_ffi(),
                position,
                forest.hovered_node().unwrap_or(NodeKey::null()).as_ffi(),
                modifiers
            );
            true
        } else {
            false
        }
    } else {
        input_key_changed(
            &forest,
            position,
            button_index,
            modifiers,
            skiatree.input_upcalls.key_released
        )
    }
    
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_mouse_leave(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
) {
    let skiatree = unsafe { &mut *skiatree };
    let mut forest = unsafe { &*forest }.borrow_mut();
    update_hover(skiatree, &mut forest, None);
    // log!("mouse_leave()");
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_mouse_move(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    position: Vec2i,
    modifiers: EnumSet<Modifier>
) {
    let skiatree = unsafe { &mut *skiatree };
    let forest_cell = unsafe { &*forest };
    let mut forest = forest_cell.borrow_mut();
    if position == forest.last_mouse_position {
        return;
    }
    
    update_hover(skiatree, &mut forest, Some(position));
    let hovered_node = forest.hovered_node().unwrap_or(NodeKey::null()).as_ffi();
    let pressed_node = forest.pressed_node;
    drop(forest);
    let is_dragging = if let Some(pressed_node) = pressed_node {
        (skiatree.input_upcalls.mouse_drag)(
            pressed_node.as_ffi(),
            position,
            hovered_node,
            modifiers
        );
        true
    } else  {
        false
    };
    let notify = |node_key: NodeKey| -> bool {
        (skiatree.input_upcalls.mouse_move)(
            node_key.as_ffi(),
            position,
            modifiers,
            is_dragging
        )
    };
    for &node_key in forest_cell.borrow().hovered_nodes.iter().rev() {
        if notify(node_key) {
            break;
        }
    }
    
    // log!("mouse_move(position={position:?}, modifiers={modifiers:?})");

}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_input_mouse_wheel(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    position: Vec2i,
    wheel_delta: f32,
    modifiers: EnumSet<Modifier>
) {
    let skiatree = unsafe { &mut *skiatree };
    let mut forest = unsafe { &*forest }.borrow_mut();
    update_hover(skiatree, &mut forest, Some(position));
    // log!("mouse_wheel(position={position:?}, wheel_delta={wheel_delta}, modifiers={modifiers:?})");

    for &node_key in forest.hovered_nodes.iter().rev() {
        if (skiatree.input_upcalls.mouse_wheel)(node_key.as_ffi(), position, wheel_delta, modifiers) { break; }
    }
}
