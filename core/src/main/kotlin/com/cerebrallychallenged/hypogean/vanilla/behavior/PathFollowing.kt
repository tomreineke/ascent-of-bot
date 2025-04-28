package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.skipTurn
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.pathfinding.MovementGraph
import com.cerebrallychallenged.hypogean.vanilla.actions.MoveAction

private fun ActionTable.findMove(target: Cell): ActionInstance? =
    groupedByAction[MoveAction].groupedByTarget[target].instances.firstOrNull()

suspend fun NpcContext.followPath(path: CellPath, cancelCondition: NpcContext.() -> Boolean = { false }): Boolean {
    val waypoints = path.waypoints
    val waypointCount = waypoints.size
    var i = 0
    while (i < waypointCount) {
        if (cancelCondition()) return false
        var currentMove: ActionInstance? = null
        while (i < waypointCount) {
            val move = (currentMove?.expand() ?: availableActions).findMove(waypoints[i])
            if (move != null) {
                currentMove = move
                ++i
            } else {
                break
            }
        }
        if (currentMove != null) {
            submit(currentMove)
        } else {
            skipTurn()
        }
    }
    return true
}


/**
 * Moves to the closest of the specified targets.
 */
suspend fun NpcContext.moveTo(
    targets: List<Cell>,
    graph: MovementGraph,
    cancelCondition: NpcContext.() -> Boolean = { false }
): Boolean {
    if (targets.any { it in activeActor.occupiedLocations }) return true
    val query = world.shortestPath(graph).from(activeActor.checkedLocation)
    val shortestPathFound = targets.mapNotNull { query.to(it) }.minByOrNull { it.length } ?: return false
    return followPath(shortestPathFound, cancelCondition)
}

suspend fun NpcContext.moveTo(
    target: Cell,
    graph: MovementGraph,
    cancelCondition: NpcContext.() -> Boolean = { false }
): Boolean = moveTo(listOf(target), graph, cancelCondition)
