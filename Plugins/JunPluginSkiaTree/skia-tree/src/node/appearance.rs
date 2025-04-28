use crate::drawing::InputState;
use crate::error::UnitResult;
use crate::geo::Vec2i;
use crate::library::SkiaTreeLibrary;
use crate::node::set_node_property;

#[no_mangle]
pub extern "C" fn skiatree_node_set_visual_translation(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    translation: Vec2i
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.visual_translation[input_state.0] = translation;
        Ok(())
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_remove_visual_translation(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        node.visual_translation.remove(input_state.0);
        Ok(())
    })
}
