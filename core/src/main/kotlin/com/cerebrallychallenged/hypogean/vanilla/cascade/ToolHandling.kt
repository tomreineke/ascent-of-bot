package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.activeActor
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectKind
import com.cerebrallychallenged.hypogean.model.effect.MutableEffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.lastUseTime
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount

object EnergyConsumption : EffectKind()

context(CascadeBlock)
suspend fun handleEquipment(equipment: Item, distance: Int = 0) {
    if (!equipment.isAlive) return
    if (isReal) {
        equipment.lastUseTime = world.currentIniTime
        equipment.remainingUseCount?.let { useCount ->
            val newCount = useCount - 1
            equipment.remainingUseCount = newCount
            if (newCount <= 0) {
                schedule {
                    performEntityDestruction(equipment)
                }
            }
        }
    }

    world.activeActor?.let { activeActor ->
        val energyConsumption = when (val consumption = equipment.activeEnergyConsumption) {
            is ActiveEnergyConsumption.PerAction -> consumption.value
            is ActiveEnergyConsumption.PerDistance -> consumption.perMeter * distance
        }
        if (energyConsumption > 0) {
            dealDirectEffect(
                activeActor,
                Effect(energyConsumption of EnergyConsumption),
                MutableEffectModifiers(),
                EffectReason.ByEntity(equipment)
            )
        }
    }
}
