package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.radians
import com.cerebrallychallenged.jun.math.roundTowards
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Vec2f internal constructor(val x: Float, val y: Float) : FloatVector<Vec2i, Vec2f, Vec2d>() {

    companion object {
        @JvmField
        val ZERO = vec(0.0f, 0.0f)

        @JvmField
        val ONE = vec(1.0f, 1.0f)

        @JvmField
        val ONE_HALF = vec(0.5f, 0.5f)

        @JvmField
        val UNIT_X = vec(1.0f, 0.0f)

        @JvmField
        val UNIT_Y = vec(0.0f, 1.0f)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec2f> = object : VectorSpace<Vec2f> {
            override val dimension: Int
                get() = 2

            override val zero: Vec2f
                get() = ZERO

            override val one: Vec2f
                get() = ONE

            override fun basis(index: Int): Vec2f {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec2f
                get() = ZERO

            override val emptyBounds: Bounds<Vec2f> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 2

    override val vectorSpace: VectorSpace<Vec2f>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0.0f && y == 0.0f

    override fun thisAsV(): Vec2f {
        return this
    }

    override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec2f -> false
        else -> x == other.x && y == other.y
    }

    override fun toString(): String = "($x, $y)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T) -> V, map: (Float) -> T): V =
            reduce(map(x), map(y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2i, reduce: (T, T) -> V, zip: (Float, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2f, reduce: (T, T) -> V, zip: (Float, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2d, reduce: (T, T) -> V, zip: (Float, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    override operator fun unaryMinus(): Vec2f = mapReduce(::Vec2f) { -it }

    override operator fun plus(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f) { a, b -> a + b }

    override operator fun plus(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a + b }

    override operator fun minus(rhs: Vec2i): Vec2f = zipReduce(rhs, ::Vec2f) { a, b -> a - b }

    override operator fun minus(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f) { a, b -> a - b }

    override operator fun minus(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a - b }

    override operator fun times(factor: Float): Vec2f = mapReduce(::Vec2f) { it * factor }

    override operator fun times(factor: Double): Vec2d = mapReduce(::Vec2d) { it * factor }

    override fun min(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f, ::min)

    override fun max(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f, ::max)

    override fun pointwiseMul(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f) { a, b -> a * b }

    override fun dot(rhs: Vec2f): Float = zipReduce(rhs, { x, y -> x + y }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec2f): Boolean = x < rhs.x && y < rhs.y

    override fun isLessEqualThan(rhs: Vec2f): Boolean = x <= rhs.x && y <= rhs.y

    override operator fun get(index: Int): Float {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            else -> throw IllegalArgumentException()
        }
    }

    fun append(z: Float): Vec3f = vec(x, y, z)

    fun append(z: Float, w: Float): Vec4f = vec(x, y, z, w)

    fun append(vecZW: Vec2f): Vec4f = vec(x, y, vecZW.x, vecZW.y)

    override fun floor(): Vec2i = mapReduce(::Vec2i) { it.floorToInt() }

    override fun ceil(): Vec2i = mapReduce(::Vec2i) { it.ceilToInt() }

    override fun round(): Vec2i = mapReduce(::Vec2i) { it.roundToInt() }

    override fun roundTowards(target: Vec2f): Vec2i = zipReduce(target, ::Vec2i) { a, b -> a.roundTowards(b) }

    override fun toDouble(): Vec2d = mapReduce(::Vec2d) { it.toDouble() }

    fun angle(): Angle = atan2(y, x).radians

    fun turnClockwise(): Vec2f = vec(y, -x)

    fun turnCounterClockwise(): Vec2f = vec(-y, x)

    fun lineSegmentTo(point: Vec2f): LineSegment2f = LineSegment2f(this, point)

    operator fun component1(): Float = x

    operator fun component2(): Float = y

    val yx: Vec2f
        get() = vec(y, x)
}

inline fun <T> Iterable<T>.sumBy(selector: (T) -> Vec2f): Vec2f =
        fold(Vec2f.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec2f>.sum(): Vec2f = sumBy(fun(it: Vec2f): Vec2f = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec2f): Vec2f = fold(Vec2f.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec2f>.sum(): Vec2f = sumBy(fun(it: Vec2f): Vec2f = it)

fun DataOutput.writeVec2f(value: Vec2f) {
    writeFloat(value.x)
    writeFloat(value.y)
}

fun DataInput.readVec2f(): Vec2f = vec(readFloat(), readFloat())
