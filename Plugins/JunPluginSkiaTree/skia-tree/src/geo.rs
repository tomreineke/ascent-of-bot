use nalgebra::Vector2;
use skia_safe::{IRect, Point, Rect, IPoint, Color4f};
use skia_safe::textlayout::Paragraph;

pub type Vec2f = Vector2<f32>;
pub type Vec2i = Vector2<i32>;

pub trait ParagraphExt {
    fn exact_width(&self) -> f32;
}

impl ParagraphExt for Paragraph {
    fn exact_width(&self) -> f32 {
        self.max_width().min(self.max_intrinsic_width())
    }
}

pub trait SkiaConvertible {
    type SkiaType;

    fn to_skia(self) -> Self::SkiaType;
}

impl SkiaConvertible for Vec2i {
    type SkiaType = Point;

    fn to_skia(self) -> Self::SkiaType {
        Point::new(self.x as f32, self.y as f32)
    }
}

pub trait RectExt {
    type Point;

    fn left_top(&self) -> Self::Point;

    fn left_bottom(&self) -> Self::Point;

    fn right_top(&self) -> Self::Point;

    fn right_bottom(&self) -> Self::Point;

    fn offset_rect(self, offset: IRect) -> Self;

    fn translate(self, offset: Vec2i) -> Self;

    fn contains_point(&self, point: Vec2i) -> bool;

    fn to_float(self) -> Rect;
}

impl RectExt for Rect {
    type Point = Point;

    fn left_top(&self) -> Point {
        Point::new(self.left, self.top)
    }

    fn left_bottom(&self) -> Point {
        Point::new(self.left, self.bottom)
    }

    fn right_top(&self) -> Point {
        Point::new(self.right, self.top)
    }

    fn right_bottom(&self) -> Point {
        Point::new(self.right, self.bottom)
    }

    fn offset_rect(mut self, offset: IRect) -> Self {
        self.left -= offset.left as f32;
        self.top -= offset.top as f32;
        self.right += offset.right as f32;
        self.bottom += offset.bottom as f32;
        self
    }

    fn translate(mut self, offset: Vec2i) -> Self {
        self.left += offset.x as f32;
        self.top += offset.y as f32;
        self.right += offset.x as f32;
        self.bottom += offset.y as f32;
        self
    }

    fn contains_point(&self, point: Vec2i) -> bool {
        (self.left..self.right).contains(&(point.x as f32))
        && (self.top..self.bottom).contains(&(point.y as f32))
    }

    fn to_float(self) -> Rect { self }
}

impl RectExt for IRect {
    type Point = IPoint;

    fn left_top(&self) -> IPoint {
        IPoint::new(self.left, self.top)
    }

    fn left_bottom(&self) -> IPoint {
        IPoint::new(self.left, self.bottom)
    }

    fn right_top(&self) -> IPoint {
        IPoint::new(self.right, self.top)
    }

    fn right_bottom(&self) -> IPoint {
        IPoint::new(self.right, self.bottom)
    }

    fn offset_rect(mut self, offset: IRect) -> Self {
        self.left -= offset.left;
        self.top -= offset.top;
        self.right += offset.right;
        self.bottom += offset.bottom;
        self
    }

    fn translate(mut self, offset: Vec2i) -> Self {
        self.left += offset.x;
        self.top += offset.y;
        self.right += offset.x;
        self.bottom += offset.y;
        self
    }

    fn contains_point(&self, point: Vec2i) -> bool {
        (self.left..self.right).contains(&point.x)
        && (self.top..self.bottom).contains(&point.y)
    }

    fn to_float(self) -> Rect {
        Rect { left: self.left as f32, top: self.top as f32, right: self.right as f32, bottom: self.bottom as f32 }
    }
}

pub trait IRectExt {
    fn from_pos_size(pos: Vec2i, size: Vec2i) -> Self;

    fn intersects(&self, other: &Self) -> bool;
}

impl IRectExt for IRect {
    fn from_pos_size(pos: Vec2i, size: Vec2i) -> Self {
        IRect::from_xywh(pos.x, pos.y, size.x, size.y)
    }

    fn intersects(&self, other: &Self) -> bool {
        self.left <= other.right && other.left <= self.right && self.top <= other.bottom && other.top <= self.bottom
    }
}

pub trait Interpolatable {
    fn interpolate(self, other: Self, t: f32) -> Self;
}

impl Interpolatable for f32 {
    fn interpolate(self, other: Self, t: f32) -> Self {
        self * (1.0 - t) + other * t
    }
}

impl Interpolatable for Color4f {
    fn interpolate(self, other: Self, t: f32) -> Self {
        Color4f {
            r: self.r.interpolate(other.r, t),
            g: self.g.interpolate(other.g, t),
            b: self.b.interpolate(other.b, t),
            a: self.a.interpolate(other.a, t)
        }
    }
}
