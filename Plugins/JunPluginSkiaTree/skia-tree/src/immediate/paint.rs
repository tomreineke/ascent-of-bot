use skia_safe::{Color4f, Paint, PaintStyle, ColorFilter, ImageFilter, Shader, PathEffect};

#[no_mangle]
pub extern "C" fn skiatree_paint_new() -> *mut Paint {
    Box::into_raw(Box::new(Paint::default()))
}

#[no_mangle]
pub extern "C" fn skiatree_paint_delete(paint: *mut Paint) {
    drop(unsafe { Box::from_raw(paint) })
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_anti_alias(paint: *mut Paint, anti_alias: bool) {
    let paint = unsafe { &mut *paint};
    paint.set_anti_alias(anti_alias);
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_color(paint: *mut Paint, color: Color4f) {
    let paint = unsafe { &mut *paint};
    paint.set_color4f(color, None);
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_color_filter(paint: *mut Paint, color_filter: *mut ColorFilter) {
    let paint = unsafe { &mut *paint};
    if color_filter.is_null() {
        paint.set_color_filter(None);
    } else {
        paint.set_color_filter(Some(unsafe { &*color_filter }.clone()));
    }
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_image_filter(paint: *mut Paint, image_filter: *mut ImageFilter) {
    let paint = unsafe { &mut *paint};
    if image_filter.is_null() {
        paint.set_image_filter(None);
    } else {
        paint.set_image_filter(Some(unsafe { &*image_filter }.clone()));
    }
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_path_effect(paint: *mut Paint, path_effect: *mut PathEffect) {
    let paint = unsafe { &mut *paint};
    if path_effect.is_null() {
        paint.set_path_effect(None);
    } else {
        paint.set_path_effect(Some(unsafe { &*path_effect }.clone()));
    }
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_shader(paint: *mut Paint, shader: *mut Shader) {
    let paint = unsafe { &mut *paint};
    if shader.is_null() {
        paint.set_shader(None);
    } else {
        paint.set_shader(Some(unsafe { &*shader }.clone()));
    }
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_stroke_width(paint: *mut Paint, stroke_width: f32) {
    let paint = unsafe { &mut *paint};
    paint.set_stroke_width(stroke_width);
}

#[no_mangle]
pub extern "C" fn skiatree_paint_set_style(paint: *mut Paint, paint_style: PaintStyle) {
    let paint = unsafe { &mut *paint};
    paint.set_style(paint_style);
}
