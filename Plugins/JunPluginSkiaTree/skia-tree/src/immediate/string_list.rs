use std::ffi::c_char;

use crate::ffi::{FfiToStr, ToFfiStr};

#[no_mangle]
pub extern "C" fn skiatree_string_list_new() -> *mut Vec<String> {
    Box::into_raw(Box::new(vec![]))
}

#[no_mangle]
pub extern "C" fn skiatree_string_list_delete(string_list: *mut Vec<String>) {
    drop(unsafe { Box::from_raw(string_list) })
}

#[no_mangle]
pub extern "C" fn skiatree_string_list_len(string_list: *const Vec<String>) -> usize {
    unsafe { &*string_list }.len()
}

#[no_mangle]
pub extern "C" fn skiatree_string_list_get(string_list: *const Vec<String>, index: usize) -> *mut c_char {
    let string_list = unsafe { &*string_list };
    string_list[index].to_ffi_str()
}

#[no_mangle]
pub extern "C" fn skiatree_string_list_add(string_list: *mut Vec<String>, string: *const c_char) {
    let string_list = unsafe { &mut *string_list };
    string_list.push(string.to_str().to_string());
}
