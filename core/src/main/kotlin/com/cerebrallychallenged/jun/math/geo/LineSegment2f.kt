package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.clamp

data class LineSegment2f(val startPoint: Vec2f, val endPoint: Vec2f) {
    fun reverse(): LineSegment2f = LineSegment2f(endPoint, startPoint)
}

/**
 * Computes the coefficient for this point projected onto the line defined by the specified points.
 * A result of `0.0` and `1.0` means that `this` is projected onto `startPoint` and `endPoint`, respectively.
 * A result strictly between `0.0` and `1.0` indicates a projection onto the interior of the line segment between
 * `startPoint` and `endPoint`.
 * Values less than `0.0` or greater than `1.0` indicate a projection on the line outside the specified segment.
 */
fun <V : FloatVector<*, V, *>> V.projectionAlphaOnSegment(startPoint: V, endPoint: V): Float {
    val delta = endPoint - startPoint
    val otherDelta = this - startPoint
    return (delta dot otherDelta) / delta.squaredLength
}

/**
 * Computes the coefficient for the this point projected onto the line defined by the specified line segment.
 * A result of `0.0` and `1.0` means that `this` is projected onto `startPoint` and `endPoint`, respectively.
 * A result strictly between `0.0` and `1.0` indicates a projection onto the interior of the specified line segment.
 * Values less than `0.0` or greater than `1.0` indicate a projection on the line outside the specified segment.
 */
fun Vec2f.projectionAlphaOn(lineSegment: LineSegment2f): Float =
        projectionAlphaOnSegment(lineSegment.startPoint, lineSegment.endPoint)

/**
 * Projects this point onto the line defined by the specified points.
 */
fun <V : FloatVector<*, V, *>> V.projectOnLine(startPoint: V, endPoint: V): V =
        startPoint.interpolate(projectionAlphaOnSegment(startPoint, endPoint), endPoint)

/**
 * Projects this point onto the line defined by the specified line segment.
 * Note that the projection may lay outside the specified segment.
 */
fun Vec2f.projectOn(lineSegment: LineSegment2f): Vec2f =
        projectOnLine(lineSegment.startPoint, lineSegment.endPoint)

/**
 * Projects this point onto the line segment between the specified points,
 * taking an endpoint if the projection would lay outside the segment.
 * In other words, returns the point of the specified line segment closest to this point
 * with respect to the Euclidean distance.
 */
fun <V : FloatVector<*, V, *>> V.clampedProjectOnSegment(startPoint: V, endPoint: V): V =
        startPoint.interpolate(clamp(projectionAlphaOnSegment(startPoint, endPoint), 0.0f, 1.0f), endPoint)

/**
 * Projects this point onto the specified line segment,
 * taking an endpoint if the projection would lay outside the segment.
 * In other words, returns the point of the specified line segment closest to this point
 * with respect to the Euclidean distance.
 */
fun Vec2f.clampedProjectOn(lineSegment: LineSegment2f): Vec2f =
        clampedProjectOnSegment(lineSegment.startPoint, lineSegment.endPoint)
