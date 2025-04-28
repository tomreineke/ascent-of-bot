package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.nearProps
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.jun.log.log

var Actor.hackingRange: Float by attribute(1.5f)

var Item.hackingDialog: Dialog? by attribute(null)

object HackingAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val actorCell = activeActor.checkedLocation
        for (prop in actorCell.nearProps(activeActor.hackingRange)) {
            val dialog = prop.hackingDialog
            if (dialog != null) {
                addInstance(HackingActionInstance(activeActor, prop, dialog))
            }
        }
    }

    override val category: ActionCategory = ActionCategory.Hacking

    override val hint: String = "Hack"
}

internal class HackingActionInstance(
    activeActor: Actor,
    override var target: Item,
    private val dialog: Dialog
) : ActionInstance(HackingAction, activeActor) {
    override val equipment: Item = world.dummyEntity

    override val initiativeCost: InitiativeCost = InitiativeCost.KeepTurn

    context(CascadeContext)
    override suspend fun execute() {
        val roles = dialog.determineRoles(activeActor, target)
        world.activeState = dialog.initiate(world, roles, activeActor)
        log.warn { "activeState set to ${world.activeState}" }
    }
}
