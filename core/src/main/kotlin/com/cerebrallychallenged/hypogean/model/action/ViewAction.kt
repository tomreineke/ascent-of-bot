package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext

abstract class ViewAction : Action {
    final override val category: ActionCategory
        get() = ViewActionCategory
}

abstract class ViewActionInstance(
    action: ViewAction,
    activeActor: Actor,
    val isDebugOnly: Boolean = false
) : ActionInstance(action, activeActor) {
    final override val equipment: Item
        get() = world.dummyEntity

    final override val initiativeCost: InitiativeCost
        get() = InitiativeCost.KeepTurn

    context(CascadeContext)
    final override suspend fun execute() {
        // Nothing to do here.
        // We do not throw an exception because a Behavior randomly submitting a ViewAction does not hurt either.
    }
}

internal object ViewActionCategory : ActionCategory

val ActionCategory.Companion.View: ActionCategory
    get() = ViewActionCategory

/**
 * Returns an action table with all [ViewAction]s currently available for clients with the specified faction.
 */
fun World.viewActions(faction: Faction): ActionTable {
    val state = activeState as? ActiveActorState ?: return ActionTable.Empty
    return if (state.activeActor.faction == faction) {
        state.availableActions.groupedByCategory[ViewActionCategory]
    } else ActionTable.Empty
}
