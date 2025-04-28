package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2d
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import kotlin.math.abs

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
sealed class PreBlocker(val relativePosition: Vec2i, val center: Vec2d, radii: Vec2d) : Comparable<PreBlocker> {
    var index: Int = 0

    val bounds: Bounds<Vec2d> = Bounds.centered(center, radii)

    val sourceDistance = center.length

    val shadow = Shadow.create(bounds)

    override fun compareTo(other: PreBlocker): Int = sourceDistance.compareTo(other.sourceDistance)

    abstract fun toBlocker(): Blocker

    abstract fun isPartOf(bounds: Bounds<Vec2i>): Boolean
}

class PreCellBlocker(
        relativePosition: Vec2i
) : PreBlocker(relativePosition, relativePosition.toDouble(), Vec2d.ONE_HALF) {
    override fun toBlocker(): CellBlocker = CellBlocker(index, relativePosition)

    override fun isPartOf(bounds: Bounds<Vec2i>): Boolean = relativePosition in bounds
}

class PreWallBlocker(
        relativePosition: Vec2i,
        private val heading: Heading
) : PreBlocker(
        relativePosition,
        relativePosition + heading.delta * 0.5,
        heading.delta.turnCounterClockwise().mapReduce(::Vec2d) { abs(it * 0.5) }
) {
    override fun toBlocker(): BorderBlocker = BorderBlocker(index, relativePosition, heading)

    override fun isPartOf(bounds: Bounds<Vec2i>): Boolean
            = relativePosition in bounds && (relativePosition + heading.delta) in bounds
}
