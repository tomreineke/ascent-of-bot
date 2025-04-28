package com.cerebrallychallenged.jun.math.geo

import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min

class Vec2i internal constructor(val x: Int, val y: Int) : IntVector<Vec2i, Vec2f, Vec2d>() {

    companion object {
        @JvmField
        val ZERO = vec(0, 0)

        @JvmField
        val ONE = vec(1, 1)

        @JvmField
        val UNIT_X = vec(1, 0)

        @JvmField
        val UNIT_Y = vec(0, 1)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec2i> = object : VectorSpace<Vec2i> {
            override val dimension: Int
                get() = 2

            override val zero: Vec2i
                get() = ZERO

            override val one: Vec2i
                get() = ONE

            override fun basis(index: Int): Vec2i {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec2i
                get() = ONE

            override val emptyBounds: Bounds<Vec2i> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 2

    override val vectorSpace: VectorSpace<Vec2i>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0 && y == 0

    override fun thisAsV(): Vec2i {
        return this
    }

    override fun hashCode(): Int = 31 * x + y

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec2i -> false
        else -> x == other.x && y == other.y
    }

    override fun toString(): String = "($x, $y)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T) -> V, map: (Int) -> T): V =
            reduce(map(x), map(y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2i, reduce: (T, T) -> V, zip: (Int, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2f, reduce: (T, T) -> V, zip: (Int, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2d, reduce: (T, T) -> V, zip: (Int, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    override operator fun unaryMinus(): Vec2i = mapReduce(::Vec2i) { -it }

    override operator fun plus(rhs: Vec2i): Vec2i = zipReduce(rhs, ::Vec2i) { a, b -> a + b }

    override operator fun plus(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f) { a, b -> a + b }

    override operator fun plus(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a + b }

    override operator fun minus(rhs: Vec2i): Vec2i = zipReduce(rhs, ::Vec2i) { a, b -> a - b }

    override operator fun minus(rhs: Vec2f): Vec2f = zipReduce(rhs, ::Vec2f) { a, b -> a - b }

    override operator fun minus(rhs: Vec2d): Vec2d = zipReduce(rhs, ::Vec2d) { a, b -> a - b }

    override operator fun times(factor: Int): Vec2i = mapReduce(::Vec2i) { it * factor }

    override operator fun times(factor: Float): Vec2f = mapReduce(::Vec2f) { it * factor }

    override operator fun times(factor: Double): Vec2d = mapReduce(::Vec2d) { it * factor }

    override operator fun div(divisor: Int): Vec2i = mapReduce(::Vec2i) { it / divisor }

    override operator fun div(divisor: Float): Vec2f = mapReduce(::Vec2f) { it / divisor }

    override operator fun div(divisor: Double): Vec2d = mapReduce(::Vec2d) { it / divisor }

    override fun min(rhs: Vec2i): Vec2i = zipReduce(rhs, ::Vec2i, ::min)

    override fun max(rhs: Vec2i): Vec2i = zipReduce(rhs, ::Vec2i, ::max)

    override fun pointwiseMul(rhs: Vec2i): Vec2i = zipReduce(rhs, ::Vec2i) { a, b -> a * b }

    override fun dot(rhs: Vec2i): Int = zipReduce(rhs, { x, y -> x + y }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec2i): Boolean = x < rhs.x && y < rhs.y

    override fun isLessEqualThan(rhs: Vec2i): Boolean = x <= rhs.x && y <= rhs.y

    override operator fun get(index: Int): Int {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            else -> throw IllegalArgumentException()
        }
    }

    fun append(z: Int): Vec3i = vec(x, y, z)

    fun append(z: Int, w: Int): Vec4i = vec(x, y, z, w)

    fun append(vecZW: Vec2i): Vec4i = vec(x, y, vecZW.x, vecZW.y)

    override fun toFloat(): Vec2f = mapReduce(::Vec2f) { it.toFloat() }

    override fun toDouble(): Vec2d = mapReduce(::Vec2d) { it.toDouble() }

    fun toRational(): Vec2r = mapReduce(::Vec2r) { Rational.valueOf(it.toLong()) }

    fun turnClockwise(): Vec2i = vec(-y, x)

    fun turnCounterClockwise(): Vec2i = vec(y, -x)

    val yx: Vec2i
        get() = vec(y, x)

    operator fun component1(): Int = x

    operator fun component2(): Int = y
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec2i): Vec2i = fold(Vec2i.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec2i>.sum(): Vec2i = sumBy(fun(it: Vec2i): Vec2i = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec2i): Vec2i = fold(Vec2i.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec2i>.sum(): Vec2i = sumBy(fun(it: Vec2i): Vec2i = it)

fun DataOutput.writeVec2i(value: Vec2i) {
    writeInt(value.x)
    writeInt(value.y)
}

fun DataInput.readVec2i(): Vec2i = vec(readInt(), readInt())