package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Cell

const val NO_EDGE = Float.POSITIVE_INFINITY

/**
 * Determines the graph for shortest path computations.
 * Implementing concrete classes must implement [hashCode] and [equals] so they can be used as key for caching.
 */
interface MovementGraph {

    /**
     * Returns the length of the edge between both cells or [NO_EDGE] if the edge does not exist.
     */
    fun edgeLength(source: Cell, target: Cell): Float

    /**
     * Can the cell have outgoing edges?
     */
    fun canBeSource(cell: Cell): Boolean

    /**
     * Can the cell have ingoing edges?
     */
    fun canBeTarget(cell: Cell): Boolean
}

/**
 * Is there movement possible from the specified source to the specified target?
 *
 * For that, `source` must be a suitable source
 * (checking that condition may be skipped by `ignoreSource` if it is known to be satisfied for other reasons),
 * `target` must be a suitable target,
 * and the edge from `source` to `target` must exist, i.e., have finite length.
 */
fun MovementGraph.canDirectlyMove(source: Cell, target: Cell, ignoreSource: Boolean = false): Boolean
        = (ignoreSource || canBeSource(source))
            && canBeTarget(target)
            && edgeLength(source, target).isFinite()