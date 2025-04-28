package com.cerebrallychallenged.jun.math.geo

import org.locationtech.jts.algorithm.ConvexHull
import org.locationtech.jts.geom.*
import java.lang.Long.highestOneBit
import kotlin.math.ceil
import kotlin.math.max

private fun convert(v: Vec2d): Coordinate {
    return Coordinate(v.x, v.y)
}

private fun convert(v: Coordinate): Vec2d {
    return vec(v.getX(), v.getY())
}

// Lower bound of the length of the finite geometries faking infinite geometries, e.g. lines and half-planes.
private const val EXTENSION = (1 shl 16).toDouble()

private val GEOMETRY_FACTORY = GeometryFactory()

class Geometry2d internal constructor(private val geometry: Geometry) {

    companion object {
        internal fun createGeometry(point: Vec2d): Geometry = GEOMETRY_FACTORY.createPoint(convert(point))

        internal fun createGeometry(bounds: Bounds<Vec2d>): Geometry {
            return GEOMETRY_FACTORY.toGeometry(
                    if (bounds.isEmpty)
                        Envelope()
                    else
                        Envelope(convert(bounds.min), convert(bounds.max))
            )
        }

        internal fun createGeometry(lineSegment: LineSegment2d): Geometry {
            return GEOMETRY_FACTORY.createLineString(arrayOf(
                    convert(lineSegment.startPoint),
                    convert(lineSegment.endPoint)
            ))
        }

        fun convexHull(points: Iterable<Vec2d>): Geometry2d {
            val list = CoordinateList()
            points.forEach { p -> list.add(convert(p)) }
            return Geometry2d(ConvexHull(list.toCoordinateArray(), GEOMETRY_FACTORY).convexHull)
        }

        fun halfPlane(line: LineSegment2d): Geometry2d {
            val startPoint = line.startPoint
            val delta = line.endPoint - startPoint
            require(!delta.isZero)
            val scale = (highestOneBit(ceil(max(EXTENSION, EXTENSION / delta.length)).toLong()) shl 1).toDouble()
            val scaledDelta = delta * scale
            val orthoScaledDelta = scaledDelta.turnCounterClockwise()
            val source = startPoint - scaledDelta
            val convertedSource = convert(source)
            val target = startPoint + scaledDelta
            return Geometry2d(GEOMETRY_FACTORY.createPolygon(arrayOf(
                    convertedSource,
                    convert(target),
                    convert(target + orthoScaledDelta),
                    convert(source + orthoScaledDelta),
                    convertedSource
            )))
        }

        fun circle(center: Vec2d, radius: Double): Geometry2d = Geometry2d(createGeometry(center).buffer(radius))
    }

    operator fun contains(other: Geometry2d): Boolean = geometry.contains(other.geometry)

    operator fun contains(point: Vec2d): Boolean = geometry.contains(createGeometry(point))

    operator fun contains(bounds: Bounds<Vec2d>): Boolean = geometry.contains(createGeometry(bounds))

    operator fun contains(lineSegment: LineSegment2d): Boolean = geometry.contains(createGeometry(lineSegment))

    fun covers(other: Geometry2d): Boolean = geometry.covers(other.geometry)

    fun covers(point: Vec2d): Boolean = geometry.covers(createGeometry(point))

    fun covers(bounds: Bounds<Vec2d>): Boolean = geometry.covers(createGeometry(bounds))

    fun covers(lineSegment: LineSegment2d): Boolean = geometry.covers(createGeometry(lineSegment))

    fun intersect(other: Geometry2d): Geometry2d = Geometry2d(geometry.intersection(other.geometry))

    fun intersect(bounds: Bounds<Vec2d>): Geometry2d = Geometry2d(geometry.intersection(createGeometry(bounds)))

    fun intersect(lineSegment: LineSegment2d): Geometry2d
            = Geometry2d(geometry.intersection(createGeometry(lineSegment)))

    fun intersects(other: Geometry2d): Boolean = geometry.intersects(other.geometry)

    fun intersects(bounds: Bounds<Vec2d>): Boolean = geometry.intersects(createGeometry(bounds))

    fun intersects(lineSegment: LineSegment2d): Boolean = geometry.intersects(createGeometry(lineSegment))

    private fun intersectsInterior(otherGeometry: Geometry): Boolean
            = geometry.relate(otherGeometry).matches("T********")

    fun intersectsInterior(other: Geometry2d): Boolean = intersectsInterior(other.geometry)

    fun intersectsInterior(bounds: Bounds<Vec2d>): Boolean = intersectsInterior(createGeometry(bounds))

    fun intersectsInterior(lineSegment: LineSegment2d): Boolean = intersectsInterior(createGeometry(lineSegment))

    fun union(other: Geometry2d): Geometry2d = Geometry2d(geometry.union(other.geometry))

    fun union(point: Vec2d): Geometry2d = Geometry2d(geometry.union(createGeometry(point)))

    fun union(bounds: Bounds<Vec2d>): Geometry2d = Geometry2d(geometry.union(createGeometry(bounds)))

    fun union(lineSegment: LineSegment2d): Geometry2d = Geometry2d(geometry.union(createGeometry(lineSegment)))

    fun buffer(distance: Double): Geometry2d = Geometry2d(geometry.buffer(distance))

    fun difference(other: Geometry2d): Geometry2d = Geometry2d(geometry.difference(other.geometry))

    fun difference(bounds: Bounds<Vec2d>): Geometry2d = Geometry2d(geometry.difference(createGeometry(bounds)))

    fun difference(lineSegment: LineSegment2d): Geometry2d
            = Geometry2d(geometry.difference(createGeometry(lineSegment)))

    val area: Double
        get() = geometry.area

    val centroid: Vec2d by lazy { convert(geometry.centroid.coordinate) }

    val convexHull: Geometry2d by lazy { Geometry2d(geometry.convexHull()) }

    val extremePoints: Sequence<Vec2d>
        get() = sequenceOf(*geometry.coordinates).map { convert(it) }
}
