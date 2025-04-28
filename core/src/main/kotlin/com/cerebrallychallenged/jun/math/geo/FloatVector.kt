package com.cerebrallychallenged.jun.math.geo

import kotlin.math.sqrt

abstract class FloatVector<Vi : IntVector<Vi, V, Vd>, V : FloatVector<Vi, V, Vd>, Vd : DoubleVector<Vi, V, Vd>> internal constructor() : Vec<V, Vi, V, Vd>() {
    val squaredLength: Float
        get() = dot(thisAsV())

    val length: Float
        get() = sqrt(squaredLength)

    fun distanceTo(other: V): Float = (this - other).length

    fun squaredDistanceTo(other: V): Float = (this - other).squaredLength

    abstract operator fun plus(rhs: Vd): Vd

    abstract operator fun minus(rhs: Vi): V

    abstract operator fun minus(rhs: Vd): Vd

    operator fun times(factor: Int): V = this * factor.toFloat()

    abstract operator fun times(factor: Float): V

    abstract operator fun times(factor: Double): Vd

    operator fun div(divisor: Int): V = this / divisor.toFloat()

    operator fun div(divisor: Float): V = this * (1.0f / divisor)

    operator fun div(divisor: Double): Vd = this * (1.0 / divisor)

    abstract infix fun dot(rhs: V): Float

    fun normalized(): V = this / length

    abstract operator fun get(index: Int): Float

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int): Vec2f = vec(this[indexX], this[indexY])

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int, indexZ: Int): Vec3f = vec(this[indexX], this[indexY], this[indexZ])

    // Swizzle operator
    operator fun get(indexX: Int, indexY: Int, indexZ: Int, indexW: Int): Vec4f
            = vec(this[indexX], this[indexY], this[indexZ], this[indexW])

    fun interpolate(alpha: Float, other: V): V = this * (1.0f - alpha) + other * alpha

    abstract fun floor(): Vi

    abstract fun ceil(): Vi

    abstract fun round(): Vi

    abstract fun roundTowards(target: V): Vi

    abstract fun toDouble(): Vd
}

operator fun <Vi : IntVector<Vi, Vf, *>, Vf : FloatVector<Vi, Vf, *>> Vf.plus(rhs: Vi): Vf = rhs + this

operator fun <Vf : FloatVector<*, Vf, *>> Int.times(rhs: Vf): Vf = rhs * this

operator fun <Vf : FloatVector<*, Vf, *>> Float.times(rhs: Vf): Vf = rhs * this

operator fun <Vf : FloatVector<*, Vf, Vd>, Vd : DoubleVector<*, Vf, Vd>> Double.times(rhs: Vf): Vd = rhs * this
