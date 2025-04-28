package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cascade.EffectConsequence
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.view.report.report
import kotlin.reflect.KClass

data class StatusEffectBearerPair<T : StatusEffect>(val statusEffectType: EntityType<T>, val bearer: Entity)

inline infix fun <reified T : StatusEffect> ((Initializer) -> T).on(bearer: Entity): StatusEffectBearerPair<T>
        = StatusEffectBearerPair(bearer.entityTypeOf(), bearer)

class AddStatusEffectConsequence(
    target: Entity,
    causalChange: CausalChange,
    private val statusEffectClass: KClass<out StatusEffect>
) : EffectConsequence(target, causalChange) {
    context(CascadeBlock)
    override suspend fun execute() {
        val statusEffectType = entityTypeOf(statusEffectClass)
        val newDuration = causalChange.delta
        if (newDuration > 0) {
            val oldStatusEffect = target.statusEffects.find { statusEffectType.isInstance(it) }
            if (oldStatusEffect != null) {
                // Return if the status effect already has infinite duration.
                val oldEndTime = oldStatusEffect.creationTime + (oldStatusEffect.duration ?: return)
                val newEndTime = world.currentIniTime + newDuration
                if (newEndTime > oldEndTime) {
                    oldStatusEffect.duration = newDuration
                    report(target) {
                        +"$oldStatusEffect of "
                        entityRef(target)
                        +" is refreshed to $newDuration rounds."
                    }
                } else {
                    report(target) {
                        entityRef(target)
                        +" receives $oldStatusEffect but had that already and duration is not increased."
                    }
                }
            } else {
                val newStatusEffect = target.createStatusEffect(statusEffectType.factory)
                newStatusEffect.duration = newDuration
                report(target) {
                    entityRef(target)
                    +" receives $newStatusEffect for $newDuration rounds."
                }
            }
        }
    }
}
