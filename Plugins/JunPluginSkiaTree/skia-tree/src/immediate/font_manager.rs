use std::ffi::c_char;
use anyhow::format_err;
use skia_safe::{FontMgr, Typeface};

use crate::error::guarded_box;
use crate::ffi::FfiToStr;

#[no_mangle]
pub extern "C" fn skiatree_font_manager_new() -> *mut FontMgr {
    Box::into_raw(Box::new(FontMgr::new()))
}

#[no_mangle]
pub extern "C" fn skiatree_font_manager_delete(font_manager: *mut FontMgr) {
    drop(unsafe { Box::from_raw(font_manager) })
}

#[no_mangle]
pub extern "C" fn skiatree_font_manager_load(font_manager: *mut FontMgr, file_path: *const c_char) -> *mut Typeface {
    let font_manager = unsafe { &mut *font_manager };
    let file_path: &str = file_path.to_str();
    guarded_box(|| {
        let data = std::fs::read(file_path)?;
        Ok(Box::new(font_manager.new_from_data(&data, None).ok_or_else(|| format_err!("Error loading font {}", file_path))?))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_font_manager_family_names(font_manager: *const FontMgr) -> *mut Vec<String> {
    let font_manager = unsafe { &*font_manager };
    Box::into_raw(Box::new(font_manager.family_names().collect()))
}
