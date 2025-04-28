package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.clamp

data class LineSegment2d(val startPoint: Vec2d, val endPoint: Vec2d) {
    fun reverse(): LineSegment2d = LineSegment2d(endPoint, startPoint)

    fun toGeometry(): Geometry2d = Geometry2d(Geometry2d.createGeometry(this))
}

/**
 * Computes the coefficient for this point projected onto the line defined by the specified points.
 * A result of `0.0` and `1.0` means that `this` is projected onto `startPoint` and `endPoint`, respectively.
 * A result strictly between `0.0` and `1.0` indicates a projection onto the interior of the line segment between
 * `startPoint` and `endPoint`.
 * Values less than `0.0` or greater than `1.0` indicate a projection on the line outside the specified segment.
 */
fun <V : DoubleVector<*, *, V>> V.projectionAlphaOnSegment(startPoint: V, endPoint: V): Double {
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
fun Vec2d.projectionAlphaOn(lineSegment: LineSegment2d): Double =
        projectionAlphaOnSegment(lineSegment.startPoint, lineSegment.endPoint)

/**
 * Projects this point onto the line defined by the specified points.
 */
fun <V : DoubleVector<*, *, V>> V.projectOnLine(startPoint: V, endPoint: V): V =
        startPoint.interpolate(projectionAlphaOnSegment(startPoint, endPoint), endPoint)

/**
 * Projects this point onto the line defined by the specified line segment.
 * Note that the projection may lay outside the specified segment.
 */
fun Vec2d.projectOn(lineSegment: LineSegment2d): Vec2d =
        projectOnLine(lineSegment.startPoint, lineSegment.endPoint)

/**
 * Projects this point onto the line segment between the specified points,
 * taking an endpoint if the projection would lay outside the segment.
 * In other words, returns the point of the specified line segment closest to this point
 * with respect to the Euclidean distance.
 */
fun <V : DoubleVector<*, *, V>> V.clampedProjectOnSegment(startPoint: V, endPoint: V): V =
        startPoint.interpolate(clamp(projectionAlphaOnSegment(startPoint, endPoint), 0.0, 1.0), endPoint)

/**
 * Projects this point onto the specified line segment,
 * taking an endpoint if the projection would lay outside the segment.
 * In other words, returns the point of the specified line segment closest to this point
 * with respect to the Euclidean distance.
 */
fun Vec2d.clampedProjectOn(lineSegment: LineSegment2d): Vec2d =
        clampedProjectOnSegment(lineSegment.startPoint, lineSegment.endPoint)