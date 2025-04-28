package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.cos
import com.cerebrallychallenged.jun.math.sin

const val INDEX_X: Int = 0

const val INDEX_Y: Int = 1

const val INDEX_Z: Int = 2

const val INDEX_W: Int = 3

abstract class Vec<V : Vec<V, Vi, Vf, Vd>, Vi : IntVector<Vi, Vf, Vd>, Vf : FloatVector<Vi, Vf, Vd>, Vd : DoubleVector<Vi, Vf, Vd>> internal constructor() {
    protected abstract fun thisAsV(): V

    abstract val dimension: Int

    abstract val vectorSpace: VectorSpace<V>

    abstract operator fun unaryMinus(): V

    abstract operator fun plus(rhs: V): V

    abstract operator fun minus(rhs: V): V

    abstract fun min(rhs: V): V

    abstract fun max(rhs: V): V

    abstract val isZero: Boolean

    abstract fun pointwiseMul(rhs: V): V

    /**
     * Returns if every component of this vector is less than the respective component of the other vector.
     * @param rhs the other vector.
     * @return if every component of this vector is less than the respective component of the other vector.
     */
    abstract fun isLessThan(rhs: V): Boolean

    /**
     * Returns if every component of this vector is less than or equal the respective component of the other vector.
     * @param rhs the other vector.
     * @return if every component of this vector is less than or equal the respective component of the other vector.
     */
    abstract fun isLessEqualThan(rhs: V): Boolean
}

fun Pair<Int, Int>.toVec2i(): Vec2i = vec(first, second)

fun Triple<Int, Int, Int>.toVec3i(): Vec3i = vec(first, second, third)

fun Pair<Float, Float>.toVec2f(): Vec2f = vec(first, second)

fun Triple<Float, Float, Float>.toVec3f(): Vec3f = vec(first, second, third)

//fun Pair<Double, Double>.toVec2d(): Vec2d = vec(first, second)
//
//fun Triple<Double, Double, Double>.toVec3d(): Vec3d = vec(first, second, third)

fun vec(x: Int, y: Int): Vec2i = Vec2i(x, y)

fun vec(x: Int, y: Int, z: Int): Vec3i = Vec3i(x, y, z)

fun vec(x: Int, y: Int, z: Int, w: Int): Vec4i = Vec4i(x, y, z, w)

fun vec(x: Float, y: Float): Vec2f = Vec2f(x, y)

fun vec(x: Float, y: Float, z: Float): Vec3f = Vec3f(x, y, z)

fun vec(x: Float, y: Float, z: Float, w: Float): Vec4f = Vec4f(x, y, z, w)

fun vec(x: Double, y: Double): Vec2d = Vec2d(x, y)

fun vec(x: Double, y: Double, z: Double): Vec3d = Vec3d(x, y, z)

fun vec(x: Double, y: Double, z: Double, w: Double): Vec4d = Vec4d(x, y, z, w)

fun polar(radius: Double, angle: Angle): Vec2d = vec(radius * cos(angle), radius * sin(angle))

fun polar(radius: Float, angle: Angle): Vec2f = vec(radius * cos(angle), radius * sin(angle))

fun vec(x: Rational, y: Rational): Vec2r = Vec2r(x, y)