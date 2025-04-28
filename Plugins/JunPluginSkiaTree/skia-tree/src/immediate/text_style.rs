use skia_safe::{Color4f, FontStyle, Paint, Color, Point};
use skia_safe::textlayout::{TextBaseline, TextStyle, TextShadow};

#[no_mangle]
pub extern "C" fn skiatree_text_style_new() -> *mut TextStyle {
    Box::into_raw(Box::new(TextStyle::new()))
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_clone(text_style: *const TextStyle) -> *mut TextStyle {
    let text_style = unsafe { &*text_style };
    Box::into_raw(Box::new(text_style.clone()))
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_delete(text_style: *mut TextStyle) {
    drop(unsafe { Box::from_raw(text_style) })
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_get_background_paint(text_style: *const TextStyle) -> *mut Paint {
    let text_style = unsafe { &*text_style };
    Box::into_raw(Box::new(text_style.background().clone()))
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_background_paint(text_style: *mut TextStyle, paint: *const Paint) {
    let text_style = unsafe { &mut *text_style };
    let paint = unsafe { &*paint };
    text_style.set_background_paint(paint);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_get_foreground_paint(text_style: *const TextStyle) -> *mut Paint {
    let text_style = unsafe { &*text_style };
    Box::into_raw(Box::new(text_style.foreground().clone()))
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_foreground_paint(text_style: *mut TextStyle, paint: *const Paint) {
    let text_style = unsafe { &mut *text_style };
    let paint = unsafe { &*paint };
    text_style.set_foreground_paint(paint);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_color(text_style: *mut TextStyle, color: Color4f) {
    let text_style = unsafe { &mut *text_style };
    text_style.set_color(color.to_color());
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_text_baseline(text_style: *mut TextStyle, text_baseline: TextBaseline) {
    let text_style = unsafe { &mut *text_style };
    text_style.set_text_baseline(text_baseline);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_baseline_shift(text_style: *mut TextStyle, baseline_shift: f32) {
    let text_style = unsafe { &mut *text_style };
    text_style.set_baseline_shift(baseline_shift);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_font_families(text_style: *mut TextStyle, font_families: *const Vec<String>) {
    let text_style = unsafe { &mut *text_style };
    let font_families = unsafe { &*font_families };
    text_style.set_font_families(font_families);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_font_size(text_style: *mut TextStyle, size: f32) {
    let text_style = unsafe { &mut *text_style };
    text_style.set_font_size(size);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_set_font_style(text_style: *mut TextStyle, font_style: FontStyle) {
    let text_style = unsafe { &mut *text_style };
    text_style.set_font_style(font_style);
}

#[no_mangle]
pub extern "C" fn skiatree_text_style_add_outline_shadows(text_style: *mut TextStyle, color: Color4f, amount: f32) {
    let text_style = unsafe { &mut *text_style };
    let color: Color = color.to_color();
    for x in [-amount, amount] {
        for y in [-amount, amount] {
            text_style.add_shadow(TextShadow {
                color,
                offset: Point::new(x, y),
                ..Default::default()
            });
        }
    }
}
