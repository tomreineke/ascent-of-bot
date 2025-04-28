use anyhow::anyhow;
use skia_safe::{ColorFilter, Color4f, color_filters};

use crate::error::guarded_box;

#[no_mangle]
pub extern "C" fn skiatree_color_filter_delete(filter: *mut ColorFilter) {
    drop(unsafe { Box::from_raw(filter) })
}

#[no_mangle]
pub extern "C" fn skiatree_color_filter_new_lighting(
    mul: Color4f,
    add: Color4f
) -> *mut ColorFilter {
    guarded_box(|| {
        let color_filter  = ColorFilter::new_lighting(
            mul.to_color(),
            add.to_color()
        ).ok_or_else(|| anyhow!("Cannot create color filter lighting"))?;
        Ok(Box::new(color_filter))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_color_filter_new_compose(
    outer: *const ColorFilter,
    inner: *const ColorFilter
) -> *mut ColorFilter {
    let outer = unsafe { &* outer };
    let inner = unsafe { &* inner };
    guarded_box(|| {
        let color_filter =
            outer.composed(inner).ok_or_else(|| anyhow!("Cannot create color filter compose"))?;
        Ok(Box::new(color_filter))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_color_filter_new_matrix(
    matrix: *const [f32; 20]
) -> *mut ColorFilter {
    let matrix = unsafe { &*matrix };
    guarded_box(|| {
        Ok(Box::new(color_filters::matrix_row_major(&matrix)))
    })
}
