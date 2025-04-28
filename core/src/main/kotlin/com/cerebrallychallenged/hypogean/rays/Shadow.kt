package com.cerebrallychallenged.hypogean.rays

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Geometry2d
import com.cerebrallychallenged.jun.math.geo.Vec2d
import com.cerebrallychallenged.jun.math.geo.extremePoints
import com.cerebrallychallenged.jun.math.geo.toGeometry2d

class Shadow(val start: Vec2d, val end: Vec2d, val geometry: Geometry2d) {
    companion object {
        @JvmStatic
        fun create(bounds: Bounds<Vec2d>): Shadow {
            val boundsGeometry = bounds.toGeometry2d()
            var start: Vec2d? = null
            var startGeo: Geometry2d? = null
            var end: Vec2d? = null
            var endGeo: Geometry2d? = null
            for (point in bounds.extremePoints) {
                val segment = Vec2d.ZERO.lineSegmentTo(point)
                val leftPlane = Geometry2d.halfPlane(segment)
                if (leftPlane.covers(boundsGeometry)) {
                    if (start != null) {
                        throw RuntimeException("Unexpected redundancy")
                    }
                    start = point
                    startGeo = leftPlane
                }
                val rightPlane = Geometry2d.halfPlane(segment.reverse())
                if (rightPlane.covers(boundsGeometry)) {
                    if (end != null) {
                        throw RuntimeException("Unexpected redundancy")
                    }
                    end = point
                    endGeo = rightPlane
                }
            }
            if (start == null || end == null || startGeo == null || endGeo == null) {
                throw RuntimeException()
            }
            return create(start, startGeo, end, endGeo)
        }

        private fun create(start: Vec2d, startGeo: Geometry2d, end: Vec2d, endGeo: Geometry2d): Shadow {
            val crossSegment = end.lineSegmentTo(start)
            val geometry
                    = startGeo
                            .intersect(endGeo)
                            .intersect(Geometry2d.halfPlane(crossSegment))
            return Shadow(start, end, geometry)
        }
    }

    val startAngle: Angle = start.angle()

    val endAngle: Angle = end.angle()
}
