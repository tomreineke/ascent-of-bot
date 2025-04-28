package com.cerebrallychallenged.jun.util

import com.cerebrallychallenged.jun.math.geo.Vec2d
import javafx.scene.shape.*

class PathBuilder(val path: Path = Path()) {
    private val elements = path.elements.also { it.clear() }

    fun moveTo(position: Vec2d) {
        elements.add(MoveTo(position.x, position.y))
    }

    fun moveTo(x: Double, y: Double) {
        elements.add(MoveTo(x, y))
    }

    fun lineTo(position: Vec2d) {
        elements.add(LineTo(position.x, position.y))
    }

    fun lineTo(x: Double, y: Double) {
        elements.add(LineTo(x, y))
    }

    fun quadTo(control: Vec2d, target: Vec2d) {
        elements.add(QuadCurveTo(control.x, control.y, target.x, target.y))
    }

    fun quadTo(controlX: Double, controlY: Double, targetX: Double, targetY: Double) {
        elements.add(QuadCurveTo(controlX, controlY, targetX, targetY))
    }

    fun cubicTo(
            control1X: Double, control1Y: Double,
            control2X: Double, control2Y: Double,
            targetX: Double, targetY: Double
    ) {
        elements.add(CubicCurveTo(control1X, control1Y, control2X, control2Y, targetX, targetY))
    }
}

fun buildPath(path: Path = Path(), thunk: PathBuilder.() -> Unit): Path = PathBuilder(path).apply(thunk).path