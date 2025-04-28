package com.cerebrallychallenged.hypogean.util.math.probability

import kotlin.random.Random
import kotlin.random.asJavaRandom

typealias FloatDistribution = ProbabilityDistribution<Float>

operator fun FloatDistribution.plus(other: FloatDistribution): FloatDistribution
        = jointDistribution(this, other) { x, y -> x + y }

operator fun FloatDistribution.plus(other: Float): FloatDistribution = map { it + other }

operator fun Float.plus(other: FloatDistribution): FloatDistribution = other + this

operator fun FloatDistribution.minus(other: FloatDistribution): FloatDistribution
        = jointDistribution(this, other) { x, y -> x - y }

operator fun FloatDistribution.minus(other: Float): FloatDistribution = map { it - other }

operator fun Float.minus(other: FloatDistribution): FloatDistribution = other.map { this - it }

operator fun FloatDistribution.times(other: FloatDistribution): FloatDistribution
        = jointDistribution(this, other) { x, y -> x * y }

operator fun FloatDistribution.times(other: Float): FloatDistribution = map { it * other }

operator fun Float.times(other: FloatDistribution): FloatDistribution = other * this

fun uniformDistribution(range: ClosedFloatingPointRange<Float>): FloatDistribution
        = { random -> range.start + random.nextFloat() * (range.endInclusive - range.start) }

fun standardNormalDistribution(random: Random): Float {
    return random.asJavaRandom().nextGaussian().toFloat()
}

fun normalDistribution(mean: Float = 0.0f, standardDeviation: Float = 1.0f): FloatDistribution
        = { random -> standardNormalDistribution(random) * standardDeviation + mean }
