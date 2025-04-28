package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.CausedStatusEffects
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.view.report.report

context(CascadeBlock)
fun causeStatusEffects(
    entity: Entity,
    causedStatusEffects: CausedStatusEffects
) {
    if (!isReal) return
    for (effect in causedStatusEffects.effects) {
        if (entity.isAlive) {
            val statusEffect = effect.createFor(entity)
            report(entity) {
                entityRefCapitalizeName(entity)
                +" gets "
                statusEffect.icon?.let {
                    +" "
                    image(it)
                }
                +"${statusEffect.name}."
            }
        }
    }
}
