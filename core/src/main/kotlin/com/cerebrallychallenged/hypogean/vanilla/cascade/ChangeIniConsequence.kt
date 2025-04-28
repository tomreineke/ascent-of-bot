package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cascade.EffectConsequence
import com.cerebrallychallenged.hypogean.vanilla.attributes.Ini
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.map.events.OverheadTextEvent
import com.cerebrallychallenged.hypogean.view.report.effectAmount
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

/**
 * If the ini of this entity changes, should that show up in combat report?
 */
var Entity.showIniChangesInDamageReport: Boolean by attribute(false)

/**
 * If the ini of this entity changes, should that be displayed as
 * overhead text in the map view?
 */
var Entity.showIniChangesWithOverheadText: Boolean by attribute(false)

class ChangeIniConsequence(target: Entity, causalChange: CausalChange) : EffectConsequence(target, causalChange) {
    context(CascadeBlock)
    override suspend fun execute() {
        if (target !is Actor || !target.isAlive) return
        val delta = causalChange.delta
        if (delta != 0) {
            val iniTime = target.scheduledIniTime
            val iniQueue = world.iniQueue
            iniQueue.remove(target)
            iniQueue.enqueueAbsolute(iniTime + delta, target)
            val effectiveDelta = EffectiveDelta.compute(delta, target, Ini)
            if (target.showIniChangesInDamageReport) {
                report(target) {
                    entityRefCapitalizeName(target)
                    +" receives "
                    effectAmount(effectiveDelta, Ini, causalChange)
                    +"."
                }
            }
            val effectiveDeltaIni = effectiveDelta.effectiveDelta
            if (effectiveDeltaIni != 0
                && target.showIniChangesWithOverheadText
                && ProtagonistFaction.reconOf(target) == Recon.Visible
            ) {
                world.notifyViewEvent(OverheadTextEvent(
                    target,
                    FLinearColor.Gray,
                    "%+d".format(effectiveDeltaIni)
                ))
            }
        }
    }
}
