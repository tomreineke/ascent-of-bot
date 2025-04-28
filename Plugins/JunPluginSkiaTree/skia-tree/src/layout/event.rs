use crate::library::SkiaTreeLibrary;
use crate::log::log;
use crate::node::{NodeElement, NodeKey, NodeKeyExt};
use crate::slot_maps::KeyExt;

pub struct LayoutEventCollector {
    resized_nodes: Vec<u64>
}

impl LayoutEventCollector {
    pub fn new() -> LayoutEventCollector {
        LayoutEventCollector {
            resized_nodes: vec![]
        }
    }

    pub fn on_resize(&mut self, node: &mut NodeElement) {
        if !node.is_resize_event_queued {
            node.is_resize_event_queued = true;
            self.resized_nodes.push(node.key.as_ffi());
        }
    }

    pub fn dispatch(&mut self, library: &SkiaTreeLibrary) {
        if !self.resized_nodes.is_empty() {
            let nodes = &library.nodes;
            for &node_key in &self.resized_nodes {
                nodes[NodeKey::from_ffi(node_key)].borrow_mut().is_resize_event_queued = false;
            }
            (library.input_upcalls.resized)(self.resized_nodes.as_ptr(), self.resized_nodes.len());
        }
        self.resized_nodes.clear();
    }
}
