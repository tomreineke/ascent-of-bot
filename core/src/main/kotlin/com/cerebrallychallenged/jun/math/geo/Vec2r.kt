package com.cerebrallychallenged.jun.math.geo

class Vec2r internal constructor(val x: Rational, val y: Rational) : RationalVector<Vec2r, Vec2i, Vec2f, Vec2d>() {

    companion object {
        @JvmField
        val ZERO = vec(Rational.ZERO, Rational.ZERO)

        @JvmField
        val ONE = vec(Rational.ONE, Rational.ONE)

        @JvmField
        val ONE_HALF = vec(Rational.ONE_HALF, Rational.ONE_HALF)

        @JvmField
        val UNIT_X = vec(Rational.ONE, Rational.ZERO)

        @JvmField
        val UNIT_Y = vec(Rational.ZERO, Rational.ONE)

        @JvmField
        val VECTOR_SPACE: VectorSpace<Vec2r> = object : VectorSpace<Vec2r> {
            override val dimension: Int
                get() = 2

            override val zero: Vec2r
                get() = ZERO

            override val one: Vec2r
                get() = ONE


            override fun basis(index: Int): Vec2r {
                return when (index) {
                    INDEX_X -> UNIT_X
                    INDEX_Y -> UNIT_Y
                    else -> throw IllegalArgumentException()
                }
            }

            override val pointSize: Vec2r
                get() = ZERO

            override val emptyBounds: Bounds<Vec2r> = EmptyBounds(ZERO)
        }
    }

    override val dimension: Int
        get() = 2

    override val vectorSpace: VectorSpace<Vec2r>
        get() = VECTOR_SPACE

    override val isZero: Boolean
        get() = x.isZero && y.isZero

    override fun thisAsV(): Vec2r {
        return this
    }

    override fun hashCode(): Int = 31 * x.hashCode() + y.hashCode()

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is Vec2r -> false
        else -> x == other.x && y == other.y
    }

    override fun toString(): String ="($x, $y)"

    inline fun <reified T, reified V> mapReduce(reduce: (T, T) -> V, map: (Rational) -> T): V =
            reduce(map(x), map(y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2r, reduce: (T, T) -> V, zip: (Rational, Rational) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2i, reduce: (T, T) -> V, zip: (Rational, Int) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2f, reduce: (T, T) -> V, zip: (Rational, Float) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    inline fun <reified T, reified V> zipReduce(rhs: Vec2d, reduce: (T, T) -> V, zip: (Rational, Double) -> T): V =
            reduce(zip(x, rhs.x), zip(y, rhs.y))

    override operator fun unaryMinus(): Vec2r = mapReduce(::Vec2r) { -it }

    override operator fun plus(rhs: Vec2r): Vec2r = zipReduce(rhs, ::Vec2r) { a, b -> a + b }

    override operator fun minus(rhs: Vec2r): Vec2r = zipReduce(rhs, ::Vec2r) { a, b -> a - b }

    override operator fun times(factor: Rational): Vec2r = mapReduce(::Vec2r) { it * factor }

    override fun min(rhs: Vec2r): Vec2r = zipReduce(rhs, ::Vec2r, Rational::min)

    override fun max(rhs: Vec2r): Vec2r = zipReduce(rhs, ::Vec2r, Rational::max)

    override fun pointwiseMul(rhs: Vec2r): Vec2r = zipReduce(rhs, ::Vec2r) { a, b -> a * b }

    override fun dot(rhs: Vec2r): Rational = zipReduce(rhs, { x, y -> x + y }) { a, b -> a * b }

    override fun isLessThan(rhs: Vec2r): Boolean = x < rhs.x && y < rhs.y

    override fun isLessEqualThan(rhs: Vec2r): Boolean = x <= rhs.x && y <= rhs.y

    override operator fun get(index: Int): Rational {
        return when (index) {
            INDEX_X -> x
            INDEX_Y -> y
            else -> throw IllegalArgumentException()
        }
    }

//    fun append(z: Rational): Vec3r = vec(x, y, z)
//
//    fun append(z: Rational, w: Rational): Vec4r = vec(x, y, z, w)
//
//    fun append(vecZW: Vec2r): Vec4r = vec(x, y, vecZW.x, vecZW.y)

    override fun floor(): Vec2i = mapReduce(::Vec2i) { it.floor() }

    override fun ceil(): Vec2i = mapReduce(::Vec2i) { it.ceil() }

    override fun round(): Vec2i = mapReduce(::Vec2i) { it.round() }

    override fun roundTowards(target: Vec2i): Vec2i = zipReduce(target, ::Vec2i) { a, b -> a.roundTowards(b) }

    override fun toFloat(): Vec2f = mapReduce(::Vec2f) { it.toFloat() }

    override fun toDouble(): Vec2d = mapReduce(::Vec2d) { it.toDouble() }

//    fun angle(): Double = Math.atan2(y, x)

    fun turnClockwise(): Vec2r = vec(y, -x)

    fun turnCounterClockwise(): Vec2r = vec(-y, x)

    operator fun component1(): Rational = x

    operator fun component2(): Rational = y
}

fun <T> Iterable<T>.sumBy(selector: (T) -> Vec2r): Vec2r = fold(Vec2r.ZERO) { acc, element -> acc + selector(element) }

fun Iterable<Vec2r>.sum(): Vec2r = sumBy(fun(it: Vec2r): Vec2r = it)

fun <T> Sequence<T>.sumBy(selector: (T) -> Vec2r): Vec2r = fold(Vec2r.ZERO) { acc, element -> acc + selector(element) }

fun Sequence<Vec2r>.sum(): Vec2r = sumBy(fun(it: Vec2r): Vec2r = it)
