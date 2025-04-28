package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.roundTowards
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Vec3d internal constructor(val x: Double, val y: Double, val z: Double) : DoubleVector<Vec3i, Vec3f, Vec3d>() {

    companion object {
        @JvmField
        val ZERO = vec(0.0, 0.0, 0.0)

        @JvmField
        val ONE = vec(1.0, 1.0, 1.0)

        @JvmField
        val ONE_HALF = vec(0.5, 0.5, 0.5)

        @JvmField
        val UNIT_X = vec(1.0, 0.0, 0.0)

        @JvmField
        val UNIT_Y = vec(0.0, 1.0, 0.0)

        @JvmField
        val UNIT_Z = vec(0.0, 0.0, 1.0)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec3d> = object : VectorSpace<Vec3d> {
            override val dimension: Int
                get() = 3

            override val zero: Vec3d
                get() = ZERO

            override val one: Vec3d
                get() = ONE

            override fun basis(index: Int): Vec3d {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    INDEX_Z -> UNIT_Z
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec3d
                get() = ZERO

            override val emptyBounds: Bounds<Vec3d> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 3

    override val vectorSpace: VectorSpace<Vec3d>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0.0 && y == 0.0 && z == 0.0

    override fun thisAsV(): Vec3d {
        return this
    }

    override fun hashCode(): Int = 31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec3d -> false
        else -> x == other.x && y == other.y && z == other.z
    }

    override fun toString(): String = "($x, $y, $z)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T, T) -> V, map: (Double) -> T): V =
            reduce(map(x), map(y), map(z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3i, reduce: (T, T, T) -> V, zip: (Double, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3f, reduce: (T, T, T) -> V, zip: (Double, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3d, reduce: (T, T, T) -> V, zip: (Double, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    override operator fun unaryMinus(): Vec3d = mapReduce(::Vec3d) { -it }

    override operator fun plus(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a + b }

    override operator fun minus(rhs: Vec3i): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a - b }

    override operator fun minus(rhs: Vec3f): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a - b }

    override operator fun minus(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a - b }

    override operator fun times(factor: Double): Vec3d = mapReduce(::Vec3d) { it * factor }

    override fun min(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d, ::min)

    override fun max(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d, ::max)

    override fun pointwiseMul(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a * b }

    override fun dot(rhs: Vec3d): Double = zipReduce(rhs, { x, y, z -> x + y + z }) { a, b -> a * b }

    infix fun cross(rhs: Vec3d): Vec3d = vec(
            y * rhs.z - z * rhs.y,
            z * rhs.x - x * rhs.z,
            x * rhs.y - y * rhs.x
    )

    override fun isLessThan(rhs: Vec3d): Boolean = x < rhs.x && y < rhs.y && z < rhs.z

    override fun isLessEqualThan(rhs: Vec3d): Boolean = x <= rhs.x && y <= rhs.y && z <= rhs.z

    override operator fun get(index: Int): Double {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            INDEX_Z -> z
            else -> throw IllegalArgumentException()
        }
    }

    fun append(w: Double): Vec4d = vec(x, y, z, w)

    override fun floor(): Vec3i = mapReduce(::Vec3i) { it.floorToInt() }

    override fun ceil(): Vec3i = mapReduce(::Vec3i) { it.ceilToInt() }

    override fun round(): Vec3i = mapReduce(::Vec3i) { it.roundToInt() }

    override fun roundTowards(target: Vec3d): Vec3i = zipReduce(target, ::Vec3i) { a, b -> a.roundTowards(b) }

    override fun toFloat(): Vec3f = mapReduce(::Vec3f) { it.toFloat() }

    val xy: Vec2d
        get() = vec(x, y)

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

    operator fun component1(): Double = x

    operator fun component2(): Double = y

    operator fun component3(): Double = z
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec3d): Vec3d = fold(Vec3d.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec3d>.sum(): Vec3d = sumBy(fun(it: Vec3d): Vec3d = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec3d): Vec3d = fold(Vec3d.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec3d>.sum(): Vec3d = sumBy(fun(it: Vec3d): Vec3d = it)

fun DataOutput.writeVec3d(value: Vec3d) {
    writeDouble(value.x)
    writeDouble(value.y)
    writeDouble(value.z)
}

fun DataInput.readVec3d(): Vec3d = vec(readDouble(), readDouble(), readDouble())
