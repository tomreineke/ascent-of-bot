use std::cell::RefCell;
use std::rc::Rc;

use skia_safe::{Canvas, Point};
use skia_safe::textlayout::Paragraph;

use crate::geo::Vec2i;
use crate::layout::WidthDemand;
use crate::node::NodeKey;

const RELAXED_WIDTH: i32 = 65536;

pub struct AugmentedParagraph {
    pub paragraph: Paragraph,
    relaxed_size: Vec2i,
    last_width: i32,
    pub nodes: Vec<NodeKey>
}

impl AugmentedParagraph {
    pub fn new(
        mut paragraph: Paragraph,
        nodes: Vec<NodeKey>
    ) -> AugmentedParagraph {
        paragraph.layout(RELAXED_WIDTH as f32);
        let relaxed_size = Vec2i::new(
            paragraph.max_intrinsic_width().ceil() as i32,
            paragraph.height().ceil() as i32
        );
        AugmentedParagraph {
            paragraph,
            relaxed_size,
            last_width: RELAXED_WIDTH,
            nodes
        }
    }

    pub fn width_demand(&self) -> WidthDemand {
        WidthDemand {
            min_width: self.paragraph.min_intrinsic_width().ceil() as i32,
            relaxed_width: self.relaxed_size.x
        }
    }

    pub fn min_height(&self) -> i32 {
        self.paragraph.height().ceil() as i32
    }

    pub fn set_actual_width(&mut self, actual_width: i32) {
        if self.last_width != actual_width {
            self.last_width = actual_width;
            self.paragraph.layout(actual_width as f32);
        }
    }
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_delete(paragraph: *const RefCell<AugmentedParagraph>) {
    drop(unsafe { Rc::from_raw(paragraph) })
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_layout(paragraph: *const RefCell<AugmentedParagraph>, width: f32) {
    let paragraph = unsafe { &*paragraph };
    paragraph.borrow_mut().paragraph.layout(width);
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_paint(paragraph: *const RefCell<AugmentedParagraph>, canvas: *mut Canvas, x: f32, y: f32) {
    let paragraph = unsafe { &*paragraph };
    let canvas = unsafe { &mut *canvas };
    paragraph.borrow().paragraph.paint(canvas, Point::new(x, y));
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_get_height(paragraph: *const RefCell<AugmentedParagraph>) -> f32 {
    let paragraph = unsafe { &*paragraph };
    paragraph.borrow().paragraph.height()
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_get_max_width(paragraph: *const RefCell<AugmentedParagraph>) -> f32 {
    let paragraph = unsafe { &*paragraph };
    paragraph.borrow().paragraph.max_width()
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_get_max_intrinsic_width(paragraph: *const RefCell<AugmentedParagraph>) -> f32 {
    let paragraph = unsafe { &*paragraph };
    paragraph.borrow().paragraph.max_intrinsic_width()
}
