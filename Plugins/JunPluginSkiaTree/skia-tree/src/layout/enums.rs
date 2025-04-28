use crate::error::UnitResult;
use crate::library::SkiaTreeLibrary;
use crate::node::{get_node_property, NodeElement, set_node_property};
use crate::style::{Align, Flow, Visibility};

fn get_node_enum_property<T, F: FnOnce(&NodeElement) -> T>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    f: F
) -> i32 {
    get_node_property(skiatree, node_key, -1, |node| {
        let enum_value = f(node);
        let ordinal = unsafe { *(&enum_value as *const T as *const u8) };
        ordinal as i32
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_flow(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
) -> i32 {
    get_node_enum_property(skiatree, node_key, |node| { node.style.flow })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_flow(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    ordinal: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.flow = Flow::from_ordinal(ordinal)?;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_horizontal_align(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
) -> i32 {
    get_node_enum_property(skiatree, node_key, |node| { node.style.horizontal_align })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_horizontal_align(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    ordinal: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.horizontal_align = Align::from_ordinal(ordinal)?;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_vertical_align(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
) -> i32 {
    get_node_enum_property(skiatree, node_key, |node| { node.style.vertical_align })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_vertical_align(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    ordinal: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.vertical_align = Align::from_ordinal(ordinal)?;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_get_visibility(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
) -> i32 {
    get_node_enum_property(skiatree, node_key, |node| { node.style.visibility })
}

#[no_mangle]
pub extern "C" fn skiatree_node_layout_set_visibility(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    ordinal: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.style.visibility = Visibility::from_ordinal(ordinal)?;
        Ok(())
    })
}
