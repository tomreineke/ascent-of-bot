use std::ffi::c_char;
use skia_safe::textlayout::TypefaceFontProvider;
use skia_safe::Typeface;

use crate::ffi::FfiToOptStr;

#[no_mangle]
pub extern "C" fn skiatree_typeface_font_provider_new() -> *mut TypefaceFontProvider {
    Box::into_raw(Box::new(TypefaceFontProvider::new()))
}

#[no_mangle]
pub extern "C" fn skiatree_typeface_font_provider_delete(typeface_font_provider: *mut TypefaceFontProvider) {
    drop(unsafe { Box::from_raw(typeface_font_provider) })
}


#[no_mangle]
pub extern "C" fn skiatree_typeface_font_provider_register_typeface(
    typeface_font_provider: *mut TypefaceFontProvider,
    typeface: *const Typeface,
    alias: *const c_char
) {
    let typeface_font_provider = unsafe { &mut *typeface_font_provider };
    let typeface = unsafe { &*typeface };
    typeface_font_provider.register_typeface(typeface.clone(), alias.to_opt_str());
}
