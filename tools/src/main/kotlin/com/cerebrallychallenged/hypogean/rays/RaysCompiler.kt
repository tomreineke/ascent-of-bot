package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.hypogean.Heading
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.math.times
import com.cerebrallychallenged.jun.util.ArrayND
import com.cerebrallychallenged.jun.util.sumByFloat
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import java.util.zip.GZIPOutputStream
import kotlin.math.floor

// 2^(-20)
private const val ADJUSTING_FACTOR: Double = 0.00000095367431640625

// 2^(-10)
private const val MOVER_CONTRACTION = 0.0009765625

// Minimal required area so that two geometries are considered overlapping.
// intersects and intersectsInterior alone have too many false positives due to numerical errors.
// 2^(-16)
private const val INTERSECTION_AREA_THRESHOLD = 0.0000152587890625

// Ensures that all section areas sum up to exactly 1.0.
private fun adjustArea(area: Double): Double = ADJUSTING_FACTOR * floor(area / ADJUSTING_FACTOR)

fun main(args: Array<String>) {
    val rayStencil = RaysCompiler(40, 3600).createRayStencil()
    println("Minimal section area is ${rayStencil.exposureRaySectionAreas().minOrNull()}")
    FileOutputStream(args[0]).use { baseStream->
        val outputStream: OutputStream = if (RayStencil.COMPRESSED) GZIPOutputStream(baseStream) else baseStream
        DataOutputStream(outputStream).writeRayStencil(rayStencil)
    }
}

class RaysCompiler(private val horizontalRadius: Int, private val angleCount: Int) {
    private val preBlockers: List<PreBlocker> = let {
        val list = mutableListOf<PreBlocker>()
        for (y in -horizontalRadius..horizontalRadius) {
            for (x in -horizontalRadius..horizontalRadius) {
                if (x != 0 || y != 0) {
                    list.add(PreCellBlocker(vec(x, y)))
                }
                if (x <= 0) {
                    list.add(PreWallBlocker(vec(x, y), Heading.SOUTH_EAST))
                }
                if (x >= 0) {
                    list.add(PreWallBlocker(vec(x, y), Heading.NORTH_WEST))
                }
                if (y <= 0) {
                    list.add(PreWallBlocker(vec(x, y), Heading.SOUTH_WEST))
                }
                if (y >= 0) {
                    list.add(PreWallBlocker(vec(x, y), Heading.NORTH_EAST))
                }
            }
        }
        list.sort()
        for ((index, preBlocker) in list.withIndex()) {
            preBlocker.index = index
        }
        list
    }

    fun createRayStencil(): RayStencil = RayStencil(
            preBlockers.map { it.toBlocker() },
            (1..4).map { createMoveRays(it) },
            createExposureRays(),
            createHitRays()
    )

    private fun createMoveRays(moverSize: Int): ArrayND<Vec2i, MoveRay> {
        val radius = moverSize * 0.5 - MOVER_CONTRACTION
        val shiftAmount = (moverSize - 1.0) * 0.5
        val shift = vec(shiftAmount, shiftAmount)
        val sourceCircle = Geometry2d.circle(shift, radius)
        return ArrayND.create(Bounds.centered(Vec2i.ZERO, Vec2i.ONE * horizontalRadius)) { relativePosition ->
//            if (moverSize != 1 || relativePosition != vec(-5, 0)) {
//                return@create MoveRay(listOf())
//            }
            val shiftedRelativeTarget = relativePosition.toDouble() + shift
            val processedPreBlockers = mutableSetOf<PreBlocker>()
            val segments = bresenham(relativePosition).mapNotNull { intermediateRelativePosition ->
                val delta = intermediateRelativePosition.toDouble()
                val shiftedDelta = delta + shift
                val projectedPoint = shiftedDelta.clampedProjectOnSegment(shift, shiftedRelativeTarget)
                val targetCircle = Geometry2d.circle(projectedPoint, radius)
                val pathRegion = sourceCircle.union(targetCircle).convexHull

                val additionalPreBlockers = preBlockers.filter {
                    pathRegion.intersects(it.bounds) && it !in processedPreBlockers
                }
                processedPreBlockers.addAll(additionalPreBlockers)
                if (intermediateRelativePosition.isZero) return@mapNotNull null


                val blockers = additionalPreBlockers
                        .sortedBy { it.center.projectionAlphaOnSegment(shift, shiftedDelta) }
                        .map { it.toBlocker() }
                MoveRay.InternalSegment(intermediateRelativePosition, projectedPoint.toFloat(), blockers)
            }.toList()
            MoveRay(segments)
        }
    }

    private fun createExposureRays(): ArrayND<Vec2i, ExposureRay>{
        return ArrayND.create(Bounds.centered(Vec2i.ZERO, Vec2i.ONE * horizontalRadius)) { relativePosition ->
            if (relativePosition.isZero) {
                ExposureRay(listOf(), listOf())
            } else {
                val relativePosition2d = relativePosition.toDouble()
                val targetBounds = Bounds.centered(relativePosition2d, Vec2d.ONE_HALF)
                val targetGeometry = targetBounds.toGeometry2d()
                val lookingRegion = targetGeometry.union(Vec2d.ZERO).convexHull
                val innerAnglesToDefiningPoint = TreeMap<Angle, Vec2d>()
                val involvedBlockers = mutableListOf<PreBlocker>()
                for (blocker in preBlockers) {
                    if (blocker is PreCellBlocker && blocker.relativePosition == relativePosition) continue
                    val shadow = blocker.shadow
                    if (
                            shadow.geometry.intersectsInterior(targetGeometry)
                            && shadow.geometry.intersect(targetGeometry).area > INTERSECTION_AREA_THRESHOLD
                    ) {
                        involvedBlockers.add(blocker)
                        if (lookingRegion.contains(shadow.start)) {
                            innerAnglesToDefiningPoint[shadow.startAngle] = shadow.start
                        }
                        if (lookingRegion.contains(shadow.end)) {
                            innerAnglesToDefiningPoint[shadow.endAngle] = shadow.end
                        }
                    }
                }
                val sections = createSections(innerAnglesToDefiningPoint.values, targetBounds)
                val innerAngleIndices = createInnerAngleIndices(innerAnglesToDefiningPoint.keys)
                val entries = mutableListOf<ExposureRay.Entry>()
                for (blocker in involvedBlockers) {
                    val shadow = blocker.shadow
                    val fromSectionIndex = innerAngleIndices[shadow.startAngle] ?: 0
                    val toSectionIndex = innerAngleIndices[shadow.endAngle] ?: sections.size
                    entries.add(ExposureRay.Entry(
                            blocker.toBlocker(),
                            fromSectionIndex until toSectionIndex
                    ))
                }
                require(sections.sumByFloat { it.area } == 1.0f)
                ExposureRay(sections, entries)
            }
        }
    }

    private fun createInnerAngleIndices(innerAngles: Iterable<Angle>): Map<Angle, Int>
            = innerAngles.withIndex().associate { (index, angle) -> Pair(angle, index + 1) }

    private fun createSections(
            definingPoints: Collection<Vec2d>,
            targetBounds: Bounds<Vec2d>
    ): List<ExposureRay.Section> {
        var remainingArea = 1.0
        val sections = mutableListOf<ExposureRay.Section>()
        val targetShadow = Shadow.create(targetBounds)
        val targetGeometry = targetBounds.toGeometry2d()
        var startGeo = Geometry2d.halfPlane(Vec2d.ZERO.lineSegmentTo(targetShadow.start))
        for (definingPoint in definingPoints) {
            val segment = definingPoint.lineSegmentTo(Vec2d.ZERO)
            val endGeo = Geometry2d.halfPlane(segment)
            val sectionGeometry = startGeo.intersect(endGeo).intersect(targetGeometry)
            val area = adjustArea(sectionGeometry.area)
            if (area < INTERSECTION_AREA_THRESHOLD) {
                throw RuntimeException("Section area ${sectionGeometry.area} too small")
            }
            sections.add(ExposureRay.Section(area.toFloat(), sectionGeometry.centroid.toFloat()))
            remainingArea -= area
            startGeo = Geometry2d.halfPlane(segment.reverse())
        }
        val sectionGeometry = startGeo
                .intersect(Geometry2d.halfPlane(targetShadow.end.lineSegmentTo(Vec2d.ZERO)))
                .intersect(targetGeometry)
        sections.add(ExposureRay.Section(remainingArea.toFloat(), sectionGeometry.centroid.toFloat()))
        return sections
    }

    private fun createHitRays(): List<HitRay> {
        val step = Angle.DEGREE_360 / angleCount
        return (0 until angleCount).map { i ->
            val angle = i * step

            // Factor 2.0 is arbitrary just so that the ray extends beyond all preBlockers.
            val target = polar(2.0 * horizontalRadius, angle)
            val lineSegment = Vec2d.ZERO.lineSegmentTo(target).toGeometry()
            HitRay(preBlockers.filter { lineSegment.intersects(it.bounds) }.map { blocker ->
                val intersection = lineSegment.intersect(blocker.bounds)
                //noinspection OptionalGetWithoutIsPresent
                val hitPosition = intersection.extremePoints.minByOrNull { it.length }!!
                HitRay.Entry(blocker.toBlocker(), hitPosition.toFloat())
            })
        }
    }
}
