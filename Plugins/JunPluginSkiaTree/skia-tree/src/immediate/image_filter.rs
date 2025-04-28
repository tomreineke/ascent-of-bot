use anyhow::anyhow;
use skia_safe::{ImageFilter, image_filters, BlendMode, Point, ColorFilter};

use crate::error::guarded_box;
use crate::immediate::image::SkiaImage;

#[no_mangle]
pub extern "C" fn skiatree_image_filter_delete(filter: *mut ImageFilter) {
    drop(unsafe { Box::from_raw(filter) })
}

#[no_mangle]
pub extern "C" fn skiatree_image_filter_new_blend(
    blend_mode: BlendMode,
    background: *const ImageFilter,
    foreground: *const ImageFilter
) -> *mut ImageFilter {
    // let background = background { &* outer };
    let background = if background.is_null() {
        None
    } else {
        Some(unsafe { &*background }.clone())
    };
    let foreground = unsafe { &* foreground }.clone();
    guarded_box(|| {
        let image_filter =
            image_filters::blend(blend_mode, background, foreground, None)
                .ok_or_else(|| anyhow!("Cannot create image filter blend"))?;
        Ok(Box::new(image_filter))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_image_filter_new_color_filter(
    input: *const ImageFilter,
    color_filter: *const ColorFilter
) -> *mut ImageFilter {
    let input = unsafe { &* input };
    let color_filter = unsafe { &*color_filter };
    guarded_box(|| {
        let image_filter =
            input.clone().color_filter(None, color_filter.clone())
                .ok_or_else(|| anyhow!("Cannot create image filter from color filter"))?;
        Ok(Box::new(image_filter))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_image_filter_new_compose(
    outer: *const ImageFilter,
    inner: *const ImageFilter
) -> *mut ImageFilter {
    let outer = unsafe { &* outer };
    let inner = unsafe { &* inner };
    guarded_box(|| {
        let image_filter =
            ImageFilter::compose(outer, inner).ok_or_else(|| anyhow!("Cannot create image filter compose"))?;
        Ok(Box::new(image_filter))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_image_filter_new_image(
    image: *const SkiaImage,
) -> *mut ImageFilter {
    let image = unsafe { &* image };
    guarded_box(|| {
        let image_filter  =
            image_filters::image(image.image().clone(), None, None, None)
                .ok_or_else(|| anyhow!("Cannot create image filter from image"))?;
        Ok(Box::new(image_filter))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_image_filter_new_offset(
    input: *const ImageFilter,
    dx: f32,
    dy: f32
) -> *mut ImageFilter {
    let input = unsafe { &* input };
    guarded_box(|| {
        let image_filter =
            input.clone().offset(None, Point::new(dx, dy))
                .ok_or_else(|| anyhow!("Cannot create image filter from offset"))?;
        Ok(Box::new(image_filter))
    })
}