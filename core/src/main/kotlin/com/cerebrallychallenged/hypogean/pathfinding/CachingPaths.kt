package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Cell

class CachingPaths {
    inner class ShortestPaths(private val movementGraph: MovementGraph) {
        fun from(source: Cell): PathsQuery {
            isCacheEmpty = false
            return cache.computeIfAbsent(QueryParameter(movementGraph, source)) { PathsQuery(movementGraph, source) }
        }
    }

    private data class QueryParameter(val movementGraph: MovementGraph, val source: Cell)

    private val cache = mutableMapOf<QueryParameter, PathsQuery>()

    private var isCacheEmpty: Boolean = true

    internal fun invalidateCache() {
        if (!isCacheEmpty) {
            cache.clear()
            isCacheEmpty = true
        }
    }

    internal fun query(movementGraph: MovementGraph): ShortestPaths {
        isCacheEmpty = false
        return ShortestPaths(movementGraph)
    }
}