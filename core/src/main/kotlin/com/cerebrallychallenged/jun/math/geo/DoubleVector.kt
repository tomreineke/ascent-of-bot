package com.cerebrallychallenged.jun.math.geo

import kotlin.math.sqrt

abstract class DoubleVector<Vi : IntVector<Vi, Vf, V>, Vf : FloatVector<Vi, Vf, V>, V : DoubleVector<Vi, Vf, V>> internal constructor() : Vec<V, Vi, Vf, V>() {
    val squaredLength: Double
        get() = dot(thisAsV())

    val length: Double
        get() = sqrt(squaredLength)

    fun distanceTo(other: V): Double = (this - other).length

    abstract operator fun minus(rhs: Vi): V

    abstract operator fun minus(rhs: Vf): V

    abstract operator fun times(factor: Double): V

    operator fun times(factor: Int): V = this * factor.toDouble()

    operator fun times(factor: Float): V = this * factor.toDouble()

    operator fun div(divisor: Double): V = this * (1.0 / divisor)

    abstract infix fun dot(rhs: V): Double

    fun normalized(): V = this / length

    abstract operator fun get(index: Int): Double

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int): Vec2d = vec(this[indexX], this[indexY])

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int, indexZ: Int): Vec3d = vec(this[indexX], this[indexY], this[indexZ])

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int, indexZ: Int, indexW: Int): Vec4d
            = vec(this[indexX], this[indexY], this[indexZ], this[indexW])

    fun interpolate(alpha: Double, other: V): V = this * (1.0 - alpha) + other * alpha

    abstract fun floor(): Vi

    abstract fun ceil(): Vi

    abstract fun round(): Vi

    abstract fun roundTowards(target: V): Vi

    abstract fun toFloat(): Vf
}

operator fun <Vi : IntVector<Vi, *, Vd>, Vd : DoubleVector<Vi, *, Vd>> Vd.plus(rhs: Vi): Vd = rhs + this

operator fun <Vf : FloatVector<*, Vf, Vd>, Vd : DoubleVector<*, Vf, Vd>> Vd.plus(rhs: Vf): Vd = rhs + this

operator fun <Vd : DoubleVector<*, *, Vd>> Int.times(rhs: Vd): Vd = rhs * this

operator fun <Vd : DoubleVector<*, *, Vd>> Float.times(rhs: Vd): Vd = rhs * this

operator fun <Vd : DoubleVector<*, *, Vd>> Double.times(rhs: Vd): Vd = rhs * this
