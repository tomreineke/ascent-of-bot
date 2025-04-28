package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.zShift
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.times
import kotlin.math.max

data class MoveSegment(val target: Cell?, val projectedPoint: Vec2f, val blockers: List<DeferredBlocker>)

sealed class DeferredBlocker(
        protected val queryParameters: QueryParameters,
        protected val relativePosition: Vec2i
) {
    val position = with(queryParameters) { relativePosition.toAbsolute() }

    abstract val obstruction: Float

    abstract val cell: Cell?

    abstract val basePoint: Vec3f
}

class DeferredCellBlocker(
        queryParameters: QueryParameters,
        relativePosition: Vec2i
) : DeferredBlocker(queryParameters, relativePosition) {
    override val obstruction: Float
        get() = queryParameters.cellValue(relativePosition)

    override val cell: Cell?
        get() = queryParameters.cell(relativePosition)

    override val basePoint: Vec3f
        get() = cell?.basePoint ?: position.toFloat().append(0.0f)
}

internal class DeferredBorderBlocker(
        queryParameters: QueryParameters,
        relativePosition: Vec2i,
        private val heading: Heading
) : DeferredBlocker(queryParameters, relativePosition) {
    override val obstruction: Float
        get() = queryParameters.doubleSidedBorderValue(relativePosition, heading)

    override val cell: Cell?
        get() = null

    override val basePoint: Vec3f
        get() {
            val sourceZShift = queryParameters.cell(relativePosition)?.zShift
            val targetZShift = queryParameters.cell(relativePosition + heading.delta)?.zShift
            val zShift = when {
                sourceZShift != null && targetZShift != null -> max(sourceZShift, targetZShift)
                sourceZShift == null && targetZShift != null -> targetZShift
                sourceZShift != null && targetZShift == null -> sourceZShift
                else -> 0.0f
            }
            return (position + 0.5f * heading.delta).append(zShift)
        }
}
