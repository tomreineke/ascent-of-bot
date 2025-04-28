package com.cerebrallychallenged.hypogean.vanilla.triggers

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.base.PROP_SLOT_NAME
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.containment.insert
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.FirstBossBehavior
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.HIDDEN_BOSS_DOOR_POS
import com.cerebrallychallenged.hypogean.vanilla.props.HiddenDoor
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.jun.math.geo.Vec2i

class ClosingDoorTrigger(initializer: Initializer) : StatusEffect(initializer) {
    init {
        triggerRange = 2.0f
    }

    override fun isTriggeredBy(triggeringActor: Actor): Boolean = triggeringActor.factionEntity?.let {
        it.faction == ProtagonistFaction && world.reconTable[it, bearer] == Recon.Visible
    } ?: false

    context(CascadeBlock)
    override suspend fun executeTrigger(triggeringActor: Actor) {
        val door = world.cellAt(HIDDEN_BOSS_DOOR_POS)?.presentProps?.filter { it is HiddenDoor }?.first()
        if (door != null) {
            report(listOf(triggeringActor)) {
                +"A solid metal door is closing silently behind you. A moment later, you see something big moving towards you."
            }
            // the door slides 1 in x direction
            world.cellAt(HIDDEN_BOSS_DOOR_POS.minus(Vec2i.UNIT_X))?.slot(PROP_SLOT_NAME)?.insert(door)
            (bearer as Actor).behavior = FirstBossBehavior
            this@ClosingDoorTrigger.remove()
        }
    }
}