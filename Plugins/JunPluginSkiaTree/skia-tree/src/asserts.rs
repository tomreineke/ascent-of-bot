use skia_safe::{FilterMode, PaintStyle};
use skia_safe::font_style::{FontStyle, Slant};
use skia_safe::textlayout::{PlaceholderAlignment, TextAlign, TextBaseline};
use static_assertions::assert_eq_size;

use crate::drawing::InputState;

assert_eq_size!(FilterMode, i32);
assert_eq_size!(FontStyle, i32);
assert_eq_size!(PaintStyle, i8);
assert_eq_size!(PlaceholderAlignment, i32);
assert_eq_size!(Slant, i32);
assert_eq_size!(TextAlign, i32);
assert_eq_size!(TextBaseline, i32);

assert_eq_size!(InputState, i8);
