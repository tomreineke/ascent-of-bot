package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.roundTowards
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Vec4d internal constructor(val x: Double, val y: Double, val z: Double, val w: Double) : DoubleVector<Vec4i, Vec4f, Vec4d>() {

    companion object {
        @JvmField
        val ZERO = vec(0.0, 0.0, 0.0, 0.0)

        @JvmField
        val ONE = vec(1.0, 1.0, 1.0, 1.0)

        @JvmField
        val ONE_HALF = vec(0.5, 0.5, 0.5, 0.5)

        @JvmField
        val UNIT_X = vec(1.0, 0.0, 0.0, 0.0)

        @JvmField
        val UNIT_Y = vec(0.0, 1.0, 0.0, 0.0)

        @JvmField
        val UNIT_Z = vec(0.0, 0.0, 1.0, 0.0)

        @JvmField
        val UNIT_W = vec(0.0, 0.0, 0.0, 1.0)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec4d> = object : VectorSpace<Vec4d> {
            override val dimension: Int
                get() = 4

            override val zero: Vec4d
                get() = ZERO

            override val one: Vec4d
                get() = ONE

            override fun basis(index: Int): Vec4d {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    INDEX_Z -> UNIT_Z
                    INDEX_W -> UNIT_W
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec4d
                get() = ZERO

            override val emptyBounds: Bounds<Vec4d> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 4

    override val vectorSpace: VectorSpace<Vec4d>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0.0 && y == 0.0 && z == 0.0 && w == 0.0

    override fun thisAsV(): Vec4d {
        return this
    }

    override fun hashCode(): Int = 31 * (31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()) + w.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec4d -> false
        else -> x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun toString(): String = "($x, $y, $z, $w)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T, T, T) -> V, map: (Double) -> T): V =
            reduce(map(x), map(y), map(z), map(w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4i, reduce: (T, T, T, T) -> V, zip: (Double, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4f, reduce: (T, T, T, T) -> V, zip: (Double, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4d, reduce: (T, T, T, T) -> V, zip: (Double, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    override operator fun unaryMinus(): Vec4d = mapReduce(::Vec4d) { -it }

    override operator fun plus(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a + b }

    override operator fun minus(rhs: Vec4i): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a - b }

    override operator fun minus(rhs: Vec4f): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a - b }

    override operator fun minus(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a - b }

    override operator fun times(factor: Double): Vec4d = mapReduce(::Vec4d) { it * factor }

    override fun min(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d, ::min)

    override fun max(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d, ::max)

    override fun pointwiseMul(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a * b }

    override fun dot(rhs: Vec4d): Double = zipReduce(rhs, { x, y, z, w -> x + y + z + w }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec4d): Boolean = x < rhs.x && y < rhs.y && z < rhs.z && w < rhs.w

    override fun isLessEqualThan(rhs: Vec4d): Boolean = x <= rhs.x && y <= rhs.y && z <= rhs.z && w <= rhs.w

    override operator fun get(index: Int): Double {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            INDEX_Z -> z
            INDEX_W -> w
            else -> throw IllegalArgumentException()
        }
    }

    override fun floor(): Vec4i = mapReduce(::Vec4i) { it.floorToInt() }

    override fun ceil(): Vec4i = mapReduce(::Vec4i) { it.ceilToInt() }

    override fun round(): Vec4i = mapReduce(::Vec4i) { it.roundToInt() }

    override fun roundTowards(target: Vec4d): Vec4i = zipReduce(target, ::Vec4i) { a, b -> a.roundTowards(b) }

    override fun toFloat(): Vec4f = mapReduce(::Vec4f) { it.toFloat() }

    val xy: Vec2d
        get() = vec(x, y)

    val xyz: Vec3d
        get() = vec(x, y, z)

    /**
     * Alternative getter for [x] (red component).
     */
    val r: Double
        get() = x

    /**
     * Alternative getter for [y] (green component).
     */
    val g: Double
        get() = y

    /**
     * Alternative getter for [z] (blue component).
     */
    val b: Double
        get() = z

    /**
     * Alternative getter for [w] (alpha component).
     */
    val a: Double
        get() = w

    operator fun component1(): Double = x

    operator fun component2(): Double = y

    operator fun component3(): Double = z

    operator fun component4(): Double = w
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec4d): Vec4d = fold(Vec4d.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec4d>.sum(): Vec4d = sumBy(fun(it: Vec4d): Vec4d = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec4d): Vec4d = fold(Vec4d.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec4d>.sum(): Vec4d = sumBy(fun(it: Vec4d): Vec4d = it)

fun DataOutput.writeVec4d(value: Vec4d) {
    writeDouble(value.x)
    writeDouble(value.y)
    writeDouble(value.z)
    writeDouble(value.w)
}

fun DataInput.readVec4d(): Vec4d = vec(readDouble(), readDouble(), readDouble(), readDouble())
