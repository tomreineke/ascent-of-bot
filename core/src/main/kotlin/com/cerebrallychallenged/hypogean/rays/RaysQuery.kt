package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import kotlin.math.max

data class QueryParameters(
        val sourcePosition: Vec2i,
        val blockerValueExtractor: BlockerValueExtractor,
        val actingSubject: Any?,
        val world: World
) {
    @Suppress("NOTHING_TO_INLINE")
    internal inline fun Vec2i.toAbsolute(): Vec2i = sourcePosition + this

//    @Suppress("NOTHING_TO_INLINE")
//    internal inline fun Vec2i.toRelative(): Vec2i = this - sourcePosition

    internal fun cellValue(relativePosition: Vec2i): Float =
            blockerValueExtractor.cellValue(world, relativePosition.toAbsolute(), actingSubject)

    internal fun cell(relativePosition: Vec2i): Cell? = world.cellAt(relativePosition.toAbsolute())

    internal fun doubleSidedBorderValue(relativePosition: Vec2i, heading: Heading): Float =
            blockerValueExtractor.doubleSidedBorderValue(world, relativePosition.toAbsolute(), heading, actingSubject)
}

class RaysQuery internal constructor(private val queryParameters: QueryParameters, private val stencil: RayStencil) {
    private val cache = FloatArray(stencil.blockers.size) { Float.NaN }

    val sourcePosition = queryParameters.sourcePosition

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Vec2i.toRelative(): Vec2i = this - sourcePosition

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Vec2f.toAbsolute(): Vec2f = sourcePosition + this

    @Suppress("NOTHING_TO_INLINE")
    private inline fun Vec2f.toRelative(): Vec2f = this - sourcePosition

    private fun compute(blocker: Blocker): Float {
        val index = blocker.index
        var value = cache[index]
        if (value.isNaN()) {
            value = blocker.computeValue(queryParameters)
            cache[index] = value
        }
        return value
    }

    fun hasRay(targetPosition: Vec2i): Boolean {
        val delta = targetPosition.toRelative()
        return max(delta.x, delta.y) <= stencil.horizontalRadius
    }

    fun computeMoveSegments(targetPosition: Vec2i, moverSize: Int): Sequence<MoveSegment> =
            stencil.moveRay(targetPosition.toRelative(), moverSize).segments(queryParameters)

    fun computeDeferredBlockers(targetPosition: Vec2i, moverSize: Int): Sequence<DeferredBlocker> =
            stencil.moveRay(targetPosition.toRelative(), moverSize).deferredBlockers(queryParameters)

//    fun computeBlockersWithValues(targetPosition: Vec2i, moverSize: Int): Sequence<BlockerWithValue> = stencil
//            .moveRay(targetPosition.toRelative(), moverSize)
//            .blockersWithValues(this, sourcePosition, ::compute)

    fun computeBlockerValues(targetPosition: Vec2i, moverSize: Int): Sequence<Float> =
            stencil.moveRay(targetPosition.toRelative(), moverSize).blockerValues(::compute)

    /**
     * Returns a sequence of all existing cells, walls, and actors on the line between source and target position.
     * @param targetPosition the target position.
     * @param moverSize size of the moving subject.
     * @return a sequence of all existing cells, walls, and actors on the line between source and target position.
     */
//    fun computeBlockers(targetPosition: Vec2i, moverSize: Int): Sequence<LocatedEntity> =
//            stencil.moveRay(targetPosition.toRelative(), moverSize).blockers(this)

//    fun computeCellBlockers(targetPosition: Vec2i, moverSize: Int): Sequence<Cell> = stencil
//            .moveRay(targetPosition.toRelative(), moverSize)
//            .cellBlockers(this)

    /**
     * Returns a value between 0.0 (hidden behind blockers) and 1.0 (fully exposed)
     */
    fun computeExposure(targetPosition: Vec2i, strength: Float): Float =
            stencil.exposureRay(targetPosition.toRelative()).computeExposure(strength, ::compute)

    fun computeCentroid(targetPosition: Vec2i, strength: Float): Vec2f? =
            stencil.exposureRay(targetPosition.toRelative()).computeCentroid(strength, ::compute)?.toAbsolute()

    fun computeHit(angle: Angle, sumThreshold: Float, maxThreshold: Float, maxDistance: Float): HitResult =
            stencil.hitRay(angle).computeHit(queryParameters, sumThreshold, maxThreshold, maxDistance, ::compute)

    fun computeHit(target: Vec2f, sumThreshold: Float, maxThreshold: Float): HitResult {
        val delta = target.toRelative()
        return stencil
                .hitRay(delta.angle())
                .computeHit(queryParameters, sumThreshold, maxThreshold, delta.length, ::compute)
    }
}