package com.cerebrallychallenged.hypogean.vanilla.triggers

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Rounds
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.StatusEffectWithIntensityAndDuration
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.vanilla.actors.GreatAI
import com.cerebrallychallenged.hypogean.vanilla.factions.DeepDrillingCorpFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.CONVEYOR_BELT_Y
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.HealingOverTime
import com.cerebrallychallenged.hypogean.view.report.reportSpeech

class GreatAiApprovalTrigger(initializer: Initializer) : StatusEffect(initializer) {
    init {
        triggerRange = 1.0f
    }

    override fun isTriggeredBy(triggeringActor: Actor): Boolean = triggeringActor.factionEntity?.let {
        it.faction == ProtagonistFaction && isDroppedOnConveyorBelt(bearer)
    } ?: false

    context(CascadeBlock)
    override suspend fun executeTrigger(triggeringActor: Actor) {
        val ai = DeepDrillingCorpFaction.entity.actors.first { it is GreatAI }
        reportSpeech(listOf(triggeringActor, ai), ai) {
            +"Good, proceed."
        }
        if (triggeringActor.statusEffects.isEmpty()) {
            triggeringActor.addStatusEffect(
                StatusEffectWithIntensityAndDuration(1 of HealingOverTime, Rounds(20))
            )
        }
        remove()
    }

    private fun isDroppedOnConveyorBelt(entity: Entity): Boolean {
        val pos = (entity as LocatedEntity).position
        return pos.x in -8 downTo -10 && pos.y == CONVEYOR_BELT_Y
    }
}