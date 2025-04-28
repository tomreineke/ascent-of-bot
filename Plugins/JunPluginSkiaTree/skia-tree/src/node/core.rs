use std::cell::{RefCell, Ref};
use std::iter::Copied;
use std::ops::Deref;
use std::rc::Rc;
use std::slice::Iter;

use slotmap::SlotMap;

use crate::error::UnitResult;
use crate::geo::Vec2i;
use crate::immediate::paragraph::AugmentedParagraph;
use crate::layout::WidthDemand;
use crate::library::SkiaTreeLibrary;
use crate::node::{NodeKey, set_node_property, NodeElement, Parent, get_node_property};
use crate::rc_util::RcExt;
use crate::slot_maps::KeyExt;

pub enum NodeCore {
    Null,
    Paragraph(Rc<RefCell<AugmentedParagraph>>),
}

impl NodeCore {
    pub fn width_demand(&self) -> WidthDemand {
        match *self {
            NodeCore::Null => WidthDemand { min_width: 0, relaxed_width: 0 },
            NodeCore::Paragraph(ref paragraph) =>
                RefCell::borrow(paragraph).width_demand(),
        }
    }

    pub fn min_height(&self) -> i32 {
        match *self {
            NodeCore::Null => 0,
            NodeCore::Paragraph(ref paragraph) =>
                RefCell::borrow(paragraph).min_height(),
        }
    }

    pub fn set_actual_width(
        &mut self,
        nodes: &SlotMap<NodeKey, RefCell<NodeElement>>,
        actual_width: i32
    ) {
        match *self {
            NodeCore::Null => (),
            NodeCore::Paragraph(ref paragraph) => {
                let mut paragraph = RefCell::borrow_mut(paragraph);
                paragraph.set_actual_width(actual_width);
                let rects = paragraph.paragraph.get_rects_for_placeholders();
                for (&child_key, text_box) in paragraph.nodes.iter().zip(rects.iter()) {
                    nodes[child_key].borrow_mut().relative_position = Vec2i::new(
                        text_box.rect.left.floor() as i32,
                        text_box.rect.top.floor() as i32
                    );
                }
            },
        }
    }

    pub fn children(&self) -> CoreChildren {
        match *self {
            NodeCore::Null => CoreChildren::Empty,
            NodeCore::Paragraph(ref paragraph) =>
                CoreChildren::Ref(Ref::map(RefCell::borrow(paragraph), |p| &p.nodes)),
        }
    }

    pub fn remove_child(&mut self, _child_key: NodeKey) {}
}

pub enum CoreChildren<'a> {
    Empty,
    Single(NodeKey),
    Ref(Ref<'a, Vec<NodeKey>>)
}

impl<'a> Deref for CoreChildren<'a> {
    type Target = [NodeKey];
    fn deref(&self) -> &Self::Target {
        match *self {
            CoreChildren::Empty => &[],
            CoreChildren::Single(ref node) => std::slice::from_ref(node),
            CoreChildren::Ref(ref nodes) => nodes
        }
    }
}

impl<'a> IntoIterator for &'a CoreChildren<'a> {
    type Item = NodeKey;

    type IntoIter = Copied<Iter<'a, NodeKey>>;

    fn into_iter(self) -> Self::IntoIter {
        self.deref().iter().copied()
    }
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_core_null(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> UnitResult {
    reset_parent_of_detached_core_children(skiatree, node_key);
    set_node_property(skiatree, node_key, |node| {
        node.core = NodeCore::Null;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_core_paragraph(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    paragraph: *const RefCell<AugmentedParagraph>
) -> UnitResult {
    let paragraph = Rc::clone_raw(paragraph);
    reset_parent_of_detached_core_children(skiatree, node_key);
    set_parent_of_attached_core_children(skiatree, node_key, &paragraph.borrow().nodes);
    set_node_property(skiatree, node_key, |node| {
        node.core = NodeCore::Paragraph(paragraph);
        Ok(())
    })
}

fn set_parent_of_attached_core_children(skiatree: *mut SkiaTreeLibrary, node_key: u64, children: &[NodeKey]) {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    for &child_key in children {
        let mut child = skiatree.nodes[child_key].borrow_mut();
        child.parent = Parent::Core(node_key);
    }
}

fn reset_parent_of_detached_core_children(skiatree: *mut SkiaTreeLibrary, node_key: u64) {
    let core_children = get_node_property(skiatree, node_key, None, |node| {
        Some(match node.core {
            NodeCore::Null => vec![],
            NodeCore::Paragraph(ref paragraph) => paragraph.borrow().nodes.clone()
        })
    });
    if let Some(core_children) = core_children {
        let skiatree = unsafe { &mut *skiatree };
        for child_key in core_children {
            skiatree.nodes[child_key].borrow_mut().parent = Parent::None;
        }
    }
}
