use skia_safe::textlayout::{ParagraphStyle, TextAlign};

#[no_mangle]
pub extern "C" fn skiatree_paragraph_style_new() -> *mut ParagraphStyle {
    Box::into_raw(Box::new(ParagraphStyle::new()))
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_style_delete(paragraph_style: *mut ParagraphStyle) {
    drop(unsafe { Box::from_raw(paragraph_style) })
}

#[no_mangle]
pub extern "C" fn skiatree_paragraph_style_set_text_align(paragraph_style: *mut ParagraphStyle, text_align: TextAlign) {
    let paragraph_style = unsafe { &mut *paragraph_style };
    paragraph_style.set_text_align(text_align);
}
