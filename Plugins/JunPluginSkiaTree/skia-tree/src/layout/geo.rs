use skia_safe::IRect;

use crate::error::{UnitResult, BoolResult, FfiExt};
use crate::geo::Vec2i;
use crate::library::SkiaTreeLibrary;
use crate::node::{get_node_property, set_node_property, get_node_ext_property, get_node_bool_property, NodeKey};
use crate::slot_maps::{KeyExt, SlotMapExt};

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_left(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.left
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_left(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    left: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.left = left;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_top(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.top
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_top(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    top: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.top = top;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_right(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.right
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_right(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    right: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.right = right;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_bottom(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.bottom
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_bottom(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    bottom: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.bottom = bottom;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_min_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.min_width
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_min_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    min_width: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.min_width = min_width;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_min_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.min_height
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_min_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    min_height: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.min_height = min_height;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_max_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.max_width
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_max_width(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    max_width: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.max_width = max_width;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_max_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.max_height
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_max_height(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    max_height: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.max_height = max_height;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_hgap(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.hgap
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_hgap(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    hgap: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.hgap = hgap;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_vgap(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> i32 {
    get_node_property(skiatree, node_key, i32::MIN, |node| {
        node.style.vgap
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_vgap(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    vgap: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.vgap = vgap;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_translation(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> Vec2i {
    get_node_ext_property(skiatree, node_key, |node| {
        node.style.layout_translation
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_translation(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    translation: Vec2i
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.layout_translation = translation;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_is_scroll_viewport(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> BoolResult {
    get_node_bool_property(skiatree, node_key, |node| {
        node.style.is_scroll_viewport
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_is_scroll_viewport(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    is_scroll_viewport: bool
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.is_scroll_viewport = is_scroll_viewport;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_absolute_position(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> Vec2i {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    if let Ok(node) = skiatree.nodes.acc(node_key) {
        node.absolute_position(&skiatree.nodes)
    } else {
        Vec2i::ERROR_VALUE
    }
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_content_size(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> Vec2i {
    get_node_ext_property(skiatree, node_key, |node| {
        node.content_size
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_size(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> Vec2i {
    get_node_ext_property(skiatree, node_key, |node| {
        node.size
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_bounds(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> IRect {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    if let Ok(node) = skiatree.nodes.acc(node_key) {
        node.bounds(&skiatree.nodes)
    } else {
        IRect::ERROR_VALUE
    }
}
