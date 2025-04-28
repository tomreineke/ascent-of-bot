use std::slice::from_raw_parts;

use anyhow::format_err;
use skia_safe::PathEffect;

use crate::error::guarded_box;

#[no_mangle]
pub extern "C" fn skiatree_path_effect_delete(path_effect: *mut PathEffect) {
    drop(unsafe { Box::from_raw(path_effect) })
}


#[no_mangle]
pub extern "C" fn skiatree_path_effect_new_dash_pattern(
    dash_ptr: *const f32,
    dash_count: u32,
    phase: f32
) -> *mut PathEffect {
    let dashes = unsafe { from_raw_parts(dash_ptr, dash_count as usize) };
    guarded_box(|| {
        let path_effect =
            PathEffect::dash(dashes, phase)
                .ok_or_else(|| format_err!("Could not create path effect for dash pattern"))?;
        Ok(Box::new(path_effect))
    })
}
