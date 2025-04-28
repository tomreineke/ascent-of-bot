package com.cerebrallychallenged.hypogean.vanilla.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.PropPlacement
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.pathfinding.SimpleMovementGraph
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.RayOrientation
import com.cerebrallychallenged.jun.util.sumByFloat

/**
 * How much does that entity block the movement of objects passing mid-air?
 *
 * For instance, you usually cannot shoot through a brick wall, but easily shoot through a paper wall.
 *
 * @see BallisticExtractor
 */
var Entity.ballisticBlocking: BlockingValue by attribute(BlockingValue { 0.0f })

abstract class SimpleBlockerValueExtractor(val blockingValue: (LocatedEntity) -> BlockingValue) : BlockerValueExtractor() {
    open fun relevantCellEntities(cell: Cell, actingSubject: Any?): Sequence<LocatedEntity> = sequence {
        val presentActor = cell.presentActor
        if (presentActor != null && presentActor != actingSubject) {
            yield(presentActor)
        }
        yieldAll(cell.presentProps[PropPlacement.Center])
        yield(cell)
    }

    open fun relevantBorderEntities(
        cell: Cell,
        placement: PropPlacement,
        orientation: RayOrientation,
        heading: Heading,
        actingSubject: Any?
    ): Sequence<LocatedEntity> = cell.presentProps[placement]

    override fun cellValue(
        cell: Cell,
        actingSubject: Any?
    ): Float = relevantCellEntities(cell, actingSubject)
            .sumByFloat { blockingValue(it)() }

    override fun borderValue(
        cell: Cell,
        placement: PropPlacement,
        orientation: RayOrientation,
        heading: Heading,
        actingSubject: Any?
    ): Float = relevantBorderEntities(cell, placement, orientation, heading, actingSubject)
        .sumByFloat { blockingValue(it)(orientation, heading) }

    override fun identifyCellEntities(
        cell: Cell,
        actingSubject: Any?
    ): Sequence<LocatedEntity> = relevantCellEntities(cell, actingSubject)
        .filter { blockingValue(it)() > 0.0f }

    override fun identifyBorderEntities(
        cell: Cell,
        placement: PropPlacement,
        orientation: RayOrientation,
        heading: Heading,
        actingSubject: Any?
    ): Sequence<LocatedEntity> = relevantBorderEntities(cell, placement, orientation, heading, actingSubject)
        .filter { blockingValue(it)(orientation, heading) > 0.0f }
}

object BallisticExtractor : SimpleBlockerValueExtractor(Entity::ballisticBlocking)

object HomingObjectMovement : SimpleMovementGraph(
    BallisticExtractor,
    null,
    1,
    1.0f,
    true
)
