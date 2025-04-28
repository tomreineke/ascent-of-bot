package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cascade.EffectConsequence
import com.cerebrallychallenged.hypogean.vanilla.attributes.Health
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.map.events.OverheadTextEvent
import com.cerebrallychallenged.hypogean.view.report.effectAmount
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

/**
 * If the health of this entity changes, should that show up in combat report?
 */
var Entity.showHealthChangesInDamageReport: Boolean by attribute(false)

/**
 * If the health of this entity changes, should that displayed as
 * overhead text in the map view?
 */
var Entity.showHealthChangesWithOverheadText: Boolean by attribute(false)

class ChangeHealthConsequence(target: Entity, causalChange: CausalChange) : EffectConsequence(target, causalChange) {
    context(CascadeBlock)
    override suspend fun execute() {
        if (target is Cell || !target.isAlive) return
        val effectiveDelta = EffectiveDelta.compute(causalChange.delta, target, Health)
        if (target.showHealthChangesInDamageReport) {
            report(target) {
                entityRefCapitalizeName(target)
                +" receives "
                effectAmount(effectiveDelta, Health, causalChange)
                +"."
            }
        }
        val effectiveDeltaHealth = effectiveDelta.effectiveDelta
        if (effectiveDeltaHealth != 0
            && target.showHealthChangesWithOverheadText
            && target is LocatedEntity
            && ProtagonistFaction.reconOf(target) == Recon.Visible
        ) {
            world.notifyViewEvent(OverheadTextEvent(
                target,
                if (effectiveDeltaHealth < 0) FLinearColor.Red else FLinearColor.Green,
                "%+d".format(effectiveDeltaHealth)
            ))
        }
        target.health += effectiveDeltaHealth
        if (target.health == 0) {
            if (target.showHealthChangesInDamageReport) {
                report(target) {
                    entityRefCapitalizeName(target)
                    +" destroyed."
                }
            }
            performEntityDestruction(target)
        }
    }

    override val intProperty: IntProperty
        get() = Health
}
