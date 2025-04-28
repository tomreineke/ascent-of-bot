package com.cerebrallychallenged.hypogean.accuracy

import com.cerebrallychallenged.hypogean.util.math.probability.AngleDistribution
import com.cerebrallychallenged.hypogean.util.math.probability.normalAngleDistribution
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.FLOAT_PI
import com.cerebrallychallenged.jun.math.clamp
import com.cerebrallychallenged.jun.math.radians
import org.apache.commons.math3.special.Erf.erf
import kotlin.random.Random

private val BASE_ANGLE_DEVIATION = 0.125f.radians

fun main() {
    val random = Random(1013)
    for (i in 0..100) {
        val accuracy = i * 0.01f
        println("${"%.05f".format(accuracy)}:\t${f(accuracy, random)}\tvs.\t${g(accuracy)}")
    }
}


fun angleDistribution(intendedAngle: Angle, accuracy: Float): AngleDistribution {
    val base = normalAngleDistribution(Angle.ZERO, BASE_ANGLE_DEVIATION / accuracy)
    return { random -> intendedAngle + clamp(base(random), Angle.DEGREE_MINUS_180, Angle.DEGREE_180) }
}

private fun f(accuracy: Float, random: Random): Float {
    val distribution = angleDistribution(Angle.ZERO, accuracy)
    val times = 100000
    var count = 0
    repeat(times) {
        val angle = distribution(random).value
        if (-FLOAT_PI * 0.25f <= angle && angle <= FLOAT_PI * 0.25f) {
            ++count
        }
    }
    return count.toFloat() / times
}

private fun g(accuracy: Float): Float {
    return erf(4.44288 * accuracy).toFloat()
}