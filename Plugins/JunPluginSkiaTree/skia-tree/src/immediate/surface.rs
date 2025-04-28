use anyhow::{bail, format_err};
use skia_safe::{Budgeted, Canvas, ImageInfo, IPoint, Surface};

use crate::error::{guarded_box, guarded_unit, UnitResult};
use crate::library::SkiaTreeLibrary;

#[no_mangle]
pub extern "C" fn skiatree_surface_new(
    skiatree: *mut SkiaTreeLibrary,
    width: i32,
    height: i32
) -> *mut Surface {
    let skiatree = unsafe { &mut *skiatree };
    guarded_box(|| {
        let surface = Surface::new_render_target(
            &mut skiatree.recording_context,
            Budgeted::No,
            &ImageInfo::new_n32_premul((width, height), None),
            None,
            None,
            None,
            None
        ).ok_or_else(|| format_err!("Could not create render target"))?;
        Ok(Box::new(surface))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_surface_delete(surface: *mut Surface) {
    drop(unsafe { Box::from_raw(surface) });
}

#[no_mangle]
pub extern "C" fn skiatree_surface_get_canvas(surface: *mut Surface) -> *mut Canvas {
    let surface = unsafe { &mut *surface };
    surface.canvas() as *mut Canvas
}

#[no_mangle]
pub extern "C" fn skiatree_surface_flush_and_submit(surface: *mut Surface) {
    let surface = unsafe { &mut *surface };
    surface.flush_and_submit();
}

#[no_mangle]
pub extern "C" fn skiatree_surface_read_pixels(surface: *mut Surface, buffer_ptr: *mut u8, buffer_size: usize) -> UnitResult {
    let surface = unsafe { &mut *surface };
    guarded_unit(|| {
        let image_info = surface.image_info();
        let expected_size = (image_info.width() * image_info.height() * 4) as usize;
        if expected_size != buffer_size {
            bail!(
                "Buffer size is {}, but should be {} * {} * 4 = {}",
                buffer_size, image_info.width(), image_info.height(), expected_size
            )
        }
        let buffer_slice = unsafe { std::slice::from_raw_parts_mut(buffer_ptr, buffer_size) };
        surface.read_pixels(
            &image_info,
            buffer_slice,
            (image_info.width() * 4) as usize,
            IPoint::new(0, 0)
        );
        Ok(())
    })
}
