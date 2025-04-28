package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.World

/**
 * `ActionLibrary` maintains a list of [Action]s and computes available action instances.
 * Actions can be whitelisted and blacklisted by modifying the collections
 * returned by [whiteList] and [blackList], respectively.
 */
class ActionLibrary(private val world: World) {
    val blackList = mutableSetOf<Action>()

    val whiteList = mutableListOf<Action>()

    private val actions = world.rulebook.actions

    /**
     * Computes the table of available actions for the currently active actor.
     * Precondition: world must have an active actor.
     * @return the table of available actions.
     */
    fun computeAvailableActions(
        activeActor: Actor,
        assumedActiveActorLocation: Cell,
        actionFilter: (Action) -> Boolean = { true }
    ): ActionTable {
        var actions: Sequence<Action> = actions.asSequence().filter(actionFilter)
        if (whiteList.isNotEmpty()) {
            actions = actions.filter { it in whiteList }
        }
        if (blackList.isNotEmpty()) {
            actions = actions.filter { it !in blackList }
        }
        return computeAvailableActions(activeActor, assumedActiveActorLocation, actions)
    }

    private fun computeAvailableActions(
        activeActor: Actor,
        assumedActiveActorLocation: Cell,
        actions: Sequence<Action>
    ): ActionTable = MutableActionTable(null).apply {
        for (action in actions) {
            with(action) {
                createInstances(activeActor, assumedActiveActorLocation)
            }
        }
    }
}
