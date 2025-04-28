package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.abs
import kotlin.math.abs

typealias Metric<T> = (T, T) -> Float

fun absDiff(first: Float, second: Float): Float = abs(first - second)

fun absDiff(first: Angle, second: Angle): Float = abs(first - second).toRadians()

/**
 * Compute `(1.0f - alpha) * first + alpha * second` for coefficient `alpha` with `0.0f <= alpha` and `alpha <= 1.0f`.
 */
typealias LinearInterpolator<T> = (first: T, alpha: Float, second: T) -> T
