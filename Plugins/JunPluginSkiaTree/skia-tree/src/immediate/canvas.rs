use skia_safe::{Canvas, Color4f, FilterMode, IRect, Paint, Point, Rect};

use crate::immediate::image::SkiaImage;

#[no_mangle]
pub extern "C" fn skiatree_canvas_clear(canvas: *mut Canvas, color: Color4f) {
    let canvas = unsafe { &mut *canvas };
    canvas.clear(color);
}

#[no_mangle]
pub extern "C" fn skiatree_canvas_draw_circle(canvas: *mut Canvas, center: Point, radius: f32, paint: *const Paint) {
    let canvas = unsafe { &mut *canvas };
    let paint = unsafe { &*paint };
    canvas.draw_circle(center, radius, paint);
}

#[no_mangle]
pub extern "C" fn skiatree_canvas_draw_image(
    canvas: *mut Canvas,
    image: *const SkiaImage,
    left: f32,
    top: f32,
    paint: *const Paint
) {
    let canvas = unsafe { &mut *canvas };
    let image = unsafe { &*image };
    let paint = if paint.is_null() {
        None
    } else {
        Some(unsafe { &*paint })
    };
    canvas.draw_image(image.image(), (left, top), paint);
}

#[no_mangle]
pub extern "C" fn skiatree_canvas_draw_image_nine(
    canvas: *mut Canvas,
    image: *const SkiaImage,
    center: IRect,
    dst: IRect,
    filter_mode: FilterMode,
    paint: *const Paint
) {
    let canvas = unsafe { &mut *canvas };
    let image = unsafe { &*image };
    let paint = if paint.is_null() {
        None
    } else {
        Some(unsafe { &*paint })
    };
    canvas.draw_image_nine(image.image(), center, Rect::from_irect(dst), filter_mode, paint);
}

#[no_mangle]
pub extern "C" fn skiatree_canvas_draw_rect(canvas: *mut Canvas, rect: IRect, paint: *const Paint) {
    let canvas = unsafe { &mut *canvas };
    let paint = unsafe { &*paint };
    canvas.draw_rect(Rect::from_irect(rect), paint);
}
