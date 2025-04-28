package com.cerebrallychallenged.hypogean.pathfinding

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.BezierCurve
import com.cerebrallychallenged.jun.math.geo.curve.Curve

class CellPath(
        val source: Cell,

        // source is not included in waypoints.
        // target is included in waypoints.
        // waypoints must not be empty.
        val waypoints: List<Cell>
) {
    val target = waypoints.last()

    // Euclidean length of the path.
    val length: Float by lazy {
        var sum = 0.0f
        var prev = source.position
        for (cell in waypoints) {
            val pos = cell.position
            sum += prev.distanceTo(pos)
            prev = pos
        }
        sum
    }

    /**
     * The heading closest to the final rotation of an object moving along this path.
     */
    val finalHeading: Angle by lazy {
        val size = waypoints.size
        val preLastPos = (if (size > 1) waypoints[size - 2] else source).position
        (target.position - preLastPos).toDouble().angle()
    }

    val tail: CellPath?
        get() = if (waypoints.size > 1) CellPath(waypoints.first(), waypoints.drop(1)) else null

    override fun toString(): String =
            "Path[source=${source.position}, waypoints=${waypoints.joinToString(", "){ it.position.toString() }}]"
}

// Higher value means smoother curves, but if it is too smooth, the curve could cut a corner.
internal const val CONTROL_POINT_DISTANCE = 0.4f

fun CellPath.toCurve(controlPointDistance: Float = CONTROL_POINT_DISTANCE): BezierCurve<Vec3f> {
    var prev: Vec3f? = null
    var current = source.basePoint
    val builder = BezierCurve.from(current)
    for (cell in waypoints) {
        val next = cell.basePoint
        if (prev != null) {
            builder.lineTo(current + (prev - current).normalized() * controlPointDistance)
            builder.quadTo(current, current + (next - current).normalized() * controlPointDistance)
        }
        prev = current
        current = next
    }
    return builder.lineTo(current).build()
}

fun List<Vec3f>.toCurve(controlPointDistance: Float = CONTROL_POINT_DISTANCE): Curve<Vec3f> {
    val iter = iterator()

    var prev: Vec3f? = null
    var current = iter.next()
    val builder = BezierCurve.from(current)
    for (next in iter) {
        if (prev != null) {
            builder.lineTo(current + (prev - current).normalized() * controlPointDistance)
            builder.quadTo(current, current + (next - current).normalized() * controlPointDistance)
        }
        prev = current
        current = next
    }
    return builder.lineTo(current).build()
}

fun List<CellPath>.mergePaths(): CellPath {
    val waypoints = mutableListOf<Cell>()
    for (path in this) {
        waypoints.addAll(path.waypoints)
    }
    return CellPath(first().source, waypoints)
}
