package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import kotlin.math.abs

fun bresenham(target: Vec2i): Sequence<Vec2i> = sequence {
    val tx = target.x
    val ty = target.y
    val dx = abs(tx)
    val dy = -abs(ty)
    val sx = if (0 < tx) 1 else -1
    val sy = if (0 < ty) 1 else - 1
    var err = dx + dy
    var x = 0
    var y = 0
    while (true) {
        yield(vec(x, y))
        if (x == tx && y == ty) break
        val e2 = 2 * err
        if (e2 > dy) {
            err += dy
            x += sx
        }
        if (e2 < dx) {
            err += dx
            y += sy
        }
    }
}