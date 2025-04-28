package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.model.modelError

/**
 * `NullAction` is used by NPCs to recompute the available actions.
 * Like [SkipAction], it has no effect, but unlike it, `NullAction` lets the active actor keep its turn.
 */
object NullAction : Action {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        addInstance(NullActionInstance(activeActor))
    }

    override val category: ActionCategory
        get() = ActionCategory.Null
}

internal class NullActionInstance(activeActor: Actor) : ActionInstance(NullAction, activeActor) {
    override val equipment: Item
        get() = world.dummyEntity

    override val target: Entity
        get() = world.dummyEntity

    override val initiativeCost: InitiativeCost
        get() = InitiativeCost.KeepTurn

    context(CascadeContext)
    override suspend fun execute() {}
}

internal val ActionTable.nullActionInstance: ActionInstance
    get() = groupedByAction[NullAction].instances.firstOrNull() ?: modelError("Null action not available")
