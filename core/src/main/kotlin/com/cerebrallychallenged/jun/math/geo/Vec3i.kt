package com.cerebrallychallenged.jun.math.geo

import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min

class Vec3i internal constructor(val x: Int, val y: Int, val z: Int) : IntVector<Vec3i, Vec3f, Vec3d>() {

    companion object {
        @JvmField
        val ZERO = vec(0, 0, 0)

        @JvmField
        val ONE = vec(1, 1, 1)

        @JvmField
        val UNIT_X = vec(1, 0, 0)

        @JvmField
        val UNIT_Y = vec(0, 1, 0)

        @JvmField
        val UNIT_Z = vec(0, 0, 1)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec3i> = object : VectorSpace<Vec3i> {
            override val dimension: Int
                get() = 3

            override val zero: Vec3i
                get() = ZERO

            override val one: Vec3i
                get() = ONE

            override fun basis(index: Int): Vec3i {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    INDEX_Z -> UNIT_Z
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec3i
                get() = ONE

            override val emptyBounds: Bounds<Vec3i> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 3

    override val vectorSpace: VectorSpace<Vec3i>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0 && y == 0 && z == 0

    override fun thisAsV(): Vec3i {
        return this
    }

    override fun hashCode(): Int = 31 * (31 * x + y) + z

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec3i -> false
        else -> x == other.x && y == other.y && z == other.z
    }

    override fun toString(): String = "($x, $y, $z)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T, T) -> V, map: (Int) -> T): V =
            reduce(map(x), map(y), map(z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3i, reduce: (T, T, T) -> V, zip: (Int, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3f, reduce: (T, T, T) -> V, zip: (Int, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3d, reduce: (T, T, T) -> V, zip: (Int, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    override operator fun unaryMinus(): Vec3i = mapReduce(::Vec3i) { -it }

    override operator fun plus(rhs: Vec3i): Vec3i = zipReduce(rhs, ::Vec3i) { a, b -> a + b }

    override operator fun plus(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f) { a, b -> a + b }

    override operator fun plus(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a + b }

    override operator fun minus(rhs: Vec3i): Vec3i = zipReduce(rhs, ::Vec3i) { a, b -> a - b }

    override operator fun minus(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f) { a, b -> a - b }

    override operator fun minus(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a - b }

    override operator fun times(factor: Int): Vec3i = mapReduce(::Vec3i) { it * factor }

    override operator fun times(factor: Float): Vec3f = mapReduce(::Vec3f) { it * factor }

    override operator fun times(factor: Double): Vec3d = mapReduce(::Vec3d) { it * factor }

    override operator fun div(divisor: Int): Vec3i = mapReduce(::Vec3i) { it / divisor }

    override operator fun div(divisor: Float): Vec3f = mapReduce(::Vec3f) { it / divisor }

    override operator fun div(divisor: Double): Vec3d = mapReduce(::Vec3d) { it / divisor }

    override fun min(rhs: Vec3i): Vec3i = zipReduce(rhs, ::Vec3i, ::min)

    override fun max(rhs: Vec3i): Vec3i = zipReduce(rhs, ::Vec3i, ::max)

    override fun pointwiseMul(rhs: Vec3i): Vec3i = zipReduce(rhs, ::Vec3i) { a, b -> a * b }

    override fun dot(rhs: Vec3i): Int = zipReduce(rhs, { x, y, z -> x + y + z }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec3i): Boolean = x < rhs.x && y < rhs.y && z < rhs.z

    override fun isLessEqualThan(rhs: Vec3i): Boolean = x <= rhs.x && y <= rhs.y && z <= rhs.z

    override operator fun get(index: Int): Int {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            INDEX_Z -> z
            else -> throw IllegalArgumentException()
        }
    }

    fun append(w: Int): Vec4i = vec(x, y, z, w)

    val xy: Vec2i
        get() = vec(x, y)

    override fun toFloat(): Vec3f = mapReduce(::Vec3f) { it.toFloat() }

    override fun toDouble(): Vec3d = mapReduce(::Vec3d) { it.toDouble() }

    operator fun component1(): Int = x

    operator fun component2(): Int = y

    operator fun component3(): Int = z
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec3i): Vec3i = fold(Vec3i.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec3i>.sum(): Vec3i = sumBy(fun(it: Vec3i): Vec3i = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec3i): Vec3i = fold(Vec3i.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec3i>.sum(): Vec3i = sumBy(fun(it: Vec3i): Vec3i = it)

fun DataOutput.writeVec3i(value: Vec3i) {
    writeInt(value.x)
    writeInt(value.y)
    writeInt(value.z)
}

fun DataInput.readVec3i(): Vec3i = vec(readInt(), readInt(), readInt())