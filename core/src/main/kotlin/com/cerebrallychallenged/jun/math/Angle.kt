package com.cerebrallychallenged.jun.math

import java.io.DataInput
import java.io.DataOutput
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

@JvmInline
value class Angle(val value: Float) : Comparable<Angle> {
    companion object {
        val ZERO: Angle = 0.0f.radians

        val DEGREE_0: Angle = ZERO

        val DEGREE_90: Angle = (FLOAT_PI * 0.5f).radians

        val DEGREE_180: Angle = FLOAT_PI.radians

        val DEGREE_270: Angle = (FLOAT_PI * 1.5f).radians

        val DEGREE_360: Angle = (FLOAT_PI * 2.0f).radians

        val DEGREE_MINUS_180: Angle = -FLOAT_PI.radians
    }

    /**
     * Returns the difference between this and the other angle within the interval from -PI to PI.
     * @param other the other angle.
     * @return difference between the angles.
     */
    fun diff(other: Angle): Angle = (this - other + DEGREE_180).floorMod(DEGREE_360) - DEGREE_180

    operator fun unaryMinus(): Angle = Angle(-value)

    operator fun plus(other: Angle): Angle = Angle(value + other.value)

    operator fun minus(other: Angle): Angle = Angle(value - other.value)

    operator fun times(factor: Int): Angle = Angle(value * factor)

    operator fun times(factor: Float): Angle = Angle(value * factor)

    operator fun div(divisor: Int): Angle = Angle(value / divisor)

    operator fun div(divisor: Float): Angle = Angle(value / divisor)

    operator fun div(divisor: Angle): Float = value / divisor.value

    fun floorMod(other: Angle): Angle = Angle(value.floorMod(other.value))

    fun toRadians(): Float = value

    fun toDegrees(): Float = Math.toDegrees(value.toDouble()).toFloat()

    override fun compareTo(other: Angle): Int = value.compareTo(other.value)
}

operator fun Int.times(angle: Angle): Angle = angle * this

operator fun Float.times(angle: Angle): Angle = angle * this

val Float.radians: Angle
    get() = Angle(this)

val Float.degrees: Angle
    get() = Angle(Math.toRadians(this.toDouble()).toFloat())

val Number.degrees: Angle
    get() = toFloat().degrees

fun clamp(value: Angle, lo: Angle, hi: Angle): Angle = clamp(value.value, lo.value, hi.value).radians

fun cos(angle: Angle): Float = cos(angle.value)

fun sin(angle: Angle): Float = sin(angle.value)

fun tan(angle: Angle): Float = tan(angle.value)

fun abs(angle: Angle): Angle = Angle(abs(angle.value))

fun DataOutput.writeAngle(angle: Angle) = writeFloat(angle.value)

fun DataInput.readAngle(): Angle = readFloat().radians
