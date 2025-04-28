use std::cell::RefCell;
use std::rc::Rc;
use std::time::Instant;

use num::Zero;
use slotmap::Key;
use tracing::instrument;

use crate::error::{guarded_rc, guarded_unit, UnitResult};
use crate::geo::Vec2i;
use crate::input::HoverTag;
use crate::library::SkiaTreeLibrary;
use crate::node::{NodeKey, NodesContext};
use crate::rc_util::RcExt;
use crate::slot_maps::KeyExt;

pub enum Tooltippable {
    None,
    Candidate {
        node: NodeKey,
        time: Instant
    },
    Shown {
        node: NodeKey
    }
}

impl Tooltippable {
    pub fn uses_node(&self, n: NodeKey) -> bool {
        match *self {
            Tooltippable::None => false,
            Tooltippable::Candidate { node, .. } => node == n,
            Tooltippable::Shown { node, .. } => node == n,
        }
    }
}
pub struct Forest {
    pub layers: Vec<NodeKey>,

    pub last_mouse_position: Vec2i,

    pub hovered_nodes: Vec<NodeKey>,

    pub pressed_node: Option<NodeKey>,

    pub tooltippable: Tooltippable,

    pub last_hover_tag: HoverTag
}

impl Forest {
    pub fn is_pressed(&self, node_key: NodeKey) -> bool {
        self.pressed_node.map_or(false, |n| n == node_key)
    }

    pub fn hovered_node(&self) -> Option<NodeKey> {
        self.hovered_nodes.last().copied()
    }
}

impl Default for Forest {
    fn default() -> Self {
        Forest {
            layers: vec![],
            last_mouse_position: Vec2i::zero(),
            hovered_nodes: vec![],
            pressed_node: None,
            tooltippable: Tooltippable::None,
            last_hover_tag: HoverTag::First
        }
    }
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_new() -> *const RefCell<Forest> {
    guarded_rc(|| {
        Ok(Rc::new(RefCell::new(Forest::default())))
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_delete(forest: *const RefCell<Forest>) {
    drop(unsafe { Rc::from_raw(forest) });
}


#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_clone(forest: *const RefCell<Forest>) -> *const RefCell<Forest> {
    Rc::into_raw(Rc::clone_raw(forest))
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_tick(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>
) {
    let skiatree = unsafe { &mut *skiatree };
    let forest = unsafe { &*forest };
    skiatree.handle_tooltip_timer(forest);
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_set_layer(
    skiatree: *mut SkiaTreeLibrary,
    forest: *const RefCell<Forest>,
    layer_index: usize,
    node_key: u64
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let mut forest = unsafe { &*forest }.borrow_mut();
    let node_key = NodeKey::from_ffi(node_key);
    guarded_unit(|| {
        if forest.layers.len() <= layer_index {
            forest.layers.resize(layer_index + 1, NodeKey::null());
        }
        forest.layers[layer_index] = node_key;
        skiatree.propagate_needs_layout(node_key)?;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_forest_get_mouse_position(forest: *const RefCell<Forest>) -> Vec2i {
    unsafe { &*forest }.borrow().last_mouse_position
}
