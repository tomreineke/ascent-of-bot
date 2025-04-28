use std::ffi::c_char;

use skia_safe::FontMgr;
use skia_safe::textlayout::FontCollection;

use crate::ffi::FfiToOptStr;

#[no_mangle]
pub extern "C" fn skiatree_font_collection_new() -> *mut FontCollection {
    Box::into_raw(Box::new(FontCollection::new()))
}

#[no_mangle]
pub extern "C" fn skiatree_font_collection_delete(font_collection: *mut FontCollection) {
    drop(unsafe { Box::from_raw(font_collection) })
}

#[no_mangle]
pub extern "C" fn skiatree_font_collection_set_default_font_manager(
    font_collection: *mut FontCollection,
    font_manager: *const FontMgr,
    default_family_name: *const c_char,
) {
    let font_collection = unsafe { &mut *font_collection };
    let font_manager = if font_manager.is_null() {
        None
    } else {
        Some(unsafe { &*font_manager }.clone())
    };
    font_collection.set_default_font_manager(font_manager, default_family_name.to_opt_str());
}

#[no_mangle]
pub extern "C" fn skiatree_font_collection_set_asset_font_manager(
    font_collection: *mut FontCollection,
    font_manager: *const FontMgr
) {
    let font_collection = unsafe { &mut *font_collection };
    let font_manager = if font_manager.is_null() {
        None
    } else {
        Some(unsafe { &*font_manager }.clone())
    };
    font_collection.set_asset_font_manager(font_manager);
}
