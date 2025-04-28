package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cascade.EffectConsequence
import com.cerebrallychallenged.hypogean.vanilla.attributes.Energy
import com.cerebrallychallenged.hypogean.vanilla.attributes.energy
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.map.events.OverheadTextEvent
import com.cerebrallychallenged.hypogean.view.report.effectAmount
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

/**
 * If the energy of this entity changes, should that show up in combat report?
 */
var Entity.showEnergyChangesInDamageReport: Boolean by attribute(false)

/**
 * If the energy of this entity changes, should that displayed as
 * overhead text in the map view?
 */
var Entity.showEnergyChangesWithOverheadText: Boolean by attribute(false)

class ChangeEnergyConsequence(target: Entity, causalChange: CausalChange) : EffectConsequence(target, causalChange) {
    context(CascadeBlock)
    override suspend fun execute() {
        if (!target.isAlive) return
        val effectiveDelta = EffectiveDelta.compute(causalChange.delta, target, Energy)
        if (target.showEnergyChangesInDamageReport) {
            report(target) {
                entityRef(target)
                +" receives "
                effectAmount(effectiveDelta, Energy, causalChange)
                +"."
            }
        }
        val effectiveDeltaEnergy = effectiveDelta.effectiveDelta
        if (effectiveDeltaEnergy != 0
            && target.showEnergyChangesWithOverheadText
            && target is LocatedEntity
            && (causalChange !is EffectResult || causalChange.tables.first().table.kind != EnergyConsumption)
            && ProtagonistFaction.reconOf(target) == Recon.Visible
        ) {
            // Don't show energy consumption as overhead text as it would be spamming.
            world.notifyViewEvent(OverheadTextEvent(
                target,
                FLinearColor.Blue,
                "%+d".format(effectiveDeltaEnergy)
            ))
        }
        target.energy += effectiveDeltaEnergy
        if (target.energy == 0) {
            schedule {
                performEnergyBlackout(target)
            }
        }
    }

    override val intProperty: IntProperty
        get() = Energy
}
