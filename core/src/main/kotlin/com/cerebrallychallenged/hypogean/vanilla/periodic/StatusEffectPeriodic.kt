package com.cerebrallychallenged.hypogean.vanilla.periodic

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Periodic
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.vanilla.cascade.EffectReason
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect

object StatusEffectPeriodic : Periodic {
    context(CascadeBlock)
    override suspend fun execute(bearer: Entity) {
        val currentIniTime = world.currentIniTime
        for (entity in world.entities) {
            for (statusEffect in entity.statusEffects) {
                dealDirectEffect(entity, statusEffect.directEffect, EffectModifiers.Empty, EffectReason.ByEntity(statusEffect))
                statusEffect.duration?.let { duration ->
                    val expirationTime = statusEffect.creationTime + duration
                    // If (expirationTime <= currentIniTime + 1) is true, the removal has to be requested already.
                    // This is because the status effect is only removed at the start of the next turn.
                    // Cf. World.simulateNextStep().
                    // If the condition was expirationTime <= currentIniTime (which is wrong), an effect
                    // would last one turn longer than it should be.
                    if (expirationTime <= currentIniTime + 1) {
                        statusEffect.remove()
                    }
                }
            }
        }
    }
}
