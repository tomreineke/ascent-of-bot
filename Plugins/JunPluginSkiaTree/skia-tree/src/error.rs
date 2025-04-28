use std::cell::RefCell;
use std::ffi::CString;
use std::os::raw::c_char;
use std::ptr::{null, null_mut};
use std::rc::Rc;

use anyhow::Result;
use skia_safe::IRect;
use slotmap::Key;
use tracing::instrument;

use crate::geo::Vec2i;

thread_local! {
    pub static LAST_ERROR: RefCell<Option<CString>> = RefCell::new(None);
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_last_error() -> *const c_char {
    LAST_ERROR.with(|last_error| {
        let last_error = last_error.borrow();
        if let Some(error_message) = last_error.as_ref() {
            error_message.as_ptr()
        } else {
            null()
        }
    })
}

pub fn guarded<T, F: FnOnce() -> Result<T>>(error_value: T, block: F) -> T {
    match block() {
        Ok(value) => value,
        Err(e) => {
            LAST_ERROR.with(|last_error| {
                last_error.replace(
                    CString::new(e.to_string()).or_else(|_| CString::new("error message contains <NUL>")).ok()
                );
            });
            error_value
        }
    }
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_reset_last_error() {
    LAST_ERROR.with(|last_error| {
        last_error.replace(None);
    })
}

#[repr(u8)]
pub enum UnitResult {
    Success = 0,
    Error = 255
}

pub fn guarded_unit<F: FnOnce() -> Result<()>>(block: F) -> UnitResult {
    guarded(UnitResult::Error, || {
        block()?;
        Ok(UnitResult::Success)
    })
}


#[repr(u8)]
pub enum BoolResult {
    False = 0,
    True = 1,
    Error = 255
}

impl From<bool> for BoolResult {
    fn from(value: bool) -> Self {
        if value {
            BoolResult::True
        } else {
            BoolResult::False
        }
    }
}

pub fn guarded_bool<F: FnOnce() -> Result<bool>>(block: F) -> BoolResult {
    guarded(BoolResult::Error, || {
        Ok(block()?.into())
    })
}

pub const ERROR_CODE: usize = usize::MAX;

pub fn guarded_index<F: FnOnce() -> Result<usize>>(block: F) -> usize {
    guarded(ERROR_CODE, || {
        block()
    })
}

pub fn guarded_key<K: Key, F: FnOnce() -> Result<K>>(block: F) -> u64 {
    guarded(K::null(), || {
        block()
    }).data().as_ffi()
}

pub fn guarded_box<T, F: FnOnce() -> Result<Box<T>>>(block: F) -> *mut T {
    guarded(null_mut(), || {
        Ok(Box::into_raw(block()?))
    })
}

pub fn guarded_rc<T, F: FnOnce() -> Result<Rc<T>>>(block: F) -> *const T {
    guarded(null(), || {
        Ok(Rc::into_raw(block()?))
    })
}

pub trait FfiExt {
    const ERROR_VALUE: Self;

    fn guarded<F: FnOnce() -> Result<Self>>(block: F) -> Self where Self: Sized {
        guarded(Self::ERROR_VALUE, block)
    }
}

impl FfiExt for Vec2i {
    const ERROR_VALUE: Self = Vec2i::new(i32::MIN, 0);
}

impl FfiExt for IRect {
    const ERROR_VALUE: Self = IRect::new(i32::MIN, 0, 0, 0);
}
