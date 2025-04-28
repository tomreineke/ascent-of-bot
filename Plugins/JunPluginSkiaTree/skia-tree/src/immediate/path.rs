use skia_safe::{Path, Point};

#[no_mangle]
pub extern "C" fn skiatree_path_delete(path: *mut Path) {
    drop(unsafe { Box::from_raw(path) })
}

#[no_mangle]
pub extern "C" fn skiatree_path_new() -> *mut Path {
    Box::into_raw(Box::new(Path::new()))
}

#[no_mangle]
pub extern "C" fn skiatree_path_line_to(path: *mut Path, x: f32, y: f32) {
    let path = unsafe { &mut *path };
    path.line_to(Point::new(x, y));
}

#[no_mangle]
pub extern "C" fn skiatree_path_move_to(path: *mut Path, x: f32, y: f32) {
    let path = unsafe { &mut *path };
    path.move_to(Point::new(x, y));
}
