use skia_safe::textlayout::{PlaceholderAlignment, PlaceholderStyle, TextBaseline};

#[no_mangle]
pub extern "C" fn skiatree_placeholder_style_new(
    width: f32,
    height: f32,
    alignment: PlaceholderAlignment,
    baseline: TextBaseline,
    offset: f32
) -> *mut PlaceholderStyle {
    Box::into_raw(Box::new(PlaceholderStyle::new(width, height, alignment, baseline, offset)))
}

#[no_mangle]
pub extern "C" fn skiatree_placeholder_style_delete(placeholder_style: *mut PlaceholderStyle) {
    drop(unsafe { Box::from_raw(placeholder_style) })
}
