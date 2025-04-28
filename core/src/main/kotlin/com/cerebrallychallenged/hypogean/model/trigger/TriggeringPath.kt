package com.cerebrallychallenged.hypogean.model.trigger

import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.reconOf
import com.cerebrallychallenged.hypogean.model.zShift
import com.cerebrallychallenged.hypogean.pathfinding.CONTROL_POINT_DISTANCE
import com.cerebrallychallenged.hypogean.pathfinding.CellPath
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.rays.DeferredBlocker
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.BezierCurve
import com.cerebrallychallenged.jun.math.geo.curve.Curve
import com.cerebrallychallenged.jun.math.geo.curve.toFollowCurve
import com.cerebrallychallenged.jun.math.geo.vec

class CurvedMoveSegment(val target: Cell, val blockers: List<DeferredBlocker>, curve: Curve<Vec3f>) {
    val positionCurve = curve.reparameterizeAtUnitSpeed(0.01f, /*MOVEMENT_SPEED*/ 3.0f)

    val rotationCurve = positionCurve.toFollowCurve(Vec3f.UNIT_Z).flatten(0.01f)

    val finalHeading: Angle
        get() {
            val vertices = positionCurve.vertices.toList()
            val count = vertices.size
            return (vertices[count - 1].xy - vertices[count - 2].xy).angle()
        }

    context(WorldContext)
    fun isVisibleFor(faction: Faction): Boolean {
        val factionEntity = faction.entity
        return (blockers.asSequence().mapNotNull { it.cell } + target).any { factionEntity.reconOf(it) == Recon.Visible }
    }
}

fun CellPath.toSegments(
        blockerValueExtractor: BlockerValueExtractor,
        moverSize: Int,
        actingSubject: Any?
): Sequence<CurvedMoveSegment> = sequence {
    if (waypoints.isEmpty()) return@sequence
    var prevWaypoint = source
    val world = prevWaypoint.world
    var pathBuilder = BezierCurve.from(prevWaypoint.basePoint.translateByMoverSize(moverSize))

    for ((waypointIndex, waypoint) in waypoints.withIndex()) {
        val isLastWaypoint = waypointIndex == waypoints.size - 1
        val segments =
                world.queryRays(prevWaypoint.position, blockerValueExtractor, actingSubject)
                        .computeMoveSegments(waypoint.position, moverSize)
        for (segment in segments) {
            val cell = segment.target ?: return@sequence
            val zShift = cell.zShift
            val curve: Curve<Vec3f>
            if (!isLastWaypoint && cell == waypoint) {
                val currentPoint = waypoint.position2f
                val preDelta = (currentPoint - prevWaypoint.position2f).normalized() * CONTROL_POINT_DISTANCE
                val prePoint = currentPoint - preDelta
                val postDelta =
                        (waypoints[waypointIndex + 1].position2f - currentPoint).normalized() * CONTROL_POINT_DISTANCE
                val postPoint = currentPoint + postDelta
                val preControl = prePoint.interpolate(0.5f, currentPoint).append(zShift)
                val postControl = currentPoint.interpolate(0.5f, postPoint).append(zShift)
                val mid = preControl.interpolate(0.5f, postControl)
                curve = pathBuilder.lineTo(prePoint.append(zShift)).quadTo(preControl, mid).build()
                pathBuilder = BezierCurve.from(mid).quadTo(postControl, postPoint.append(zShift))
            } else {
                val currentPoint = segment.projectedPoint.append(zShift)
                curve = pathBuilder.lineTo(currentPoint).build()
                pathBuilder = BezierCurve.from(currentPoint)
            }
            yield(CurvedMoveSegment(cell, segment.blockers, curve))
        }
        prevWaypoint = waypoint
    }
}

private val halfSize = vec(0.5f, 0.5f, 0.0f)
private fun Vec3f.translateByMoverSize(moverSize: Int): Vec3f = if (moverSize == 1) {
    this
} else {
    this + halfSize * (moverSize - 1)
}

fun CurvedMoveSegment.intersectedCells(): Sequence<Cell> = blockers.asSequence().mapNotNull { it.cell }

fun Iterable<CurvedMoveSegment>.intersectedCells(): Sequence<Cell> = asSequence().flatMap { it.intersectedCells() }
