package com.cerebrallychallenged.hypogean

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.FLOAT_PI
import com.cerebrallychallenged.jun.math.floorMod
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec

// [-PI/4; PI/4[    -> [0.0; 1.0[ -> 0 -> NORTH_WEST
// [PI/4; 3*PI/4[   -> [1.0; 2.0[ -> 1 -> NORTH_EAST
// [3*PI/4; 5*PI/4[ -> [2.0; 3.0[ -> 2 -> SOUTH_EAST
// [5*PI/4; 7*PI/4[ -> [3.0; 4.0[ -> 3 -> SOUTH_WEST
fun Angle.roundToHeading(): Heading =
        Heading.values()[(this.value / FLOAT_PI * 2.0f + 0.5f).floorMod(4.0f).toInt()]



enum class Heading(val delta: Vec2i, val angle: Angle, private val rightAngleDistances: IntArray) {
    NORTH_WEST(vec(1, 0), Angle.DEGREE_0, intArrayOf(0, 1, 2, 1)),
    NORTH_EAST(vec(0, 1), Angle.DEGREE_90, intArrayOf(1, 0, 1, 2)),
    SOUTH_EAST(vec(-1, 0), Angle.DEGREE_180, intArrayOf(2, 1, 0, 1)),
    SOUTH_WEST(vec(0, -1), Angle.DEGREE_270, intArrayOf(1, 2, 1, 0));

    companion object {
        val deltas = values().map { it.delta }
    }

    val rotation = Quaternion.fromNormalAxisAngle(Vec3f.UNIT_Z, angle)

    val rotationTransform: Transform3f = Transform3f.rotation(rotation)

    fun opposite(): Heading = when(this) {
        NORTH_WEST -> SOUTH_EAST
        NORTH_EAST -> SOUTH_WEST
        SOUTH_EAST -> NORTH_WEST
        SOUTH_WEST -> NORTH_EAST
    }

    /**
     * Angle between this and the other heading given by integral multiples of the right angle, i.e.,
     * 0 means same heading.
     * 1 means right angle, 90°.
     * 2 means 180°.
     * @param other the other heading.
     * @return angle between this and the other heading given by integral multiples of the right angle.
     */
    fun rightAngleDistance(other: Heading): Int = rightAngleDistances[other.ordinal]

    fun transform(v: Vec2i): Vec2i = when (this) {
        NORTH_WEST -> v
        NORTH_EAST -> vec(-v.y, v.x)
        SOUTH_EAST -> -v
        SOUTH_WEST -> vec(v.y, -v.x)
    }

    fun flipX(): Heading = when (this) {
        NORTH_WEST, SOUTH_EAST -> this
        NORTH_EAST -> SOUTH_WEST
        SOUTH_WEST -> NORTH_EAST
    }
}

fun Vec2i.headingTo(other: Vec2i): Heading {
    val cmpX = this.x.compareTo(other.x)
    val cmpY = this.y.compareTo(other.y)
    return when {
        cmpY == 0 && cmpX < 0 -> Heading.NORTH_WEST
        cmpY == 0 && cmpX > 0 -> Heading.SOUTH_EAST
        cmpX == 0 && cmpY < 0 -> Heading.NORTH_EAST
        cmpX == 0 && cmpY > 0 -> Heading.SOUTH_WEST
        else -> throw IllegalArgumentException("No defined heading from $this to $other")
    }
}
