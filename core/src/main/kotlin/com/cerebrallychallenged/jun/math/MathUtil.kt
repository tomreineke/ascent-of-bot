package com.cerebrallychallenged.jun.math

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.truncate

const val FLOAT_PI = kotlin.math.PI.toFloat()

fun Int.floorDiv(divisor: Int): Int = Math.floorDiv(this, divisor)

fun Int.floorMod(divisor: Int): Int = Math.floorMod(this, divisor)

fun Double.floorMod(divisor: Double): Double = this - divisor * floor(this / divisor)

fun Float.floorMod(divisor: Float): Float = this - divisor * floor(this / divisor)

@Suppress("NOTHING_TO_INLINE")
inline fun clamp(value: Int, lo: Int, hi: Int): Int = value.coerceIn(lo, hi)

@Suppress("NOTHING_TO_INLINE")
inline fun clamp(value: Double, lo: Double, hi: Double): Double = value.coerceIn(lo, hi)

@Suppress("NOTHING_TO_INLINE")
inline fun clamp(value: Float, lo: Float, hi: Float): Float = value.coerceIn(lo, hi)

fun Double.floorToInt(): Int = floor(this).toInt()

fun Float.floorToInt(): Int = floor(this).toInt()

fun Double.ceilToInt(): Int = ceil(this).toInt()

fun Float.ceilToInt(): Int = ceil(this).toInt()

fun Double.truncateToInt(): Int = truncate(this).toInt()

fun Float.truncateToInt(): Int = truncate(this).toInt()

/**
 * Rounds this towards the target, i.e., upwards if target is greater and downwards if target is smaller.
 * @param target the target.
 * @return this rounded towards the target.
 */
fun Double.roundTowards(target: Double): Int = if (this < target) ceilToInt() else floorToInt()

/**
 * Rounds this towards the target, i.e., upwards if target is greater and downwards if target is smaller.
 * @param target the target.
 * @return this rounded towards the target.
 */
fun Float.roundTowards(target: Float): Int = if (this < target) ceilToInt() else floorToInt()

fun interpolate(first: Double, alpha: Double, second: Double): Double = (1.0 - alpha) * first + alpha * second

fun interpolate(first: Float, alpha: Float, second: Float): Float = (1.0f - alpha) * first + alpha * second

fun interpolate(first: Angle, alpha: Float, second: Angle): Angle = (1.0f - alpha) * first + alpha * second

fun Int.ceilToPowerOfTwo(): Int {
    var v = this
    // https://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
    --v
    v = v or (v shr 1)
    v = v or (v shr 2)
    v = v or (v shr 4)
    v = v or (v shr 8)
    v = v or (v shr 16)
    ++v
    return v
}

val Int.isEven: Boolean
    get() = this and 1 == 0

val Int.isOdd: Boolean
    get() = this and 1 == 1
