package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.pathfinding.MovementGraph
import com.cerebrallychallenged.hypogean.pathfinding.canDirectlyMove
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.util.crossJoin
import com.cerebrallychallenged.jun.util.maxAllBy

const val DEFAULT_SIGHT_STRENGTH = 0.5f

/**
 * @param exposure how much of the target cell is exposed.
 *                 Lies within the interval `]0.0; 1.0]`, no exposure to fully exposed.
 * @param viewPosition the position from which to actually look.
 * @param sideStepDelta the sidestep taken. Is the zero vector for non-actors or if no sidestep was taken.
 */
class Sight(
        val exposure: Float,
        val viewPosition: Vec2i,
        val sideStepDelta: Vec2i,
        computeTargetCentroid: () -> Vec2f
) {
    /**
     * Centroid of the exposed target area.
     */
    val targetCentroid: Vec2f by lazy(computeTargetCentroid)
}

val Sight?.exposure: Float
    get() = this?.exposure ?: 0.0f

private fun RaysQuery.createSight(
        targetPosition: Vec2i,
        strength: Float,
        viewPosition: Vec2i,
        sideStepDelta: Vec2i = Vec2i.ZERO
): Sight? {
    val exposure = computeExposure(targetPosition, strength)
    return if (exposure > 0.0f) {
        // When the target is exposed, the centroid exists, too.
        Sight(exposure, viewPosition, sideStepDelta) { computeCentroid(targetPosition, strength)!! }
    } else {
        null
    }
}

class SightQuery internal constructor(private val pointOfViews: List<PointOfView>) {
    internal data class PointOfView(val viewPosition: Vec2i, val sideStepDelta: Vec2i, val query: RaysQuery)

    fun of(targetPosition: Vec2i, strength: Float = DEFAULT_SIGHT_STRENGTH): Sight?
            = pointOfViews
            .asSequence()
            .mapNotNull { (viewPosition, sideStepDelta, query) ->
                query.createSight(targetPosition, strength, viewPosition, sideStepDelta)
            }
            .maxAllBy { it.exposure }
            .minByOrNull { it.sideStepDelta.length }

    fun of(target: LocatedEntity, strength: Float = DEFAULT_SIGHT_STRENGTH): Sight? {
        return if (target is Actor) {
            pointOfViews
                    .asSequence()
                    .crossJoin(target.occupiedPositions.points)
                    .mapNotNull { (pointOfView, targetPosition) ->
                        val (viewPosition, sideStepDelta, query) = pointOfView
                        query.createSight(targetPosition, strength, viewPosition, sideStepDelta)
                    }
                    .maxAllBy { it.exposure }
                    .minByOrNull { it.sideStepDelta.length }
        } else {
            of(target.position, strength)
        }
    }
}

fun Cell.sight(blockerValueExtractor: BlockerValueExtractor): SightQuery {
    return SightQuery(listOf(SightQuery.PointOfView(
            position,
            Vec2i.ZERO,
            world.queryRays(position, blockerValueExtractor, this)
    )))
}

/**
 * Queries the sight of this actor to other entities, taking potential sidestep movements into account.
 *
 * In doubt stay where we are, i. e. no sidestep
 * We don't necessarily want to move closer to an opponent because of explosion radius. For this a quick
 * move before shooting would be better.
 *
 * @param blockerValueExtractor determines how other block the visibility.
 * @param sideStepMovement used to determine if this actor can take the sidestep.
 * @param sourceLocation assumed location of this actor. If not specified, the actual location is used.
 */
fun Actor.sight(
        blockerValueExtractor: BlockerValueExtractor,
        sideStepMovement: MovementGraph? = null,
        sourceLocation: Cell = checkedLocation
): SightQuery {
    val sourcePosition = sourceLocation.position
    val queries = mutableListOf<SightQuery.PointOfView>()
    val occupiedPositions = occupiedPositions(sourceLocation).points.toSet()
    for (position in occupiedPositions) {
        queries.add(SightQuery.PointOfView(
                position,
                Vec2i.ZERO,
                world.queryRays(position, blockerValueExtractor, this)
        ))
    }
    if (sideStepMovement != null) {
        for (heading in Heading.values()) {
            val sideStepDelta = heading.delta
            val sideStepPosition = sourcePosition + sideStepDelta
            // For a side step to make, the neighboring cell must exist and the actor must be able to move there.
            // We can ignore the source cell as the actor is already standing there.
            val sideStepCell = world.cellAt(sideStepPosition) ?: continue
            if (!sideStepMovement.canDirectlyMove(sourceLocation, sideStepCell, ignoreSource = true)) continue
            for (position in occupiedPositions(sideStepCell).points) {
                if (position !in occupiedPositions) {
                    queries.add(SightQuery.PointOfView(
                            position,
                            sideStepDelta,
                            world.queryRays(position, blockerValueExtractor, this)
                    ))
                }
            }
        }
    }
    return SightQuery(queries)
}
