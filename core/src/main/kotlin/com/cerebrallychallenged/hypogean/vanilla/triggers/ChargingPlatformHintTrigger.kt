package com.cerebrallychallenged.hypogean.vanilla.triggers

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.hint.hint

class ChargingPlatformHintTrigger(initializer: Initializer) : StatusEffect(initializer) {
    init {
        triggerRange = 3.0f
    }

    override fun isTriggeredBy(triggeringActor: Actor): Boolean = triggeringActor.factionEntity?.let {
        it.faction == ProtagonistFaction && world.reconTable[it, bearer] == Recon.Visible
    } ?: false

    context(CascadeBlock)
    override suspend fun executeTrigger(triggeringActor: Actor) {
        if (bearer.health > 0) {
            hint(bearer) {
                +"Going onto a charging panel or skipping your turn on a charging panel will restore energy."
            }
            remove()
        }
    }
}