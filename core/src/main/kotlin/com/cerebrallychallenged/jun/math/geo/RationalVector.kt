package com.cerebrallychallenged.jun.math.geo

import kotlin.math.sqrt

abstract class RationalVector<V : RationalVector<V, Vi, Vf, Vd>, Vi : IntVector<Vi, Vf, Vd>, Vf : FloatVector<Vi, Vf, Vd>, Vd : DoubleVector<Vi, Vf, Vd>> internal constructor() : Vec<V, Vi, Vf, Vd>() {
    val squaredLength: Rational
        get() = dot(thisAsV())

    val length: Double
        get() = sqrt(squaredLength.toDouble())

    abstract operator fun times(factor: Rational): V

    abstract operator fun get(index: Int): Rational

    abstract fun dot(rhs: V): Rational

    fun interpolate(alpha: Rational, other: V): V = this * (Rational.ONE - alpha) + other * alpha

    abstract fun floor(): Vi

    abstract fun ceil(): Vi

    abstract fun round(): Vi

    abstract fun roundTowards(target: Vi): Vi

    abstract fun toFloat(): Vf

    abstract fun toDouble(): Vd
}
