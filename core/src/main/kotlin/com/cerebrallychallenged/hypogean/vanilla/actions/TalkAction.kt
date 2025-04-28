package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.nearActors
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog

var Actor.talkingRange: Float by attribute(3.0f)

object TalkAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val actorCell = activeActor.checkedLocation
        for (actor in actorCell.nearActors(activeActor.talkingRange)) {
            val dialog = actor.dialog
            // When the active actor has a dialog, it's for the other NPCs to talk to him.
            // We don't want self-talk dialogs, however, so we add the 2nd condition.
            if (dialog != null && actor != activeActor) {
                addInstance(TalkActionInstance(activeActor, actor, dialog))
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Talk

    override val hint: String = "Talk"
}

internal class TalkActionInstance(
    activeActor: Actor,
    override var target: Actor,
    private val dialog: Dialog
) : ActionInstance(TalkAction, activeActor) {
    override val equipment: Item = world.dummyEntity

    override val initiativeCost: InitiativeCost = InitiativeCost.KeepTurn

    context(CascadeContext)
    override suspend fun execute() {
        val roles = dialog.determineRoles(activeActor, target)
        world.activeState = dialog.initiate(world, roles, activeActor)
    }
}
