package com.cerebrallychallenged.jun.math.geo

import java.io.DataInput
import java.io.DataOutput

sealed class Bounds<V : Vec<V, *, *, *>> {
    companion object {
        @JvmStatic
        fun empty2i(): Bounds<Vec2i> = Vec2i.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun empty3i(): Bounds<Vec3i> = Vec3i.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun empty4i(): Bounds<Vec4i> = Vec4i.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun empty2d(): Bounds<Vec2d> = Vec2d.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun empty3d(): Bounds<Vec3d> = Vec3d.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun empty4d(): Bounds<Vec4d> = Vec4d.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun empty2r(): Bounds<Vec2r> = Vec2r.VECTOR_SPACE.emptyBounds

        @JvmStatic
        fun <V: Vec<V, *, *, *>> byMinSize(min: V, size: V): Bounds<V> {
            val vectorSpace = min.vectorSpace
            require(vectorSpace.zero.isLessEqualThan(size)) { "size must not have negative components" }
            return NonEmptyBounds(min, min + size - vectorSpace.pointSize)
        }

        @JvmStatic
        fun <V: Vec<V, *, *, *>> centered(center: V, radii: V): Bounds<V> {
            require(center.vectorSpace.zero.isLessEqualThan(radii)) { "radii must not have negative components" }
            return NonEmptyBounds(center - radii, center + radii)
        }

        @JvmStatic
        fun <V: Vec<V, *, *, *>> of(first: V, vararg points: V): Bounds<V> {
            var result: Bounds<V> = first.singletonBounds()
            for (point in points) {
                result = result.extend(point)
            }
            return result
        }
    }

    abstract val isEmpty: Boolean

    abstract val min: V

    abstract val max: V

    abstract fun extend(point: V): Bounds<V>

    abstract fun union(other: Bounds<V>): Bounds<V>

    abstract fun intersect(other: Bounds<V>): Bounds<V>

    abstract fun intersects(other: Bounds<V>): Boolean

    abstract operator fun contains(point: V): Boolean

    abstract operator fun contains(other: Bounds<V>): Boolean

    abstract fun containsInterior(point: V): Boolean

    abstract val size: V

    abstract fun translate(delta: V): Bounds<V>
}

internal class EmptyBounds<V: Vec<V, *, *, *>>(override val size: V) : Bounds<V>() {
    override val isEmpty: Boolean
        get() = true

    override val min: V
        get() = throw NoSuchElementException("Empty bounds")

    override val max: V
        get() = throw NoSuchElementException("Empty bounds")

    override fun toString(): String = "Bounds(empty)"

    override fun extend(point: V): Bounds<V> = point.singletonBounds()

    override fun union(other: Bounds<V>): Bounds<V> = other

    override fun intersect(other: Bounds<V>): Bounds<V> = this

    override fun intersects(other: Bounds<V>): Boolean = false

    override operator fun contains(point: V): Boolean = false

    override operator fun contains(other: Bounds<V>): Boolean = other === this

    override fun containsInterior(point: V): Boolean = false

    override fun translate(delta: V): Bounds<V> = this
}

private class NonEmptyBounds<V: Vec<V, *, *, *>>(override val min: V, override val max: V) : Bounds<V>() {
    init {
        assert(min.isLessEqualThan(max)) { "min must be component-wise less or equal than max" }
    }

    override val isEmpty: Boolean
        get() = false

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is NonEmptyBounds<*> -> false
            else -> min == other.min && max == other.max
        }
    }

    override fun hashCode(): Int = 31 * min.hashCode() + max.hashCode()

    override fun toString(): String = "Bounds(min=$min, max=$max)"

    override fun extend(point: V): Bounds<V> = NonEmptyBounds(min.min(point), max.max(point))

    override fun union(other: Bounds<V>): Bounds<V> {
        return if (other.isEmpty) {
            this
        } else {
            NonEmptyBounds(min.min(other.min), max.max(other.max))
        }
    }

    override fun intersect(other: Bounds<V>): Bounds<V> {
        return if (other.isEmpty) {
            other
        } else {
            val newMin = min.max(other.min)
            val newMax = max.min(other.max)
            if (newMin.isLessEqualThan(newMax)) {
                NonEmptyBounds(newMin, newMax)
            } else {
                min.vectorSpace.emptyBounds
            }
        }
    }

    override fun intersects(other: Bounds<V>): Boolean = !intersect(other).isEmpty

    override fun contains(point: V): Boolean = min.isLessEqualThan(point) && point.isLessEqualThan(max)

    override fun contains(other: Bounds<V>): Boolean = min.isLessEqualThan(other.min) && other.max.isLessEqualThan(max)

    override fun containsInterior(point: V): Boolean = min.isLessThan(point) && point.isLessThan(max)

    override val size: V
        get() = max - min + min.vectorSpace.pointSize

    override fun translate(delta: V): Bounds<V> = NonEmptyBounds(min + delta, max + delta)
}

fun <V: Vec<V, *, *, *>> V.singletonBounds(): Bounds<V> = NonEmptyBounds(this, this)

val Bounds<Vec2i>.points: Sequence<Vec2i>
    get() {
        return if (isEmpty) {
            emptySequence()
        } else sequence {
            for (x in min.x .. max.x) {
                for (y in min.y .. max.y) {
                    yield(vec(x, y))
                }
            }
        }
    }

val Bounds<Vec2i>.interiorPoints: Sequence<Vec2i>
    get() {
        return if (isEmpty) {
            emptySequence()
        } else sequence {
            for (x in (min.x + 1) until max.x) {
                for (y in (min.y + 1) until max.y) {
                    yield(vec(x, y))
                }
            }
        }
    }

val Bounds<Vec2i>.boundaryPoints: Sequence<Vec2i>
    get() {
        return if (isEmpty) {
            emptySequence()
        } else sequence {
            for (x in min.x .. max.x) {
                if (x == min.x || x == max.x) {
                    for (y in min.y .. max.y) {
                        yield(vec(x, y))
                    }
                } else {
                    yield(vec(x, min.y))
                    if (min.y != max.y) {
                        yield(vec(x, max.y))
                    }
                }
            }
        }
    }

fun Bounds<Vec2i>.contains(x: Int, y: Int): Boolean = min.x <= x && x <= max.x && min.y <= y && y <= max.y

val Bounds<Vec2i>.rangeX: IntRange
    get() = min.x .. max.x

val Bounds<Vec2i>.rangeY: IntRange
    get() = min.y .. max.y

val <V : DoubleVector<*, *, V>> Bounds<V>.center: V
    get() = (min + max) * 0.5

val Bounds<Vec2d>.extremePoints: Sequence<Vec2d>
    get() {
        require(!isEmpty)
        val xEqual = min.x == max.x
        val yEqual = min.y == max.y
        return when {
            xEqual && yEqual -> sequenceOf(min)
            xEqual || yEqual -> sequenceOf(min, max)
            else -> {
                val maxMin = vec(max.x, min.y)
                val minMax = vec(min.x, max.y)
                sequenceOf(min, maxMin, max, minMax)
            }
        }
    }

val Bounds<Vec2d>.boundary: Sequence<LineSegment2d>
    get() {
        require(!isEmpty)
        val xEqual = min.x == max.x
        val yEqual = min.y == max.y
        return when {
            xEqual && yEqual -> sequenceOf(min.lineSegmentTo(min))
            xEqual || yEqual -> sequenceOf(min.lineSegmentTo(max), max.lineSegmentTo(min))
            else -> {
                val maxMin = vec(max.x, min.y)
                val minMax = vec(min.x, max.y)
                sequenceOf(
                        min.lineSegmentTo(maxMin),
                        maxMin.lineSegmentTo(max),
                        max.lineSegmentTo(minMax),
                        minMax.lineSegmentTo(min)
                )
            }
        }
    }

fun Bounds<Vec2d>.toGeometry2d(): Geometry2d = Geometry2d(Geometry2d.createGeometry(this))
fun <V : Vec<V, *, *, *>> DataOutput.writeBounds(bounds: Bounds<V>, writeVector: DataOutput.(V) -> Unit) {
    val size = bounds.size
    writeVector(size)
    if (!size.isZero) {
        writeVector(bounds.min)
    }
}

inline fun <V : Vec<V, *, *, *>> DataInput.readBounds(emptyBounds: Bounds<V>, readVector: DataInput.() -> V): Bounds<V> {
    val size = readVector()
    return if (size.isZero) {
        emptyBounds
    } else {
        val min = readVector()
        Bounds.byMinSize(min, size)
    }
}

fun DataOutput.writeBounds2i(bounds: Bounds<Vec2i>) = writeBounds(bounds) { writeVec2i(it) }

fun DataInput.readBounds2i(): Bounds<Vec2i> = readBounds(Bounds.empty2i()) { readVec2i() }

fun DataOutput.writeBounds3i(bounds: Bounds<Vec3i>) = writeBounds(bounds) { writeVec3i(it) }

fun DataInput.readBounds3i(): Bounds<Vec3i> = readBounds(Bounds.empty3i()) { readVec3i() }

fun DataOutput.writeBounds4i(bounds: Bounds<Vec4i>) = writeBounds(bounds) { writeVec4i(it) }

fun DataInput.readBounds4i(): Bounds<Vec4i> = readBounds(Bounds.empty4i()) { readVec4i() }