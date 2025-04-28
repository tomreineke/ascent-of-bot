use std::f32::consts::PI;
use std::time::Instant;

use anyhow::{anyhow, bail};
use skia_safe::{Color4f, PathEffect, Paint};

use crate::background::Background;
use crate::drawing::InputState;
use crate::error::{guarded, UnitResult};
use crate::geo::Interpolatable;
use crate::library::SkiaTreeLibrary;
use crate::node::{set_node_property, NodeKey};
use crate::slot_maps::{KeyExt, SlotMapExt};

#[derive(Clone, Copy)]
pub enum AnimationDriver {
    Null,
    Linear {
        speed: f32,
        max_value: f32
    },
    Wave {
        frequency: f32,
        phase: f32
    }
}

impl AnimationDriver {
    fn evaluate(&self, time: f32) -> (f32, bool) {
        match *self {
            AnimationDriver::Null => (0.0, false),
            AnimationDriver::Linear { speed, max_value } => {
                let value = speed * time;
                if max_value <= value {
                    (max_value, false)
                } else {
                    (value, true)
                }
            },
            AnimationDriver::Wave { frequency, phase } => {
                let value = ((frequency * time * 2.0 * PI + phase).cos() + 1.0) * 0.5;
                (value, true)
            }
        }
    }
}

#[derive(Clone)]
pub enum AnimationTarget {
    Null,
    DashPhase,
    Opacity,
    ColorInterpolation {
        first_color: Color4f,
        second_color: Color4f,
    }
}

impl AnimationTarget {
    fn update(&self, value: f32, paint: &mut Paint) {
        match *self {
            AnimationTarget::Null => (),
            AnimationTarget::DashPhase => {
                if let Some(mut x) = paint.path_effect().and_then(|e| e.as_a_dash()) {
                    x.phase = value;
                    paint.set_path_effect(PathEffect::dash(&x.intervals, x.phase));
                }
            },
            AnimationTarget::Opacity => {
                paint.set_alpha((value * 256.0).floor().clamp(0.0, 255.0) as u8);
            },
            AnimationTarget::ColorInterpolation { first_color, second_color } => {
                paint.set_color4f(first_color.interpolate(second_color, value), None);
            }
        }
    }
}

#[derive(Clone)]
pub struct BackgroundAnimation {
    driver: AnimationDriver,
    target: AnimationTarget,
    start_time: Instant
}

impl BackgroundAnimation {
    pub fn execute(&self, paint: &mut Paint) -> bool {
        let time = self.start_time.elapsed().as_secs_f32();
        let (value, keep) = self.driver.evaluate(time);
        self.target.update(value, paint);
        keep
    }

    pub fn execute_all(animations: &mut Vec<BackgroundAnimation>, paint: &mut Paint) {
        animations.retain(|animation| animation.execute(paint));
    }
}

impl Default for BackgroundAnimation {
    fn default() -> Self {
        BackgroundAnimation {
            driver: AnimationDriver::Null,
            target: AnimationTarget::Null,
            start_time: Instant::now()
        }
    }
}

#[no_mangle]
pub extern "C" fn skiatree_node_background_attach_animation(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
) -> i32 {
    let skiatree = unsafe { &mut *skiatree };
    let node_key = NodeKey::from_ffi(node_key);
    guarded(-1, || {
        let mut node = skiatree.nodes.acc_mut(node_key)?;
        let background = &mut node.background[input_state.0];
        let animations = background.animations()
            .ok_or_else(|| anyhow!("Cannot add animations to current Background type."))?;
        let index = animations.len();
        animations.push(BackgroundAnimation::default());
        Ok(index as i32)
    })
}

fn access_background_animation<F: Fn(&mut BackgroundAnimation)>(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    animation_index: i32,
    f: F
) -> UnitResult {
    set_node_property(skiatree, node_key, |node| {
        let background = &mut node.background[input_state.0];
        match *background {
            Background::Rect { ref mut animations, .. } => {
                let animation = animations.get_mut(animation_index as usize)
                    .ok_or_else(|| anyhow!("Illegal animation index {}", animation_index))?;
                f(animation);
                Ok(())
            },
            _ => bail!("Cannot add animations to current Background type.")
        }
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_background_animation_set_driver_linear(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    animation_index: i32,
    speed: f32,
    max_value: f32
) -> UnitResult {
    access_background_animation(skiatree, node_key, input_state, animation_index,  |animation| {
        animation.driver = AnimationDriver::Linear { speed, max_value };
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_background_animation_set_driver_wave(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    animation_index: i32,
    frequency: f32,
    phase: f32
) -> UnitResult {
    access_background_animation(skiatree, node_key, input_state, animation_index,  |animation| {
        animation.driver = AnimationDriver::Wave { frequency, phase };
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_background_animation_set_target_dash_phase(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    animation_index: i32
) -> UnitResult {
    access_background_animation(skiatree, node_key, input_state, animation_index,  |animation| {
        animation.target = AnimationTarget::DashPhase;
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_background_animation_set_target_opacity(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    animation_index: i32
) -> UnitResult {
    access_background_animation(skiatree, node_key, input_state, animation_index,  |animation| {
        animation.target = AnimationTarget::Opacity;
    })
}

#[no_mangle]
pub extern "C" fn skiatree_node_background_animation_set_target_color_interpolation(
    skiatree: *mut SkiaTreeLibrary,
    node_key: u64,
    input_state: InputState,
    animation_index: i32,
    first_color: Color4f,
    second_color: Color4f
) -> UnitResult {
    access_background_animation(skiatree, node_key, input_state, animation_index,  |animation| {
        animation.target = AnimationTarget::ColorInterpolation { first_color, second_color };
    })
}
