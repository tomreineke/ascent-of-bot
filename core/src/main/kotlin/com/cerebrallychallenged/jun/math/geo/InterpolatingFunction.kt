package com.cerebrallychallenged.jun.math.geo

import java.util.*

/**
 * A function whose values are explicitly given only at certain points.
 * For inputs in between these points, the result is interpolated.
 * The function is constant for inputs outside the interval spanned by these points.
 *
 * Formally, if this function f is explicitly defined for x_1 <= x_2 <= ... <= x_n, then
 * f(x) = (1.0 - (x - x_i) / (x_{i + 1} - x_i)) * f(x_i) + (x - x_i) / (x_{i + 1} - x_i) * f(x_{i + 1}) if x_i < x < x_{i + 1},
 * f(x) = f(x_1) if x < x_1, and
 * f(x) = f(x_n) if x > x_n.
 * @param <R> type of the image of this function.
 */
class InterpolatingFunction<R>(
        val points: NavigableMap<Float, R>,
        val interpolator: LinearInterpolator<R>
) : (Float) -> R {
    override fun invoke(t: Float): R {
        return points.getOrElse(t) {
            val floorEntry = points.floorEntry(t)
            val ceilingEntry = points.ceilingEntry(t)
            when {
                floorEntry == null -> ceilingEntry.value
                ceilingEntry == null -> floorEntry.value
                else -> {
                    val floorKey = floorEntry.key
                    val alpha = (t - floorKey) / (ceilingEntry.key - floorKey)
                    interpolator(floorEntry.value, alpha, ceilingEntry.value)
                }
            }
        }
    }
}
