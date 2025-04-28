use std::ffi::c_char;
use std::rc::Rc;

use anyhow::format_err;
use skia_safe::{CubicResampler, Data, FilterMode, Image, IPoint, MipmapMode, Paint, Rect, SamplingOptions, Surface};

use crate::error::guarded_rc;
use crate::ffi::FfiToStr;

pub enum SkiaImage {
    Buffered(Image, Vec<u8>),
    SelfContained(Image)
}

impl SkiaImage {
    pub fn image(&self) -> &Image {
        match *self {
            SkiaImage::Buffered(ref image, _) => image,
            SkiaImage::SelfContained(ref image) => image
        }
    }
}

#[no_mangle]
pub extern "C" fn skiatree_image_load(file_path: *const c_char, size: *mut IPoint) -> *const SkiaImage {
    let size = unsafe { &mut *size };
    let file_path = file_path.to_str();
    guarded_rc(|| {
        let bytes = std::fs::read(file_path)?;
        let im = Image::from_encoded(unsafe { Data::new_bytes(&bytes) })
            .ok_or_else(|| format_err!(r#"Could not load image from "{}""#, file_path))?;
        size.x = im.width();
        size.y = im.height();
        Ok(Rc::new(SkiaImage::Buffered(im, bytes)))
    })
}

#[no_mangle]
pub extern "C" fn skiatree_image_delete(image: *const SkiaImage) {
    drop(unsafe { Rc::from_raw(image) });
}


#[no_mangle]
pub extern "C" fn skiatree_image_resize(image: *const SkiaImage, new_width: i32, new_height: i32) -> *const SkiaImage {
    let image = unsafe { &*image };
    guarded_rc(|| {
        let mut surface = Surface::new_raster_n32_premul((new_width, new_height))
            .ok_or_else(|| format_err!("Could not create surface for rescaling to {}x{}", new_width, new_height))?;
        let mut paint = Paint::default();
        paint.set_anti_alias(true);
        paint.set_dither(false);
        surface.canvas().draw_image_rect_with_sampling_options(
            image.image(),
            None,
            &Rect::new(0.0, 0.0, new_width as f32, new_height as f32),
            SamplingOptions {
                max_aniso: 0,
                use_cubic: true,
                cubic: CubicResampler::mitchell(),
                filter: FilterMode::Linear,
                mipmap: MipmapMode::Linear
            },
            &paint
        );
        surface.flush_and_submit();
        Ok(Rc::new( SkiaImage::SelfContained(surface.image_snapshot())))
    })
}
