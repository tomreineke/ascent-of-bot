use std::ffi::{c_char, CStr, CString};
use std::ptr::null;
use tracing::instrument;

pub trait FfiToStr {
    fn to_str<'a>(self) -> &'a str;
}

impl FfiToStr for *const c_char {
    fn to_str<'a>(self) -> &'a str {
        unsafe { CStr::from_ptr(self) } .to_str().unwrap()
    }
}

pub trait FfiToOptStr {
    fn to_opt_str<'a>(self) -> Option<&'a str>;
}

impl FfiToOptStr for *const c_char {
    fn to_opt_str<'a>(self) -> Option<&'a str> {
        if self.is_null() {
            None
        } else {
            Some(self.to_str())
        }
    }
}

pub trait ToFfiStr {
    fn to_ffi_str(self) -> *mut c_char;
}

impl<'a> ToFfiStr for &'a str {
    fn to_ffi_str(self) -> *mut c_char {
        CString::new(self).unwrap().into_raw()
    }
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_string_free(ptr: *mut c_char) {
    drop(unsafe { CString::from_raw(ptr) });
}

pub trait FfiSlice<T> {
    fn as_ptr_or_null(&self) -> *const T;
}

impl<T> FfiSlice<T> for Vec<T> {
    fn as_ptr_or_null(&self) -> *const T {
        if self.is_empty() {
            null()
        } else {
            self.as_ptr()
        }
    }
}
