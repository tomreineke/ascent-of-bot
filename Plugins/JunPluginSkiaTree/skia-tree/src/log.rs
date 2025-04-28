use std::cell::RefCell;
use std::ffi::CString;
use tracing::instrument;

thread_local! {
    static LOG_FN: RefCell<Option<extern "C" fn(*const std::os::raw::c_char)>> = RefCell::new(None);
}

#[allow(unused)]
macro_rules! log {
    ($($arg:tt)*) => {{
        $crate::log::log_string(format!("{}", format_args!($($arg)*)));
    }};
}

pub fn log_string(message: String) {
    println!("{}", message);
    let message = CString::new(message).unwrap_or_else(|_| CString::new("value").unwrap());
    LOG_FN.with_borrow_mut(|log_fn| {
        if let Some(f) = *log_fn {
            f(message.as_ptr());
        }
    });
}

#[no_mangle]
#[instrument]
pub extern "C" fn skiatree_set_log_fn(f: extern "C" fn(*const std::os::raw::c_char)) {
    LOG_FN.replace(Some(f));
}

#[allow(unused)] // Necessary to use macro in same crate.
pub(crate) use log;
