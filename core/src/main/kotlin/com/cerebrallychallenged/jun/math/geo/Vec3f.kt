package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.floorToInt
import com.cerebrallychallenged.jun.math.roundTowards
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Vec3f internal constructor(val x: Float, val y: Float, val z: Float) : FloatVector<Vec3i, Vec3f, Vec3d>() {

    companion object {
        @JvmField
        val ZERO = vec(0.0f, 0.0f, 0.0f)

        @JvmField
        val ONE = vec(1.0f, 1.0f, 1.0f)

        @JvmField
        val ONE_HALF = vec(0.5f, 0.5f, 0.5f)

        @JvmField
        val UNIT_X = vec(1.0f, 0.0f, 0.0f)

        @JvmField
        val UNIT_Y = vec(0.0f, 1.0f, 0.0f)

        @JvmField
        val UNIT_Z = vec(0.0f, 0.0f, 1.0f)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec3f> = object : VectorSpace<Vec3f> {
            override val dimension: Int
                get() = 3

            override val zero: Vec3f
                get() = ZERO

            override val one: Vec3f
                get() = ONE

            override fun basis(index: Int): Vec3f {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    INDEX_Z -> UNIT_Z
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec3f
                get() = ZERO

            override val emptyBounds: Bounds<Vec3f> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 3

    override val vectorSpace: VectorSpace<Vec3f>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x == 0.0f && y == 0.0f && z == 0.0f

    override fun thisAsV(): Vec3f {
        return this
    }

    override fun hashCode(): Int = 31 * (31 * x.hashCode() + y.hashCode()) + z.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec3f -> false
        else -> x == other.x && y == other.y && z == other.z
    }

    override fun toString(): String = "($x, $y, $z)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T, T) -> V, map: (Float) -> T): V =
            reduce(map(x), map(y), map(z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3i, reduce: (T, T, T) -> V, zip: (Float, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3f, reduce: (T, T, T) -> V, zip: (Float, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    inline fun <reified T, reified V> zipReduce(rhs: Vec3d, reduce: (T, T, T) -> V, zip: (Float, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y), zip(z, rhs.z))

    override operator fun unaryMinus(): Vec3f = mapReduce(::Vec3f) { -it }

    override operator fun plus(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f) { a, b -> a + b }

    override operator fun plus(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a + b }

    override operator fun minus(rhs: Vec3i): Vec3f = zipReduce(rhs, ::Vec3f) { a, b -> a - b }

    override operator fun minus(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f) { a, b -> a - b }

    override operator fun minus(rhs: Vec3d): Vec3d = zipReduce(rhs, ::Vec3d) { a, b -> a - b }

    override operator fun times(factor: Float): Vec3f = mapReduce(::Vec3f) { it * factor }

    override operator fun times(factor: Double): Vec3d = mapReduce(::Vec3d) { it * factor }

    override fun min(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f, ::min)

    override fun max(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f, ::max)

    override fun pointwiseMul(rhs: Vec3f): Vec3f = zipReduce(rhs, ::Vec3f) { a, b -> a * b }

    override fun dot(rhs: Vec3f): Float = zipReduce(rhs, { x, y, z -> x + y + z }) { a, b -> a * b }

    infix fun cross(rhs: Vec3f): Vec3f = vec(
            y * rhs.z - z * rhs.y,
            z * rhs.x - x * rhs.z,
            x * rhs.y - y * rhs.x
    )

    override fun isLessThan(rhs: Vec3f): Boolean = x < rhs.x && y < rhs.y && z < rhs.z

    override fun isLessEqualThan(rhs: Vec3f): Boolean = x <= rhs.x && y <= rhs.y && z <= rhs.z

    override operator fun get(index: Int): Float {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            INDEX_Z -> z
            else -> throw IllegalArgumentException()
        }
    }

    fun append(w: Float): Vec4f = vec(x, y, z, w)

    override fun floor(): Vec3i = mapReduce(::Vec3i) { it.floorToInt() }

    override fun ceil(): Vec3i = mapReduce(::Vec3i) { it.ceilToInt() }

    override fun round(): Vec3i = mapReduce(::Vec3i) { it.roundToInt() }

    override fun roundTowards(target: Vec3f): Vec3i = zipReduce(target, ::Vec3i) { a, b -> a.roundTowards(b) }

    override fun toDouble(): Vec3d = mapReduce(::Vec3d) { it.toDouble() }

    val xy: Vec2f
        get() = vec(x, y)

    /**
     * Returns the quaternion rotating the x-vector to this vector.
     * The result is often called "look-at quaternion".
     * @param upVector vector for what is considered "up".
     */
    fun toLookAtWith(upVector: Vec3f): Quaternion {
        val x = normalized()
        val y = (upVector cross this).normalized()
        val z = (x cross y).normalized()
        return Quaternion.fromOrthogonalAxes(x, y, z)
    }

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

    operator fun component1(): Float = x

    operator fun component2(): Float = y

    operator fun component3(): Float = z
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec3f): Vec3f = fold(Vec3f.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec3f>.sum(): Vec3f = sumBy(fun(it: Vec3f): Vec3f = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec3f): Vec3f = fold(Vec3f.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec3f>.sum(): Vec3f = sumBy(fun(it: Vec3f): Vec3f = it)

fun DataOutput.writeVec3f(value: Vec3f) {
    writeFloat(value.x)
    writeFloat(value.y)
    writeFloat(value.z)
}

fun DataInput.readVec3f(): Vec3f = vec(readFloat(), readFloat(), readFloat())
