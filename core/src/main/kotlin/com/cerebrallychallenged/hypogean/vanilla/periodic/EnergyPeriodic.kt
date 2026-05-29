package com.cerebrallychallenged.hypogean.vanilla.periodic

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Periodic
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectModifier
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.EffectValueExpression
import com.cerebrallychallenged.hypogean.model.effect.MutableEffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.vanilla.attributes.energyCharging
import com.cerebrallychallenged.hypogean.vanilla.attributes.energyProduction
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.passiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.cascade.EnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect
import com.cerebrallychallenged.hypogean.vanilla.effects.EnergyCharging

object EnergyPeriodic : Periodic {
    context(CascadeBlock)
    override suspend fun execute(bearer: Entity) {
        for (actor in world.actors) {
            val effects = mutableListOf<EffectValueExpression>()
            val modifiers = MutableEffectModifiers()
            val baseEnergyConsumption = actor.passiveEnergyConsumption
            var hasConsumers = false
            for (item in actor.equippedItems) {
                val consumption = item.passiveEnergyConsumption
                if (consumption > 0) {
                    hasConsumers = true
                    modifiers.add(
                        EffectModifiers.Phase.ProducersConsumers,
                        EffectModifier(consumption of EnergyConsumption),
                        EffectModifiers.Reason.By(item)
                    )
                }
            }
            if (baseEnergyConsumption > 0 || hasConsumers) {
                effects.add(baseEnergyConsumption of EnergyConsumption)
            }
            var hasProducers = false
            for (item in actor.equippedItems) {
                val production = item.energyProduction
                if (production > 0) {
                    hasProducers = true
                    modifiers.add(
                        EffectModifiers.Phase.ProducersConsumers,
                        EffectModifier(production of EnergyCharging),
                        EffectModifiers.Reason.By(item)
                    )
                }
            }
            val chargingItems = actor.occupiedLocations.flatMap { cell ->
                cell.presentProps.filter { it.health > 0 && it.energyCharging > 0 }
            }.distinct()
            for (item in chargingItems) {
                hasProducers = true
                modifiers.add(
                    EffectModifiers.Phase.ProducersConsumers,
                    EffectModifier(item.energyCharging of EnergyCharging),
                    EffectModifiers.Reason.By(item)
                )
            }
            if (hasProducers) {
                effects.add(0 of EnergyCharging)
            }
            if (effects.isNotEmpty()) {
                dealDirectEffect(
                    actor,
                    Effect(*effects.toTypedArray()),
                    modifiers,
                    EffectReason.ByEntity(actor)
                )
            }
        }
    }
}
