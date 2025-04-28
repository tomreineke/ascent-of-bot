package com.cerebrallychallenged.jun.math.geo

import kotlin.math.sqrt

abstract class IntVector<V : IntVector<V, Vf, Vd>, Vf : FloatVector<V, Vf, Vd>, Vd : DoubleVector<V, Vf, Vd>> internal constructor() : Vec<V, V, Vf, Vd>() {
    val squaredLength: Int
        get() = dot(thisAsV())

    val length: Double
        get() = sqrt(squaredLength.toDouble())

    fun squaredDistanceTo(other: V): Int = (this - other).squaredLength

    fun distanceTo(other: V): Float = sqrt(squaredDistanceTo(other).toFloat())

    abstract operator fun plus(rhs: Vf): Vf

    abstract operator fun plus(rhs: Vd): Vd

    abstract operator fun minus(rhs: Vf): Vf

    abstract operator fun minus(rhs: Vd): Vd

    abstract operator fun times(factor: Int): V

    abstract operator fun times(factor: Float): Vf

    abstract operator fun times(factor: Double): Vd

    abstract operator fun div(divisor: Int): V

    abstract operator fun div(divisor: Float): Vf

    abstract operator fun div(divisor: Double): Vd

    abstract infix fun dot(rhs: V): Int

    abstract operator fun get(index: Int): Int

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int): Vec2i {
        return vec(this[indexX], this[indexY])
    }

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int, indexZ: Int): Vec3i {
        return vec(this[indexX], this[indexY], this[indexZ])
    }

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int, indexZ: Int, indexW: Int): Vec4i {
        return vec(this[indexX], this[indexY], this[indexZ], this[indexW])
    }

    abstract fun toFloat(): Vf

    abstract fun toDouble(): Vd
}

operator fun <Vi : IntVector<Vi, *, *>> Int.times(rhs: Vi): Vi = rhs * this

operator fun <Vi : IntVector<Vi, Vf, *>, Vf : FloatVector<Vi, Vf, *>> Float.times(rhs: Vi): Vf = rhs * this

operator fun <Vi : IntVector<Vi, *, Vd>, Vd : DoubleVector<Vi, *, Vd>> Double.times(rhs: Vi): Vd = rhs * this
