package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.RaysQuery
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points

/**
 * Movement graph yielding the Euclidean distance for the length of each valid edge.
 *
 * Note that this class is abstract because it shall be subclassed by data classes to be used as key in caching.
 */
abstract class SimpleMovementGraph(
        val blockerValueExtractor: BlockerValueExtractor,
        private val movingSubject: Any?,
        private val moverSize: Int,
        val obstructionThreshold: Float,
        private val canMoveIntoOccupiedCells: Boolean
) : MovementGraph {
    fun createQuery(world: World, sourcePosition: Vec2i): RaysQuery =
            world.queryRays(sourcePosition, blockerValueExtractor, movingSubject)

    override fun edgeLength(source: Cell, target: Cell): Float {
        val world = source.world
        val sourcePosition = source.position
        val targetPosition = target.position
        val query = createQuery(world, sourcePosition)
        if (!query.hasRay(targetPosition)) return NO_EDGE
        val obstruction = query
                .computeBlockerValues(targetPosition, moverSize)
                .maxOrNull()
        require(obstruction != null)
        return if (obstruction < obstructionThreshold) {
            //FIXME increase distance as determined by additional extractor, e.g., such that trench wall reduces range

            sourcePosition.distanceTo(targetPosition)
        } else {
            NO_EDGE
        }
    }

    private val Cell.isUnoccupied: Boolean
        get() = blockerValueExtractor.cellValue(this, movingSubject) < obstructionThreshold

    override fun canBeSource(cell: Cell): Boolean {
        return if (moverSize == 1) {
            cell.isUnoccupied
        } else {
            val world = cell.world
            Bounds.byMinSize(cell.position, Vec2i.ONE * moverSize).points.all { position ->
                world.cellAt(position)?.isUnoccupied ?: false
            }
        }
    }

    override fun canBeTarget(cell: Cell): Boolean = canMoveIntoOccupiedCells || canBeSource(cell)
}