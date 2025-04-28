package com.cerebrallychallenged.jun.math.geo

import javafx.geometry.Point2D

fun Vec2f.Companion.from(point: Point2D): Vec2f = vec(point.x.toFloat(), point.y.toFloat())
fun Vec2d.Companion.from(point: Point2D): Vec2d = vec(point.x, point.y)
fun Vec2r.Companion.from(point: Point2D): Vec2r = vec(point.x.toRational(), point.y.toRational())
