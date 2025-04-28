package com.cerebrallychallenged.hypogean.util.math.probability

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.radians

typealias AngleDistribution = ProbabilityDistribution<Angle>

fun normalAngleDistribution(mean: Angle, standardDeviation: Angle): AngleDistribution {
    val base = normalDistribution(mean.value, standardDeviation.value)
    return { random -> base(random).radians }
}