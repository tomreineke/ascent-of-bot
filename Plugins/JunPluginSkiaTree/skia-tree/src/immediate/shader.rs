use std::cell::RefCell;
use std::rc::Rc;
use std::slice::from_raw_parts;

use anyhow::format_err;
use skia_safe::{Color4f, Point, Shader, TileMode};
use skia_safe::gradient_shader::GradientShaderColors;

use crate::error::guarded_box;

#[no_mangle]
pub extern "C" fn skiatree_shader_delete(shader: *const RefCell<Shader>) {
    drop(unsafe { Rc::from_raw(shader) })
}

#[no_mangle]
pub extern "C" fn skiatree_shader_new_linear_gradient(
    point0_x: f32,
    point0_y: f32,
    point1_x: f32,
    point1_y: f32,
    colors_ptr: *const Color4f,
    pos_ptr: *const f32,
    count: i32,
    mode: TileMode
) -> *mut Shader {
    let colors = GradientShaderColors::ColorsInSpace(
        unsafe { from_raw_parts(colors_ptr, count as usize) },
        None
    );
    let pos = if pos_ptr.is_null() {
        None
    } else {
        Some(unsafe { from_raw_parts(pos_ptr, count as usize) })
    };
    guarded_box(|| {
        let shader =
            Shader::linear_gradient(
                (Point::new(point0_x, point0_y), Point::new(point1_x, point1_y)),
                colors,
                pos,
                mode,
                None,
                None
            ).ok_or_else(|| format_err!("Could not create shader for linear gradient"))?;
        Ok(Box::new(shader))
    })
}
