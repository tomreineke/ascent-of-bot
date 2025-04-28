package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot

object DirectShotAction : Action, ActionWithAccuracy {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        createInstance<WeaponSlot, DirectShotWeapon>(
            activeActor,
            assumedActiveActorLocation,
            this@DirectShotAction,
            ::DirectShotActionInstance
        )
    }

    override val category: ActionCategory = ActionCategory.Attack
}

internal class DirectShotActionInstance(
        activeActor: Actor,
        assumedActiveActorLocation: Cell,
        tool: DirectShotWeapon,
        target: LocatedEntity
) : BasicShotActionInstance<DirectShotWeapon>(activeActor, assumedActiveActorLocation, tool, target, DirectShotAction)
