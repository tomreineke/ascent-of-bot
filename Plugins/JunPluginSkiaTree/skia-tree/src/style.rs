use anyhow::{bail, Result};
use num::Zero;

use crate::geo::Vec2i;

#[derive(Clone, Copy, PartialEq, Eq)]
pub enum Flow {
    None,
    LeftToRight,
    Vertical,
    LeftToRightThenTopToBottom,
    LeftToRightThenBottomToTop,
    Table
}

impl Flow {
    pub fn from_ordinal(ordinal: i32) -> Result<Flow> {
        Ok(match ordinal {
            0 => Flow::None,
            1 => Flow::LeftToRight,
            2 => Flow::Vertical,
            3 => Flow::LeftToRightThenTopToBottom,
            4 => Flow::LeftToRightThenBottomToTop,
            5 => Flow::Table,
            i => bail!("Unknown Flow enum ordinal {}", i)
        })
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
pub enum Align {
    Min,
    Center,
    Max,
    Stretch
}

impl Align {
    pub fn compute_offset(&self, parent_size: i32, child_size: i32) -> i32 {
        match *self {
            Align::Min | Align::Stretch => 0,
            Align::Center => (parent_size - child_size) / 2,
            Align::Max => parent_size - child_size
        }
    }

    pub fn from_ordinal(ordinal: i32) -> Result<Align> {
        Ok(match ordinal {
            0 => Align::Min,
            1 => Align::Center,
            2 => Align::Max,
            3 => Align::Stretch,
            i => bail!("Unknown Align enum ordinal {}", i)
        })
    }
}

#[derive(Clone, Copy, PartialEq, Eq, Debug)]
pub enum Visibility {
    Visible,
    Hidden,
    Collapsed
}

impl Visibility {
    pub fn from_ordinal(ordinal: i32) -> Result<Visibility> {
        Ok(match ordinal {
            0 => Visibility::Visible,
            1 => Visibility::Hidden,
            2 => Visibility::Collapsed,
            i => bail!("Unknown Visibility enum ordinal {}", i)
        })
    }
}

pub struct Style {
    pub left: i32,
    pub top: i32,
    pub right: i32,
    pub bottom: i32,
    pub min_width: i32,
    pub min_height: i32,
    pub max_width: i32,
    pub max_height: i32,
    pub flow: Flow,
    pub horizontal_align: Align,
    pub vertical_align: Align,
    pub hgap: i32,
    pub vgap: i32,
    pub layout_translation: Vec2i,
    pub is_scroll_viewport: bool,
    pub visibility: Visibility,
}

pub const LARGE_SIZE: i32 = 10_000_000;

impl Default for Style {
    fn default() -> Self {
        Style {
            left: 0,
            top: 0,
            right: 0,
            bottom: 0,
            min_width: 0,
            min_height: 0,
            max_width: LARGE_SIZE,
            max_height: LARGE_SIZE,
            flow: Flow::None,
            horizontal_align: Align::Min,
            vertical_align: Align::Min,
            hgap: 0,
            vgap: 0,
            layout_translation: Vec2i::zero(),
            is_scroll_viewport: false,
            visibility: Visibility::Visible,
        }
    }
}
