use std::rc::Rc;

use anyhow::Result;
use skia_safe::{Canvas, Color4f, FilterMode, IRect, Paint, PaintStyle, Rect, Path};
use tracing::instrument;

use crate::background::animation::BackgroundAnimation;
use crate::drawing::InputState;
use crate::error::UnitResult;
use crate::geo::{RectExt, Vec2i};
use crate::immediate::image::SkiaImage;
use crate::library::SkiaTreeLibrary;
use crate::node::set_node_property;
use crate::rc_util::RcExt;

pub mod animation;

#[repr(C)]
#[derive(Clone)]
pub enum Background {
    Empty,
    Rect {
        first_paint: Paint,
        second_paint: Option<Paint>,
        corner_radius: f32,
        overshoot: IRect,
        animations: Vec<BackgroundAnimation>
    },
    Wireframe(Color4f),
    NinePatch {
        image: Rc<SkiaImage>,
        overshoot: IRect,
        center: IRect,
        paint: Option<Paint>,
        animations: Vec<BackgroundAnimation>
    },
    Image {
        image: Rc<SkiaImage>,
        overshoot: IRect,
        paint: Option<Paint>,
        animations: Vec<BackgroundAnimation>
    },
    Path {
        path: Path,
        paint: Paint,
        animations: Vec<BackgroundAnimation>
    }
}

impl Background {
    pub fn draw_on(&mut self, canvas: &mut Canvas, size: Vec2i) -> Result<()> {
        let bounds = Rect {
            left: 0.0,
            top: 0.0,
            right: size.x as f32,
            bottom: size.y as f32
        };
        match *self {
            Background::Empty => {},
            Background::Rect {
                ref mut first_paint,
                ref second_paint,
                corner_radius,
                overshoot,
                ref mut animations
            } => {
                BackgroundAnimation::execute_all(animations, first_paint);
                let effective_rect = bounds.offset_rect(overshoot);
                let mut draw = |paint: &Paint| {
                    if corner_radius == 0.0 {
                        canvas.draw_rect(effective_rect, paint);
                    } else {
                        canvas.draw_round_rect(effective_rect, corner_radius, corner_radius, paint);
                    }
                };
                draw(first_paint);
                if let Some(second_paint) = second_paint.as_ref() {
                    draw(second_paint);
                }
            },
            Background::Wireframe(color) => {
                let mut paint = Paint::new(color, None);
                paint.set_style(PaintStyle::Stroke);
                canvas.draw_rect(bounds, &paint);
                canvas.draw_line(bounds.left_top(), bounds.right_bottom(), &paint);
                canvas.draw_line(bounds.left_bottom(), bounds.right_top(), &paint);
            },
            Background::NinePatch {
                ref image,
                overshoot,
                center,
                ref mut paint,
                ref mut animations
            } => {
                if let Some(paint) = paint {
                    BackgroundAnimation::execute_all(animations, paint);
                }
                let effective_rect = bounds.offset_rect(overshoot);
                canvas.draw_image_nine(
                    image.image(),
                    center,
                    effective_rect,
                    FilterMode::Linear,
                    paint.as_ref()
                );
            },
            Background::Image {
                ref image,
                overshoot,
                ref mut paint,
                ref mut animations
            } => {
                if let Some(paint) = paint {
                    BackgroundAnimation::execute_all(animations, paint);
                }
                let effective_rect = bounds.offset_rect(overshoot);
                canvas.draw_image(image.image(), effective_rect.left_top(), paint.as_ref());
            },
            Background::Path {
                ref path,
                ref mut paint ,
                ref mut animations
            } => {
                BackgroundAnimation::execute_all(animations, paint);
                canvas.draw_path(path, paint);
            }
        }
        Ok(())
    }

    pub fn animations(&mut self) -> Option<&mut Vec<BackgroundAnimation>> {
        match *self {
            Background::Rect { ref mut animations, .. } => Some(animations),
            Background::NinePatch { ref mut animations, .. } => Some(animations),
            Background::Image { ref mut animations, .. } => Some(animations),
            Background::Path { ref mut animations, .. } => Some(animations),
            _ => None
        }
    }
}

impl Default for Background {
    fn default() -> Self {
        Background::Empty
    }
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_background_empty(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState
) -> UnitResult {
    set_background(skiatree, node_key, input_state, Background::Empty)
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_background_rect(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    first_paint: *mut Paint,
    second_paint: *mut Paint,
    corner_radius: f32,
    overshoot: IRect
) -> UnitResult {
    let first_paint = unsafe { &*first_paint }.clone();
    let second_paint = if second_paint.is_null() { None } else { Some(unsafe { &*second_paint }.clone()) };
    set_background(
        skiatree,
        node_key,
        input_state,
        Background::Rect { first_paint, second_paint, corner_radius, overshoot, animations: vec![] }
    )
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_background_wireframe(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    color: Color4f
) -> UnitResult {
    set_background(skiatree, node_key, input_state, Background::Wireframe(color))
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_background_ninepatch(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    image: *const SkiaImage,
    overshoot: IRect,
    center: IRect,
    paint: *const Paint
) -> UnitResult {
    let image = Rc::clone_raw(image);
    let paint = if paint.is_null() {
        None
    } else {
        Some(unsafe { &*paint }.clone())
    };
    set_background(
        skiatree,
        node_key,
        input_state,
        Background::NinePatch { image, overshoot, center, paint, animations: vec![] }
    )
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_background_image(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    image: *const SkiaImage,
    overshoot: IRect,
    paint: *const Paint
) -> UnitResult {
    let image = Rc::clone_raw(image);
    let paint = if paint.is_null() {
        None
    } else {
        Some(unsafe { &*paint }.clone())
    };
    set_background(
        skiatree,
        node_key,
        input_state,
        Background::Image { image, overshoot, paint, animations: vec![] }
    )
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_set_background_path(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    path: *const Path,
    paint: *const Paint
) -> UnitResult {
    let path = unsafe { &*path }.clone();
    let paint = unsafe { &*paint }.clone();
    set_background(
        skiatree,
        node_key,
        input_state,
        Background::Path { path, paint, animations: vec![] }
    )
}

fn set_background(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    background: Background
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    set_node_property(skiatree, node_key, |node| {
        node.background.put(input_state.0, background);
        Ok(())
    })
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_node_remove_background(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState
) -> UnitResult {
    let skiatree = unsafe { &mut *skiatree };
    set_node_property(skiatree, node_key, |node| {
        node.background.remove(input_state.0);
        Ok(())
    })
}
