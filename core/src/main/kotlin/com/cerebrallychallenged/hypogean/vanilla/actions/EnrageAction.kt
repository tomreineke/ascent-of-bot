package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.action.Action
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.actionHistory
import com.cerebrallychallenged.hypogean.vanilla.ActionWithAccuracy
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.items.WeaponSlot
import com.cerebrallychallenged.hypogean.vanilla.items.melee.EnrageArm
import com.cerebrallychallenged.jun.math.clamp

object EnrageAction : Action, ActionWithAccuracy {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        createInstance<WeaponSlot, EnrageArm>(
            activeActor,
            assumedActiveActorLocation,
            this@EnrageAction,
            { _, _, tool, target ->
                val decrement = activeActor.actionHistory.takeLastWhile { it.action is EnrageAction }.count()
                // reduce initiative cost down to a minimum of 1
                val initiativeCost =
                    InitiativeCost.Delta(clamp(tool.initiativeCost - decrement, 1, tool.initiativeCost))
                EnrageActionInstance(activeActor, assumedActiveActorLocation, tool, target, initiativeCost)
            }
        )
    }

    override val category: ActionCategory = ActionCategory.Attack
}

internal class EnrageActionInstance(
    activeActor: Actor,
    assumedActiveActorLocation: Cell,
    tool: EnrageArm,
    target: LocatedEntity,
    override val initiativeCost: InitiativeCost
) : AdjacentActionInstance(activeActor, EnrageAction, assumedActiveActorLocation, false, tool, target)
