package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.base.PropPlacement
import com.cerebrallychallenged.jun.math.geo.Vec2i

/**
 * Value usually returned by [BlockerValueExtractor] to indicate complete blockage.
 */
const val HIGH_VALUE: Float = 10000.0f

/**
 * Computes how strong a blocker (cell or wall in the way of a ray) is.
 *
 * All methods have an optional `actingSubject` parameter representing something that 'rides' on the ray.
 * It could be an Entity like a moving Actor or a flying rocket, or a non-entity object like an explosion.
 * Examples for blocker values depending on the acting subject are a gate / force field letting through only actors
 * of the same faction, a wall transparent to certain kinds of radiation, or the fact that an actor cannot block
 * its own way.
 */
abstract class BlockerValueExtractor(private val missingCellValue: Float = HIGH_VALUE) {
    fun cellValue(world: World, position: Vec2i, actingSubject: Any?): Float = world.cellAt(position)?.let { cell ->
        cellValue(cell, actingSubject)
    } ?: missingCellValue

    abstract fun cellValue(cell: Cell, actingSubject: Any?): Float

    open fun identifyCellEntities(cell: Cell, actingSubject: Any?): Sequence<LocatedEntity> = sequenceOf()

    fun doubleSidedBorderValue(
            world: World,
            position: Vec2i,
            heading: Heading,
            actingSubject: Any?
    ): Float {
        var result = 0.0f
        world.cellAt(position)?.let { cell ->
            result += borderValue(
                cell,
                PropPlacement.Border(heading),
                RayOrientation.Outbound,
                heading,
                actingSubject
            )
        }
        world.cellAt(position + heading.delta)?.let { cell ->
            result += borderValue(
                cell,
                PropPlacement.Border(heading.opposite()),
                RayOrientation.Inbound,
                heading,
                actingSubject
            )
        }
        return result
    }

    abstract fun borderValue(
        cell: Cell,
        placement: PropPlacement,
        orientation: RayOrientation,
        heading: Heading,
        actingSubject: Any?
    ): Float

    open fun identifyBorderEntities(
        cell: Cell,
        placement: PropPlacement,
        orientation: RayOrientation,
        heading: Heading,
        actingSubject: Any?
    ): Sequence<LocatedEntity> = sequenceOf()
}

class BlockerValueExtractors : SimpleObjectRegistry<BlockerValueExtractor>()

object ZeroExtractor : BlockerValueExtractor(0.0f) {
    override fun cellValue(cell: Cell, actingSubject: Any?): Float = 0.0f

    override fun borderValue(
        cell: Cell,
        placement: PropPlacement,
        orientation: RayOrientation,
        heading: Heading,
        actingSubject: Any?
    ) = 0.0f
}
