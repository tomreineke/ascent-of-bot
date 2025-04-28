use std::ffi::c_char;

use skia_safe::Typeface;

use crate::ffi::ToFfiStr;

#[no_mangle]
pub extern "C" fn skiatree_typeface_delete(typeface: *mut Typeface) {
    drop(unsafe { Box::from_raw(typeface) })
}

#[no_mangle]
pub extern "C" fn skiatree_typeface_get_family_name(typeface: *const Typeface) -> *mut c_char {
    let typeface = unsafe { &* typeface };
    typeface.family_name().to_ffi_str()
}
