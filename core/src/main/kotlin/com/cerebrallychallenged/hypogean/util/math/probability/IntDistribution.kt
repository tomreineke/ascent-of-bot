package com.cerebrallychallenged.hypogean.util.math.probability

typealias IntDistribution = ProbabilityDistribution<Int>

operator fun IntDistribution.plus(other: IntDistribution): IntDistribution
        = jointDistribution(this, other) { x, y -> x + y }

operator fun IntDistribution.plus(other: Int): IntDistribution = map { it + other }

operator fun Int.plus(other: IntDistribution): IntDistribution = other + this

operator fun IntDistribution.minus(other: IntDistribution): IntDistribution
        = jointDistribution(this, other) { x, y -> x - y }

operator fun IntDistribution.minus(other: Int): IntDistribution = map { it - other }

operator fun Int.minus(other: IntDistribution): IntDistribution = other.map { this - it }

operator fun IntDistribution.times(other: IntDistribution): IntDistribution
        = jointDistribution(this, other) { x, y -> x * y }

operator fun IntDistribution.times(other: Int): IntDistribution = map { it * other }

operator fun Int.times(other: IntDistribution): IntDistribution = other * this

fun uniformDistribution(range: IntRange): IntDistribution
        = { random -> random.nextInt(range.first, range.last + 1) }
