use std::cell::{RefCell, Ref, RefMut};
use std::ffi::c_char;
use std::fmt;
use std::ptr::null_mut;

use anyhow::{bail, Result};
use num::Zero;
use skia_safe::IRect;
use slotmap::{Key, new_key_type, SlotMap};
use smallvec::SmallVec;
use tracing::instrument;

use crate::background::Background;
use crate::drawing::InputStateFlag;
use crate::enumset_store::EnumSetMap;
use crate::error::{guarded, guarded_unit, UnitResult, BoolResult, guarded_bool, FfiExt};
use crate::ffi::{FfiToStr, ToFfiStr};
use crate::geo::{Vec2i, IRectExt};
use crate::input::{HitModel, HoverTag};
use crate::layout::WidthDemand;
use crate::library::SkiaTreeLibrary;
use crate::node::core::NodeCore;
use crate::slot_maps::{KeyExt, SlotMapExt};
use crate::style::Style;
use crate::table::Table;

mod appearance;
pub mod core;
mod hover;

new_key_type! {
    /// Key for the nodes.
    pub struct NodeKey;
}

pub trait NodeKeyExt {
    fn as_ffi(self) -> u64;
}

impl NodeKeyExt for NodeKey {
    fn as_ffi(self) -> u64 {
        self.data().as_ffi()
    }
}

pub type NodeChildren = SmallVec<[NodeKey; 1]>;

#[derive(Clone, Copy)]
pub enum Parent {
    None,
    Regular(NodeKey),
    Core(NodeKey)
}

pub struct NodeElement {
    pub key: NodeKey,

    pub parent: Parent,

    pub children: NodeChildren,

    pub style: Style,

    pub last_provided_width_parameter_for_actual_width: i32,

    pub is_width_demand_dirty: bool,

    pub is_min_height_dirty: bool,

    pub last_provided_height_parameter_for_actual_height: i32,

    pub width_demand: WidthDemand,

    pub width_demand_including_margin: WidthDemand,

    pub min_height: i32,

    pub min_height_including_margin: i32,

    /// Computed width for core and child nodes,
    /// without margins and before parent size or min/max attributs are taken into account.
    pub content_size: Vec2i,

    /// Computed actual width, which is the content_size coerced by parent size and min/max attributes.
    pub size: Vec2i,

    /// This flag must only be accessed by LayoutEventCollector.
    pub is_resize_event_queued: bool,

    pub relative_position: Vec2i,

    pub hit_model: HitModel,

    /// Prevents nodes, that lie below this node and do not descend from this node, to be hovered.
    pub consumes_hover: bool,

    pub inherits_input_state: bool,

    pub is_selected: bool,

    pub core: NodeCore,

    pub background: EnumSetMap<InputStateFlag, Background>,

    pub visual_translation: EnumSetMap<InputStateFlag, Vec2i>,

    pub debug_name: String,

    pub hover_tag: HoverTag,

    pub table: Option<Box<Table>>
}

impl NodeElement {
    pub fn mark_dirty(&mut self) {
        self.last_provided_width_parameter_for_actual_width = -1;
        self.is_width_demand_dirty = true;
        self.is_min_height_dirty = true;
        self.last_provided_height_parameter_for_actual_height = -1;
    }

    pub fn parent(&self) -> Option<NodeKey> {
        match self.parent {
            Parent::None => None,
            Parent::Regular(node_key) | Parent::Core(node_key) => Some(node_key)
        }
    }

    pub fn absolute_position(&self, nodes: &SlotMap<NodeKey, RefCell<NodeElement>>) -> Vec2i {
        let mut parent = self.parent();
        let mut position = self.relative_position;
        while let Some(node) = parent.and_then(|node_key| nodes.get(node_key)).map(RefCell::borrow) {
            position += node.relative_position;
            parent = node.parent();
        }
        position
    }

    pub fn bounds(&self, nodes: &SlotMap<NodeKey, RefCell<NodeElement>>) -> IRect {
        IRect::from_pos_size(self.absolute_position(nodes), self.size)
    }

    pub fn width_including_margin(&self) -> i32 {
        self.size.x + self.style.left + self.style.right
    }

    pub fn children<'a>(
        &'a self,
        nodes: &'a SlotMap<NodeKey, RefCell<NodeElement>>
    ) -> impl Iterator<Item=Ref<'a, NodeElement>> {
        self.children.iter().copied().map(|child_key| nodes[child_key].borrow())
    }

    pub fn children_mut<'a>(
        &'a self,
        nodes: &'a SlotMap<NodeKey, RefCell<NodeElement>>
    ) -> impl Iterator<Item=RefMut<'a, NodeElement>> {
        self.children.iter().copied().map(|child_key| nodes[child_key].borrow_mut())
    }
}

macro_rules! children_mut_of {
    ($node:ident, $nodes:ident) => {
        $node.children.iter().copied().map(|child_key| $nodes[child_key].borrow_mut())
    };
}

#[allow(unused)] // Necessary to use macro in same crate.
pub(crate) use children_mut_of;

impl Default for NodeElement {
    fn default() -> Self {
        NodeElement {
            key: NodeKey::null(),
            parent: Parent::None,
            children: NodeChildren::default(),
            style: Style::default(),
            last_provided_width_parameter_for_actual_width: -1,
            is_width_demand_dirty: true,
            is_min_height_dirty: true,
            last_provided_height_parameter_for_actual_height: -1,
            width_demand: WidthDemand::default(),
            width_demand_including_margin: WidthDemand::default(),
            min_height: 0,
            min_height_including_margin: 0,
            content_size: Vec2i::zero(),
            size: Vec2i::zero(),
            is_resize_event_queued: false,
            relative_position: Vec2i::zero(),
            hit_model: HitModel::default(),
            consumes_hover: true,
            inherits_input_state: false,
            is_selected: false,
            core: NodeCore::Null,
            background: EnumSetMap::default(),
            visual_translation: EnumSetMap::default(),
            debug_name: String::new(),
            hover_tag: HoverTag::Unhovered,
            table: None
        }
    }
}

impl fmt::Debug for NodeElement {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, r#"NodeElement(key={}, name="{}")"#, self.key.data().as_ffi(), self.debug_name)
    }
}

pub fn get_node_property<T, F: FnOnce(&NodeElement) -> T>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    error_value: T,
    f: F
) -> T {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded(error_value, || {
        let node = skiatree.nodes.acc(node_key)?;
        Ok(f(&node))
    })
}

pub fn set_node_property<F: FnOnce(&mut NodeElement) -> Result<()>>(skiatree: *mut SkiaTreeLibrary, node_key: u64, f: F) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded_unit(|| {
        let mut node = skiatree.nodes.acc_mut(node_key)?;
        f(&mut node)?;
        drop(node);
        skiatree.propagate_needs_layout(node_key)
    })
}

pub fn get_node_bool_property<F: FnOnce(&NodeElement) -> bool>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    f: F
) -> BoolResult {
    get_node_property(skiatree, node_key, BoolResult::Error, |node| {
        guarded_bool(|| Ok(f(node)))
    })
}

pub fn get_node_ext_property<T: FfiExt, F: FnOnce(&NodeElement) -> T>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    f: F
) -> T {
    get_node_property(skiatree, node_key, T::ERROR_VALUE, f)
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_new(
    skiatree: *mut SkiaTreeLibrary,
) -> u64 {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = skiatree.nodes.insert(RefCell::new(NodeElement::default()));
    skiatree.nodes[node_key].borrow_mut().key = node_key;
    node_key.as_ffi()
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_delete(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded_unit(|| {
        skiatree.detach_node(node_key)?;
        skiatree.nodes.acc_remove(node_key)?;
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_detach(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded_unit(|| {
        skiatree.detach_node(node_key)
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_attach_child(
    skiatree: *mut SkiaTreeLibrary,
    parent_key: u64,
    child_key: u64
) -> UnitResult {
    attach_node(skiatree, parent_key, child_key, |children, child| {
        children.push(child);
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_attach_child_at_position(
    skiatree: *mut SkiaTreeLibrary,
    parent_key: u64,
    child_key: u64,
    position: usize
) -> UnitResult {
    attach_node(skiatree, parent_key, child_key, |children, child| {
        if !(0..=children.len()).contains(&position) {
            bail!("Invalid insertion position {}", position)
        }
        children.insert(position, child);
        Ok(())
    })
}

fn attach_node<F: Fn(&mut NodeChildren, NodeKey) -> Result<()>>(
    skiatree: *mut SkiaTreeLibrary,
    parent_key: u64,
    child_key: u64,
    insert: F
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let parent_key = NodeKey::from_ffi(parent_key);
    let child_key = NodeKey::from_ffi(child_key);
    guarded_unit(|| {
        skiatree.detach_node(child_key)?;
        skiatree.propagate_needs_layout(parent_key)?;
        skiatree.nodes.acc_mut(child_key)?.parent = Parent::Regular(parent_key);
        insert(&mut skiatree.nodes.acc_mut(parent_key)?.children, child_key)
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_get_debug_name(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> *mut c_char {
    get_node_property(skiatree, node_key, null_mut(), |node| {
        node.debug_name.to_ffi_str()
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_debug_name(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    debug_name: *const c_char
) -> UnitResult {
    let debug_name = debug_name.to_str().to_string();
    set_node_property(skiatree, node_key, |node| {
        node.debug_name = debug_name;
        Ok(())
    })
}

pub trait NodesContext {
    fn propagate_needs_layout(&mut self, node_key: NodeKey) -> Result<()>;

    fn detach_node(&mut self, node_key: NodeKey) -> Result<()>;
}

impl NodesContext for SkiaTreeLibrary {
    fn propagate_needs_layout(&mut self, mut node_key: NodeKey) -> Result<()> {
        loop {
            let mut node = self.nodes.acc_mut(node_key)?;
            // All 4 caching parameters are invalidated only here and only outside the layout run,
            // hence we only need to check is_min_height_dirty.
            if node.is_min_height_dirty { break; }
            node.last_provided_width_parameter_for_actual_width = -1;
            node.is_width_demand_dirty = true;
            node.is_min_height_dirty = true;
            node.last_provided_height_parameter_for_actual_height = -1;
            if let Some(key) = node.parent() { node_key = key; } else { break; }
        }
        Ok(())
    }

    fn detach_node(&mut self, node_key: NodeKey) -> Result<()> {
        self.propagate_needs_layout(node_key)?;
        let node = self.nodes.acc(node_key)?;
        match node.parent {
            Parent::Regular(parent_key) => {
                let mut parent = self.nodes.acc_mut(parent_key)?;
                let children = &mut parent.children;
                if let Some(position) = children.iter().position(|&x| x == node_key) {
                    children.remove(position);
                }
            },
            Parent::Core(parent_key) => {
                self.nodes.acc_mut(parent_key)?.core.remove_child(node_key);
            },
            _ => ()
        }
        Ok(())
    }
}
