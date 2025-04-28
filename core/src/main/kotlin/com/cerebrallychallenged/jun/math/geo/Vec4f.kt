package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.roundTowards
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Vec4f internal constructor(val x: Float, val y: Float, val z: Float, val w: Float) : FloatVector<Vec4i, Vec4f, Vec4d>() {

    companion object {
        @JvmField
        val ZERO = vec(0.0f, 0.0f, 0.0f, 0.0f)

        @JvmField
        val ONE = vec(1.0f, 1.0f, 1.0f, 1.0f)

        @JvmField
        val ONE_HALF = vec(0.5f, 0.5f, 0.5f, 0.5f)

        @JvmField
        val UNIT_X = vec(1.0f, 0.0f, 0.0f, 0.0f)

        @JvmField
        val UNIT_Y = vec(0.0f, 1.0f, 0.0f, 0.0f)

        @JvmField
        val UNIT_Z = vec(0.0f, 0.0f, 1.0f, 0.0f)

        @JvmField
        val UNIT_W = vec(0.0f, 0.0f, 0.0f, 1.0f)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec4f> = object : VectorSpace<Vec4f> {
            override val dimension: Int
                get() = 4

            override val zero: Vec4f
                get() = ZERO

            override val one: Vec4f
                get() = ONE

            override fun basis(index: Int): Vec4f {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    INDEX_Z -> UNIT_Z
                    INDEX_W -> UNIT_W
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec4f
                get() = ZERO

            override val emptyBounds: Bounds<Vec4f> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 4

    override val vectorSpace: VectorSpace<Vec4f>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0.0f && y == 0.0f && z == 0.0f && w == 0.0f

    override fun thisAsV(): Vec4f {
        return this
    }

    override fun hashCode(): Int = 31 * (31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()) + w.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec4f -> false
        else -> x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun toString(): String = "($x, $y, $z, $w)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T, T, T) -> V, map: (Float) -> T): V =
            reduce(map(x), map(y), map(z), map(w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4i, reduce: (T, T, T, T) -> V, zip: (Float, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4f, reduce: (T, T, T, T) -> V, zip: (Float, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    inline fun <reified T, reified V> zipReduce(rhs: Vec4d, reduce: (T, T, T, T) -> V, zip: (Float, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z), zip(w, rhs.w))

    override operator fun unaryMinus(): Vec4f = mapReduce(::Vec4f) { -it }

    override operator fun plus(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f) { a, b -> a + b }

    override operator fun plus(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a + b }

    override operator fun minus(rhs: Vec4i): Vec4f = zipReduce(rhs, ::Vec4f) { a, b -> a - b }

    override operator fun minus(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f) { a, b -> a - b }

    override operator fun minus(rhs: Vec4d): Vec4d = zipReduce(rhs, ::Vec4d) { a, b -> a - b }

    override operator fun times(factor: Float): Vec4f = mapReduce(::Vec4f) { it * factor }

    override operator fun times(factor: Double): Vec4d = mapReduce(::Vec4d) { it * factor }

    override fun min(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f, ::min)

    override fun max(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f, ::max)

    override fun pointwiseMul(rhs: Vec4f): Vec4f = zipReduce(rhs, ::Vec4f) { a, b -> a * b }

    override fun dot(rhs: Vec4f): Float = zipReduce(rhs, { x, y, z, w -> x + y + z + w }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec4f): Boolean = x < rhs.x && y < rhs.y && z < rhs.z && w < rhs.w

    override fun isLessEqualThan(rhs: Vec4f): Boolean = x <= rhs.x && y <= rhs.y && z <= rhs.z && w <= rhs.w

    override operator fun get(index: Int): Float {
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

    override fun roundTowards(target: Vec4f): Vec4i = zipReduce(target, ::Vec4i) { a, b -> a.roundTowards(b) }

    override fun toDouble(): Vec4d = mapReduce(::Vec4d) { it.toDouble() }

    val xy: Vec2f
        get() = vec(x, y)

    val xyz: Vec3f
        get() = vec(x, y, z)

    /**
     * Alternative getter for [x] (red component).
     */
    val r: Float
        get() = x

    /**
     * Alternative getter for [y] (green component).
     */
    val g: Float
        get() = y

    /**
     * Alternative getter for [z] (blue component).
     */
    val b: Float
        get() = z

    /**
     * Alternative getter for [w] (alpha component).
     */
    val a: Float
        get() = w

    operator fun component1(): Float = x

    operator fun component2(): Float = y

    operator fun component3(): Float = z

    operator fun component4(): Float = w
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec4f): Vec4f = fold(Vec4f.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec4f>.sum(): Vec4f = sumBy(fun(it: Vec4f): Vec4f = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec4f): Vec4f = fold(Vec4f.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec4f>.sum(): Vec4f = sumBy(fun(it: Vec4f): Vec4f = it)

fun DataOutput.writeVec4f(value: Vec4f) {
    writeFloat(value.x)
    writeFloat(value.y)
    writeFloat(value.z)
    writeFloat(value.w)
}

fun DataInput.readVec4f(): Vec4f = vec(readFloat(), readFloat(), readFloat(), readFloat())
