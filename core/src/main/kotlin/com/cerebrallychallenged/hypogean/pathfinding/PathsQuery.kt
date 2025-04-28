package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Cell

class PathsQuery(movementGraph: MovementGraph, source: Cell) {
    private val pathFinding = PathFinding(movementGraph, source)

    fun preCompute(maxDistance: Float = Float.POSITIVE_INFINITY) {
        pathFinding.expand(maxDistance)
    }

    fun to(target: Cell, maxDistance: Float = Float.POSITIVE_INFINITY): CellPath?
            = pathFinding.pathTo(target, maxDistance)

    fun reachableCells(maxDistance: Float = Float.POSITIVE_INFINITY) = pathFinding.reachableCells(maxDistance)

    fun allPaths(maxLength: Float = Float.POSITIVE_INFINITY) = pathFinding.allPaths(maxLength)
}