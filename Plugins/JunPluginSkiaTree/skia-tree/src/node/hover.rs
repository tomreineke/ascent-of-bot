use skia_safe::IRect;

use crate::error::{UnitResult, BoolResult};
use crate::input::HitModel;
use crate::library::SkiaTreeLibrary;
use crate::node::{set_node_property, get_node_bool_property};

#[no_mangle]
pub extern "C" fn skiatree_node_set_hit_model_none(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.hit_model = HitModel::None;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_hit_model_rect(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    border: IRect
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.hit_model = HitModel::Rect { border };
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_hit_model_rounded_rect(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    border: IRect,
    corner_radius: i32
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.hit_model = HitModel::RoundedRect { border, corner_radius };
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_consumes_hover(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> BoolResult {
    get_node_bool_property(skiatree, node_key, |node| node.consumes_hover)
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_consumes_hover(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    consumes_hover: bool
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.consumes_hover = consumes_hover;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_inherits_input_state(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> BoolResult {
    get_node_bool_property(skiatree, node_key, |node| node.inherits_input_state)
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_inherits_input_state(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    inherits_input_state: bool
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.inherits_input_state = inherits_input_state;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_get_is_selected(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64
) -> BoolResult {
    get_node_bool_property(skiatree, node_key, |node| node.is_selected)
}

#[no_mangle]
pub extern "C" fn skiatree_node_set_is_selected(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    is_selected: bool
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.is_selected = is_selected;
        Ok(())
    })
}
