package com.cerebrallychallenged.jun.math.geo

import org.apache.commons.math3.util.ArithmeticUtils
import java.lang.Math.*

sealed class Rational : Comparable<Rational> {
    companion object {
        @JvmField
        val ZERO: Rational = NormalRational(0, 1)

        @JvmField
        val ONE: Rational = NormalRational(1, 1)

        @JvmField
        val POSITIVE_INFINITY: Rational = object : LimitRational() {
            override fun toString(): String = "∞"

            override val isNonNegative: Boolean
                get() = true

            override val isPositive: Boolean
                get() = true

            override val isNegative: Boolean
                get() = false

            override fun unaryMinus(): Rational = NEGATIVE_INFINITY

            override fun plus(other: Rational): Rational {
                if (other === NEGATIVE_INFINITY) {
                    throw UnsupportedOperationException()
                } else {
                    return this
                }
            }

            override fun compareTo(other: Rational): Int = if (other === this) 0 else 1

            override fun toFloat(): Float = Float.POSITIVE_INFINITY

            override fun toDouble(): Double = Double.POSITIVE_INFINITY
        }

        @JvmField
        val NEGATIVE_INFINITY: Rational = object : LimitRational() {
            override fun toString(): String = "-"

            override val isNonNegative: Boolean
                get() = false

            override val isPositive: Boolean
                get() = false

            override val isNegative: Boolean
                get() = true

            override fun unaryMinus(): Rational = POSITIVE_INFINITY

            override fun plus(other: Rational): Rational {
                if (other == POSITIVE_INFINITY) {
                    throw UnsupportedOperationException()
                } else {
                    return this
                }
            }

            override fun compareTo(other: Rational): Int = if (other === this) 0 else -1

            override fun toFloat(): Float = Float.NEGATIVE_INFINITY

            override fun toDouble(): Double = Double.NEGATIVE_INFINITY
        }

        @JvmField
        val ONE_HALF: Rational = NormalRational(1, 2)

        fun valueOf(value: Long): Rational = when (value) {
            0L -> ZERO
            1L -> ONE
            else -> NormalRational(value, 1)
        }

        fun ratio(numer: Long, denom: Long): Rational {
            return when {
                numer == 0L -> ZERO
                denom == 1L -> valueOf(numer)
                else -> {
                    fun internalRatio(numer: Long, denom: Long): Rational {
                        val gcd = ArithmeticUtils.gcd(numer, denom)
                        return if (gcd == 1L) {
                            NormalRational(numer, denom)
                        } else {
                            NormalRational(numer / gcd, denom / gcd)
                        }
                    }
                    if (denom < 0) {
                        internalRatio(-numer, -denom)
                    } else {
                        internalRatio(numer, denom)
                    }
                }
            }
        }

        fun valueOf(value: Double): Rational = when (value) {
            0.0 -> ZERO
            1.0 -> ONE
            Double.POSITIVE_INFINITY -> POSITIVE_INFINITY
            Double.NEGATIVE_INFINITY -> NEGATIVE_INFINITY
            else -> {
                if (value.isNaN()) {
                    throw ArithmeticException("No rational value for NaN")
                }
                var exponent = getExponent(value)
                val normalizedMantissa = value.toRawBits() and 0x000FFFFFFFFFFFFFL

                val r: Rational = if (normalizedMantissa == 0L) {
                    if (exponent >= 0) {
                        valueOf(1L shl exponent)
                    } else {
                        NormalRational(1, (1 shl -exponent).toLong())
                    }
                } else {
                    val trailing = java.lang.Long.numberOfTrailingZeros(normalizedMantissa)
                    val width = 52 - trailing
                    val mantissa = normalizedMantissa shr trailing or (1L shl width)
                    exponent -= width
                    if (exponent >= 0) {
                        valueOf(mantissa shl exponent)
                    } else {
                        NormalRational(mantissa, 1L shl -exponent)
                    }
                }
                if (value < 0.0) -r else r
            }
        }
    }

    abstract val numer: Long

    abstract val denom: Long

    abstract val isFinite: Boolean

    abstract val isInfinite: Boolean

    abstract val isNonNegative: Boolean

    abstract val isPositive: Boolean

    abstract val isNegative: Boolean

    abstract val isZero: Boolean

    abstract operator fun unaryMinus(): Rational

    abstract operator fun plus(other: Rational): Rational

    abstract operator fun plus(other: Long): Rational

    abstract operator fun minus(other: Rational): Rational

    abstract operator fun minus(other: Long): Rational

    abstract operator fun times(other: Rational): Rational

    abstract operator fun times(other: Long): Rational

    abstract operator fun div(other: Rational): Rational

    abstract operator fun div(other: Long): Rational

    abstract val isInteger: Boolean

    abstract fun toInteger(): Int

    abstract fun asInteger(): Int?

    abstract fun toLong(): Long

    abstract fun asLong(): Long?

    abstract fun toFloat(): Float

    abstract fun toDouble(): Double

    abstract fun floor(): Int

    abstract fun ceil(): Int

    abstract fun round(): Int

    abstract fun roundTowards(target: Int): Int

    fun min(other: Rational): Rational = if (compareTo(other) <= 0) this else other

    fun max(other: Rational): Rational = if (compareTo(other) >= 0) this else other
}

private class NormalRational(override val numer: Long, override val denom: Long) : Rational() {
    init {
        require(denom != 0L)
    }

    override val isFinite: Boolean
        get() = true

    override val isInfinite: Boolean
        get() = false

    override val isNonNegative: Boolean
        get() = numer >= 0

    override val isPositive: Boolean
        get() = numer > 0

    override val isNegative: Boolean
        get() = numer < 0

    override val isZero: Boolean
        get() = numer == 0L

    override fun toString(): String = if (denom == 1L) numer.toString() else "$numer/$denom"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NormalRational) return false
        return numer == other.numer && denom == other.denom
    }

    override fun hashCode(): Int = (31L * numer + denom).toInt()

    override fun unaryMinus(): Rational = NormalRational(-numer, denom)

    private fun plus(otherNumer: Long, otherDenom: Long): Rational {
        return ratio(
                addExact(multiplyExact(numer, otherDenom), multiplyExact(denom, otherNumer)),
                multiplyExact(denom, otherDenom)
        )
    }

    override operator fun plus(other: Rational): Rational {
        return if (other.isInfinite) {
            other + this
        } else {
            plus(other.numer, other.denom)
        }
    }

    override operator fun plus(other: Long): Rational {
        val sum = addExact(numer, multiplyExact(denom, other))
        return if (sum == 0L) {
            ZERO
        } else {
            NormalRational(sum, denom)
        }
    }

    override operator fun minus(other: Rational): Rational = when (other) {
        NEGATIVE_INFINITY -> POSITIVE_INFINITY
        POSITIVE_INFINITY -> NEGATIVE_INFINITY
        else -> plus(-other.numer, other.denom)
    }

    override operator fun minus(other: Long): Rational = this + (-other)

    private fun times(otherNumer: Long, otherDenom: Long): Rational
            = ratio(multiplyExact(numer, otherNumer), multiplyExact(denom, otherDenom))

    override operator fun times(other: Rational): Rational {
        return if (other.isInfinite) {
            other * this
        } else {
            times(other.numer, other.denom)
        }
    }

    override operator fun times(other: Long): Rational = ratio(multiplyExact(numer, other), denom)

    override fun div(other: Rational): Rational {
        return if (other == ZERO) {
            this / 0L
        } else {
            times(other.denom, other.numer)
        }
    }

    override fun div(other: Long): Rational {
        return if (other == 0L) {
            when {
                numer == 0L -> throw IllegalArgumentException()
                numer > 0 -> POSITIVE_INFINITY
                else -> NEGATIVE_INFINITY
            }
        } else {
            ratio(numer, multiplyExact(denom, other))
        }
    }

    override val isInteger: Boolean
        get() = denom == 1L

    override fun toInteger(): Int = if (isInteger) numer.toInt() else throw UnsupportedOperationException()

    override fun asInteger(): Int? = if (isInteger) numer.toInt() else null

    override fun toLong(): Long = if (isInteger) numer else throw UnsupportedOperationException()

    override fun asLong(): Long? = if (isInteger) numer else null

    override fun toFloat(): Float = if (isInteger) numer.toFloat() else (numer / denom).toFloat()

    override fun toDouble(): Double = if (isInteger) numer.toDouble() else numer / denom.toDouble()

    override fun compareTo(other: Rational): Int {
        return if (other.isInfinite) {
            -(other.compareTo(this))
        } else {
            multiplyExact(numer, other.denom).compareTo(multiplyExact(denom, other.numer))
        }
    }

    override fun floor(): Int = if (isInteger) numer.toInt() else floorDiv(numer, denom).toInt()

    override fun ceil(): Int = if (isInteger) numer.toInt() else (floorDiv(numer, denom) + 1).toInt()

    override fun round(): Int {
        return (if (isInteger) {
            numer
        } else {
            val floorDiv = floorDiv(numer, denom)
            val floorMod = floorMod(numer, denom)
            if (2 * floorMod >= denom) floorDiv + 1 else floorDiv
        }).toInt()
    }

    override fun roundTowards(target: Int): Int = if (numer < target * denom) ceil() else floor()
}

abstract class LimitRational : Rational() {
    override val numer: Long
        get() = throw UnsupportedOperationException()

    override val denom: Long
        get() = throw UnsupportedOperationException()

    override val isFinite: Boolean
        get() = false

    override val isInfinite: Boolean
        get() = true

    override val isZero: Boolean
        get() = false

    override fun plus(other: Long): Rational = this

    override fun minus(other: Rational): Rational {
        if (other === this) {
            throw UnsupportedOperationException()
        } else {
            return this
        }
    }

    override fun minus(other: Long): Rational = this

    override fun times(other: Rational): Rational {
        return when {
            other.isZero -> throw UnsupportedOperationException()
            other.isPositive -> this
            else -> -this
        }
    }

    override fun times(other: Long): Rational {
        return when {
            other == 0L -> throw UnsupportedOperationException()
            other > 0 -> this
            else -> -this
        }
    }

    override fun div(other: Rational): Rational {
        return when {
            other.isZero -> this
            other.isInfinite -> throw UnsupportedOperationException()
            else -> this * other
        }
    }

    override fun div(other: Long): Rational = if (other == 0L) this else this * other

    override val isInteger: Boolean
        get() = false

    override fun toInteger(): Int = throw UnsupportedOperationException()

    override fun asInteger(): Int? = null

    override fun toLong(): Long = throw UnsupportedOperationException()

    override fun asLong(): Long? = null

    override fun floor(): Int = throw UnsupportedOperationException()

    override fun ceil(): Int = throw UnsupportedOperationException()

    override fun round(): Int = throw UnsupportedOperationException()

    override fun roundTowards(target: Int): Int = throw UnsupportedOperationException()
}

fun Double.toRational(): Rational = Rational.valueOf(this)

fun Long.toRational(): Rational = Rational.valueOf(this)

fun Int.toRational(): Rational = this.toLong().toRational()
