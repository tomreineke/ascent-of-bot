package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Rounds
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.StatusEffectCompanion
import com.cerebrallychallenged.hypogean.model.StatusEffectInitializationHelper
import com.cerebrallychallenged.hypogean.model.attribute.attribute

var Entity.causedStatusEffects: CausedStatusEffects by attribute(CausedStatusEffects())

class CausedStatusEffects(vararg val effects: StatusEffectWithIntensityAndDuration)

infix fun Int.of(statusEffectCompanion: StatusEffectCompanion): StatusEffectWithIntensity =
    StatusEffectWithIntensity(statusEffectCompanion, this)

class StatusEffectWithIntensity internal constructor(
    private val statusEffectCompanion: StatusEffectCompanion,
    internal val intensity: Int
) {
    infix fun over(rounds: Rounds): StatusEffectWithIntensityAndDuration =
        StatusEffectWithIntensityAndDuration(this, rounds)

    val effectContainer = statusEffectCompanion.createEffectContainer(intensity)

    internal fun createFor(bearer: Entity): StatusEffect = statusEffectCompanion.createFor(bearer).apply {
        effectContainer.applyOn(this)
    }
}

class StatusEffectWithIntensityAndDuration internal constructor(
    private val statusEffectWithIntensity: StatusEffectWithIntensity,
    val duration: Rounds
) {
    val intensity: Int
        get() = statusEffectWithIntensity.intensity

    val effectContainer: StatusEffectInitializationHelper
        get() = statusEffectWithIntensity.effectContainer

    internal fun createFor(bearer: Entity): StatusEffect = statusEffectWithIntensity.createFor(bearer).apply {
        this.duration = this@StatusEffectWithIntensityAndDuration.duration.rounds
    }
}
