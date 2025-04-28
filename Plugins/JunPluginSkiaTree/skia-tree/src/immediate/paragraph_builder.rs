use std::cell::RefCell;
use std::ffi::c_char;
use std::rc::Rc;

use skia_safe::textlayout::{FontCollection, ParagraphBuilder, ParagraphStyle, PlaceholderStyle, TextStyle, PlaceholderAlignment, TextBaseline};

use crate::error::{guarded_unit, UnitResult};
use crate::ffi::FfiToStr;
use crate::geo::Vec2i;
use crate::immediate::paragraph::AugmentedParagraph;
use crate::layout::event::LayoutEventCollector;
use crate::library::SkiaTreeLibrary;
use crate::node::NodeKey;
use crate::slot_maps::{KeyExt, SlotMapExt};

pub struct AugmentedParagraphBuilder {
    paragraph_builder: ParagraphBuilder,
    nodes: Vec<NodeKey>
}

impl AugmentedParagraphBuilder {
    fn new(paragraph_style: &ParagraphStyle, font_collection: FontCollection) -> AugmentedParagraphBuilder {
        AugmentedParagraphBuilder {
            paragraph_builder: ParagraphBuilder::new(paragraph_style, font_collection.clone()),
            nodes: vec![]
        }
    }
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_new(
    paragraph_style: *const ParagraphStyle,
    font_collection: *const FontCollection
) -> *mut AugmentedParagraphBuilder {
    let paragraph_style = unsafe { &*paragraph_style };
    let font_collection = unsafe { &*font_collection };
    Box::into_raw(Box::new(AugmentedParagraphBuilder::new(paragraph_style, font_collection.clone())))
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_delete(paragraph_builder: *mut AugmentedParagraphBuilder) {
    drop(unsafe { Box::from_raw(paragraph_builder) })
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_push_style(paragraph_builder: *mut AugmentedParagraphBuilder, style: *const TextStyle) {
    let paragraph_builder = unsafe { &mut *paragraph_builder };
    let style = unsafe { &*style };
    paragraph_builder.paragraph_builder.push_style(style);
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_pop(paragraph_builder: *mut AugmentedParagraphBuilder) {
    let paragraph_builder = unsafe { &mut *paragraph_builder };
    paragraph_builder.paragraph_builder.pop();
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_add_text(paragraph_builder: *mut AugmentedParagraphBuilder, text: *const c_char) {
    let paragraph_builder = unsafe { &mut *paragraph_builder };
    paragraph_builder.paragraph_builder.add_text(text.to_str());
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_add_placeholder(
    skiatree: *mut SkiaTreeLibrary,
    paragraph_builder: *mut AugmentedParagraphBuilder,
    node_key: u64,
    alignment: PlaceholderAlignment,
    baseline: TextBaseline,
    baseline_offset: f32
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    let paragraph_builder = unsafe { &mut *paragraph_builder };
    let node_key = NodeKey::from_ffi(node_key);
    let nodes = &skiatree.nodes;
    guarded_unit(|| {
        let mut node = nodes.acc_mut(node_key)?;
        let mut collector = LayoutEventCollector::new();
        node.update_layout(nodes, Vec2i::new(1000, 1000), &mut collector);
        let placeholder_style = PlaceholderStyle {
            width: node.size.x as f32,
            height: node.size.y as f32,
            alignment,
            baseline,
            baseline_offset
        };
        paragraph_builder.paragraph_builder.add_placeholder(&placeholder_style);
        paragraph_builder.nodes.push(node_key);
        drop(node);
        collector.dispatch(skiatree);
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_builder_build(paragraph_builder: *mut AugmentedParagraphBuilder) -> *const RefCell<AugmentedParagraph> {
    let paragraph_builder = unsafe { &mut *paragraph_builder };
    Rc::into_raw(Rc::new(RefCell::new(AugmentedParagraph::new(
        paragraph_builder.paragraph_builder.build(),
        paragraph_builder.nodes.clone()
    ))))
}
