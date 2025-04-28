package com.cerebrallychallenged.hypogean.vanilla

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.util.math.probability.AngleDistribution
import com.cerebrallychallenged.hypogean.util.math.probability.normalAngleDistribution
import com.cerebrallychallenged.hypogean.util.math.probability.normalDistribution
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.clamp
import com.cerebrallychallenged.jun.math.radians
import com.cerebrallychallenged.jun.util.product
import org.apache.commons.math3.special.Erf.erf

private const val BASE_DISTANCE_DEVIATION = 0.025f

private val BASE_ANGLE_DEVIATION = 0.025f.radians

interface ActionWithAccuracy : Action {

    /**
     * Base accuracy
     */
    val accuracy: Float
        get() = 1.0f
}

/**
 * Base accuracy of the actor or weapon.
 */
var Entity.accuracy: Float by attribute(1.0f)

/**
 * The entity (equipped item or status effect) modifies the accuracy of its bearer by that factor.
 */
var Entity.accuracyFactor: Float by attribute(1.0f)

fun ActionWithAccuracy.computeAccuracy(actor: Actor, tool: Item): Float {
    val providers = actor.statusEffects.asSequence() + tool.statusEffects.asSequence() + actor.equippedItems
    val factors = providers.map { it.accuracyFactor }.product()
    return accuracy * actor.accuracy * tool.accuracy * factors
}

fun angleDistribution(intendedAngle: Angle, accuracy: Float): AngleDistribution {
    val base = normalAngleDistribution(Angle.ZERO, BASE_ANGLE_DEVIATION / accuracy)
    return { random -> intendedAngle + clamp(base(random), Angle.DEGREE_MINUS_180, Angle.DEGREE_180) }
}

fun distanceDistribution(intendedDistance: Float, accuracy: Float)
        = normalDistribution(intendedDistance, BASE_DISTANCE_DEVIATION / accuracy)

/**
 * Computes the probability for hitting the neighboring cell with the specified accuracy.
 * It equals the probability of the angle deviating by at most `PI / 4` to each side.
 * For simplicity, we assume that an 1x1 actor tries to hit an 1x1 actor.
 *
 *
 * https://www.wolframalpha.com/input/?i=integrate+1%2Fsqrt%282+*+pi+*+%280.125+%2F+t%29%5E2%29+*+exp%28-x%5E2%2F%282+*+%280.125+%2F+t%29%5E2%29%29+dx+from+x%3D-pi%2F4+to+pi%2F4
 */
fun pointBlankRangeProbability(accuracy: Float): Float = erf(4.44288 * accuracy).toFloat()
