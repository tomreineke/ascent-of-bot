package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.maps.Entity2ObjectMap
import com.cerebrallychallenged.hypogean.util.collections.TransparentHeap
import com.cerebrallychallenged.jun.math.geo.vec

internal class PathFinding(
        private val movementGraph: MovementGraph,
        private val source: Cell
) {
    companion object {
        // Offsets of cells considered the immediate neighbor of a given cell.
        private val NEIGHBORHOOD = listOf(
                vec(1, 0),
                vec(1, 1),
                vec(0, 1),
                vec(-1, 1),
                vec(-1, 0),
                vec(-1, -1),
                vec(0, -1),
                vec(1, -1)
        )
    }

    private inner class CellData(
            val cell: Cell,
            initialDistance: Float,
            var predecessor: CellData?
    ) : Comparable<CellData> {
        init {
            dataByCell[cell] = this
        }

        private val heapNode = heap.add(this, initialDistance)

        val distance: Float
            get() = heapNode.key

        /**
         * Is set to `true` right after this has been expanded.
         * Then this belongs to the black set, i.e., the true distance is known.
         */
        var isFinal: Boolean = false

        /**
         * Decreases the stored distance to `newDistance` if the latter is actually smaller.
         * @return whether the distance has been decreased.
         */
        fun decreaseDistance(newDistance: Float): Boolean {
            return if (newDistance < distance) {
                heapNode.decreaseKey(newDistance)
                true
            } else {
                false
            }
        }

        override fun compareTo(other: CellData): Int = when {
            distance < other.distance -> -1
            distance > other.distance -> 1
            else -> cell.id.compareTo(other.cell.id)
        }
    }

    private val world = source.world

    private val dataByCell = Entity2ObjectMap<Cell, CellData>(world)

    private val heap = TransparentHeap<Float, CellData>()

    private val finalData = sortedSetOf<CellData>()

    private fun expandNext(): CellData {
        val sourceData = heap.extractMin().data
        finalData.add(sourceData)
        sourceData.isFinal = true
        val sourceCell = sourceData.cell
        if (sourceCell != source && !movementGraph.canBeSource(sourceCell)) return sourceData
        val predecessor = sourceData.predecessor
        val sourcePosition = sourceCell.position
        val predecessorPosition = predecessor?.cell?.position
        for (delta in NEIGHBORHOOD) {
            val targetPosition = sourcePosition + delta
            if (targetPosition == predecessorPosition) continue
            val targetCell = world.cellAt(targetPosition) ?: continue
            val targetData = dataByCell[targetCell]
            if (targetData == null && !movementGraph.canBeTarget(targetCell)) continue

            fun relax(edgeLength: Float, pred: CellData) {
                val distance = pred.distance + edgeLength
                if (targetData == null) {
                    CellData(targetCell, distance, pred)
                } else if (targetData.decreaseDistance(distance)) {
                    targetData.predecessor = pred
                }
            }

            if (predecessor != null) {
                val edgeLength = movementGraph.edgeLength(predecessor.cell, targetCell)
                if (edgeLength.isFinite()) {
                    relax(edgeLength, predecessor)
                    continue
                }
            }
            val edgeLength = movementGraph.edgeLength(sourceCell, targetCell)
            if (edgeLength.isFinite()) {
                relax(edgeLength, sourceData)
            }
        }
        return sourceData
    }

    init {
        CellData(source, 0.0f, null)
        expandNext()
    }

    /**
     * Expand until the expanded [Cell] satisfies the specified predicate
     * or the maximum distance from source is exceeded
     * or the heap is empty.
     */
    private fun expandUntil(maxDistance: Float, predicate: (Cell) -> Boolean): CellData? {
        while (!heap.isEmpty() && finalData.last().distance <= maxDistance) {
            val data = expandNext()
            if (predicate(data.cell)) {
                return data
            }
        }
        return null
    }

    /**
     * Expands all [Cell]s with a distance less than or equal `maxDistance` from source.
     */
    fun expand(maxDistance: Float) {
        expandUntil(maxDistance) { false }
    }

    /**
     * Reconstructs the path to the specified target if its distance from source does not exceed `maxDistance`.
     */
    private fun reconstructPathTo(targetData: CellData, maxDistance: Float): CellPath? {
        if (targetData.distance > maxDistance) return null
        val waypoints
                = generateSequence(targetData) { data -> data.predecessor.takeIf { it?.cell  !== source } }
                    .map { it.cell }
                    .toMutableList()
        return if (waypoints.isEmpty()) {
            null
        } else {
            waypoints.reverse()
            CellPath(source, waypoints)
        }
    }

    fun pathTo(target: Cell, maxDistance: Float = Float.POSITIVE_INFINITY): CellPath? {
        dataByCell[target]?.let { data ->
            if (data.isFinal) {
                // Target already has final distance (black set)
                return reconstructPathTo(data, maxDistance)
            }
        }
        val data = expandUntil(maxDistance) { cell -> cell == target } ?: return null
        // expand() just discovered the target with a final distance
        return reconstructPathTo(data, maxDistance)
    }

    private fun reachableCellData(maxDistance: Float): Sequence<CellData> {
        expand(maxDistance)
        return finalData.asSequence().takeWhile { it.distance <= maxDistance }
    }

    /**
     * Returns all cells whose shortest path distance does not exceed the specified maximum distance.
     */
    fun reachableCells(maxDistance: Float): Sequence<Cell> = reachableCellData(maxDistance).map { it.cell }

    /**
     * Returns all paths with the specified maximum length.
     */
    fun allPaths(maxLength: Float): Sequence<CellPath>
            = reachableCellData(maxLength).map { reconstructPathTo(it, maxLength)!! }
}