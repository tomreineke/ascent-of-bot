use std::rc::Rc;

pub trait RcExt<T> {
    fn clone_raw(ptr: *const T) -> Self;
}

impl<T> RcExt<T> for Rc<T> {
    fn clone_raw(ptr: *const T) -> Self {
        unsafe {
            Rc::increment_strong_count(ptr);
            Rc::from_raw(ptr)
        }
    }
}
