use skia_safe::font_style::{Slant, Weight, Width};
use skia_safe::FontStyle;

#[no_mangle]
pub extern "C" fn skiatree_font_style_new(weight: Weight, width: Width, slant: Slant) -> *mut FontStyle {
    Box::into_raw(Box::new(FontStyle::new(weight, width, slant)))
}

#[no_mangle]
pub extern "C" fn skiatree_font_style_delete(font_style: *mut FontStyle) {
    drop(unsafe { Box::from_raw(font_style) })
}
