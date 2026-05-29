package com.cerebrallychallenged.hypogean.vanilla.triggers

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.effect.destructionEffect
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealAreaEffects
import com.cerebrallychallenged.hypogean.vanilla.cascade.performEntityDestruction
import com.cerebrallychallenged.hypogean.vanilla.props.LandMine.Companion.TRIGGER_RANGE

open class MineTrigger(initializer: Initializer) : StatusEffect(initializer) {
    init {
        triggerRange = TRIGGER_RANGE
    }

    override fun isTriggeredBy(triggeringActor: Actor): Boolean = bearer is LocatedEntity

    context(CascadeBlock)
    override suspend fun executeTrigger(triggeringActor: Actor) {
        bearer.destructionEffect?.let {
            dealAreaEffects(
                it,
                bearer,
                (bearer as LocatedEntity).position,
                EffectReason.ByEntity(triggeringActor)
            )
        }
        performEntityDestruction(bearer)
    }
}