package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.curve.Polyline

class Animation(private val polyline: Polyline<Vec2f>) {
    private var time: Float = 0.0f

    val start: Vec2f = polyline.startPoint

    val current: Vec2f
        get() = polyline(time)

    fun animate(deltaSeconds: Float): Boolean {
        val anyChange = time < polyline.endTime
        time += deltaSeconds
        return anyChange
    }
}
