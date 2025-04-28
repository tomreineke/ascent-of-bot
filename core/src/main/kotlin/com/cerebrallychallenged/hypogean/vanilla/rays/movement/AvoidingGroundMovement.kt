package com.cerebrallychallenged.hypogean.vanilla.rays.movement

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.base.PropPlacement
import com.cerebrallychallenged.hypogean.pathfinding.NO_EDGE
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.RayOrientation
import kotlin.math.max

abstract class AvoidingExtractor(val base: BlockerValueExtractor) : BlockerValueExtractor() {
    abstract fun avoidance(cell: Cell, actingSubject: Any?): Float

    override fun cellValue(cell: Cell, actingSubject: Any?): Float {
        val baseValue = base.cellValue(cell, actingSubject)
        if (baseValue == NO_EDGE) return NO_EDGE
        return max(baseValue, avoidance(cell, actingSubject))
    }

    override fun borderValue(
            cell: Cell,
            placement: PropPlacement,
            orientation: RayOrientation,
            heading: Heading,
            actingSubject: Any?
    ): Float = base.borderValue(cell, placement, orientation, heading, actingSubject)
}