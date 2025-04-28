package com.cerebrallychallenged.jun.math.geo

import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min

class Vec4i internal constructor(val x: Int, val y: Int, val z: Int, val w: Int) : IntVector<Vec4i, Vec4f, Vec4d>() {

    companion object {
        @JvmField
        val ZERO = vec(0, 0, 0, 0)

        @JvmField
        val ONE = vec(1, 1, 1, 1)

        @JvmField
        val UNIT_X = vec(1, 0, 0, 0)

        @JvmField
        val UNIT_Y = vec(0, 1, 0, 0)

        @JvmField
        val UNIT_Z = vec(0, 0, 1, 0)

        @JvmField
        val UNIT_W = vec(0, 0, 0, 1)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec4i> = object : VectorSpace<Vec4i> {
            override val dimension: Int
                get() = 4

            override val zero: Vec4i
                get() = ZERO

            override val one: Vec4i
                get() = ONE

            override fun basis(index: Int): Vec4i {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    INDEX_Z -> UNIT_Z
                    INDEX_W -> UNIT_W
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec4i
                get() = ONE

            override val emptyBounds: Bounds<Vec4i> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 4

    override val vectorSpace: VectorSpace<Vec4i>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0 && y == 0 && z == 0 && w == 0

    override fun thisAsV(): Vec4i {
        return this
    }

    override fun hashCode(): Int = 31 * (31 * (31 * x + y) + z) + w

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec4i -> false
        else -> x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun toString(): String = "($x, $y, $z, $w)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T, T, T) -> V, map: (Int) -> T): V =
            reduce(map(x), map(y), map(z), map(w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4i, reduce: (T, T, T, T) -> V, zip: (Int, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4f, reduce: (T, T, T, T) -> V, zip: (Int, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4d, reduce: (T, T, T, T) -> V, zip: (Int, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    override operator fun unaryMinus(): Vec4i = mapReduce(::Vec4i) { -it }

    override operator fun plus(rhs: Vec4i): Vec4i = zipReduce(rhs, ::Vec4i) { a, b -> a + b }

    override operator fun plus(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f) { a, b -> a + b }

    override operator fun plus(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a + b }

    override operator fun minus(rhs: Vec4i): Vec4i = zipReduce(rhs, ::Vec4i) { a, b -> a - b }

    override operator fun minus(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f) { a, b -> a - b }

    override operator fun minus(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a - b }

    override operator fun times(factor: Int): Vec4i = mapReduce(::Vec4i) { it * factor }

    override operator fun times(factor: Float): Vec4f = mapReduce(::Vec4f) { it * factor }

    override operator fun times(factor: Double): Vec4d = mapReduce(::Vec4d) { it * factor }

    override operator fun div(divisor: Int): Vec4i = mapReduce(::Vec4i) { it / divisor }

    override operator fun div(divisor: Float): Vec4f = mapReduce(::Vec4f) { it / divisor }

    override operator fun div(divisor: Double): Vec4d = mapReduce(::Vec4d) { it / divisor }

    override fun min(rhs: Vec4i): Vec4i = zipReduce(rhs, ::Vec4i, ::min)

    override fun max(rhs: Vec4i): Vec4i = zipReduce(rhs, ::Vec4i, ::max)

    override fun pointwiseMul(rhs: Vec4i): Vec4i = zipReduce(rhs, ::Vec4i) { a, b -> a * b }

    override fun dot(rhs: Vec4i): Int = zipReduce(rhs, { x, y, z, w -> x + y + z + w }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec4i): Boolean = x < rhs.x && y < rhs.y && z < rhs.z && w < rhs.w

    override fun isLessEqualThan(rhs: Vec4i): Boolean = x <= rhs.x && y <= rhs.y && z <= rhs.z && w <= rhs.w

    override operator fun get(index: Int): Int {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            INDEX_Z -> z
            INDEX_W -> w
            else -> throw IllegalArgumentException()
        }
    }

    val xy: Vec2i
        get() = vec(x, y)

    val xyz: Vec3i
        get() = vec(x, y, z)

    override fun toFloat(): Vec4f = mapReduce(::Vec4f) { it.toFloat() }

    override fun toDouble(): Vec4d = mapReduce(::Vec4d) { it.toDouble() }

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    operator fun component3(): Int = z

    operator fun component4(): Int = w
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec4i): Vec4i = fold(Vec4i.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec4i>.sum(): Vec4i = sumBy(fun(it: Vec4i): Vec4i = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec4i): Vec4i = fold(Vec4i.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec4i>.sum(): Vec4i = sumBy(fun(it: Vec4i): Vec4i = it)

fun DataOutput.writeVec4i(value: Vec4i) {
    writeInt(value.x)
    writeInt(value.y)
    writeInt(value.z)
    writeInt(value.w)
}

fun DataInput.readVec4i(): Vec4i = vec(readInt(), readInt(), readInt(), readInt())