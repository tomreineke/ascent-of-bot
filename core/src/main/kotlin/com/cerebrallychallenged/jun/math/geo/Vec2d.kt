package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.*
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Vec2d(val x: Double, val y: Double) : DoubleVector<Vec2i, Vec2f, Vec2d>() {

    companion object {
        @JvmField
        val ZERO = vec(0.0, 0.0)

        @JvmField
        val ONE = vec(1.0, 1.0)

        @JvmField
        val ONE_HALF = vec(0.5, 0.5)

        @JvmField
        val UNIT_X = vec(1.0, 0.0)

        @JvmField
        val UNIT_Y = vec(0.0, 1.0)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec2d> = object : VectorSpace<Vec2d> {
            override val dimension: Int
                get() = 2

            override val zero: Vec2d
                get() = ZERO

            override val one: Vec2d
                get() = ONE

            override fun basis(index: Int): Vec2d {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec2d
                get() = ZERO

            override val emptyBounds: Bounds<Vec2d> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 2

    override val vectorSpace: VectorSpace<Vec2d>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0.0 && y == 0.0

    override fun thisAsV(): Vec2d {
        return this
    }

    override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec2d -> false
        else -> x == other.x && y == other.y
    }

    override fun toString(): String = "($x, $y)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T) -> V, map: (Double) -> T): V =
            reduce(map(x), map(y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2i, reduce: (T, T) -> V, zip: (Double, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2f, reduce: (T, T) -> V, zip: (Double, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2d, reduce: (T, T) -> V, zip: (Double, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    override operator fun unaryMinus(): Vec2d = mapReduce(::Vec2d) { -it }

    override operator fun plus(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a + b }

    override operator fun minus(rhs: Vec2i): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a - b }

    override operator fun minus(rhs: Vec2f): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a - b }

    override operator fun minus(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a - b }

    override operator fun times(factor: Double): Vec2d = mapReduce(::Vec2d) { it * factor }

    override fun min(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d, ::min)

    override fun max(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d, ::max)

    override fun pointwiseMul(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a * b }

    override fun dot(rhs: Vec2d): Double = zipReduce(rhs, { x, y -> x + y }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec2d): Boolean = x < rhs.x && y < rhs.y

    override fun isLessEqualThan(rhs: Vec2d): Boolean = x <= rhs.x && y <= rhs.y

    override operator fun get(index: Int): Double {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            else -> throw IllegalArgumentException()
        }
    }

    fun append(z: Double): Vec3d = vec(x, y, z)

    fun append(z: Double, w: Double): Vec4d = vec(x, y, z, w)

    fun append(vecZW: Vec2d): Vec4d = vec(x, y, vecZW.x, vecZW.y)

    override fun floor(): Vec2i = mapReduce(::Vec2i) { it.floorToInt() }

    override fun ceil(): Vec2i = mapReduce(::Vec2i) { it.ceilToInt() }

    override fun round(): Vec2i = mapReduce(::Vec2i) { it.roundToInt() }

    override fun roundTowards(target: Vec2d): Vec2i = zipReduce(target, ::Vec2i) { a, b -> a.roundTowards(b) }

    override fun toFloat(): Vec2f = mapReduce(::Vec2f) { it.toFloat() }

    fun angle(): Angle = atan2(y, x).toFloat().radians

    fun turnClockwise(): Vec2d = vec(y, -x)

    fun turnCounterClockwise(): Vec2d = vec(-y, x)

    fun lineSegmentTo(point: Vec2d): LineSegment2d = LineSegment2d(this, point)

    fun toGeometry(): Geometry2d = Geometry2d(Geometry2d.createGeometry(this))

    operator fun component1(): Double = x

    operator fun component2(): Double = y
}

inline fun <T> Iterable<T>.sumBy(selector: (T) -> Vec2d): Vec2d =
        fold(Vec2d.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec2d>.sum(): Vec2d = sumBy(fun(it: Vec2d): Vec2d = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec2d): Vec2d = fold(Vec2d.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec2d>.sum(): Vec2d = sumBy(fun(it: Vec2d): Vec2d = it)

fun DataOutput.writeVec2d(value: Vec2d) {
    writeDouble(value.x)
    writeDouble(value.y)
}

fun DataInput.readVec2d(): Vec2d = vec(readDouble(), readDouble())
