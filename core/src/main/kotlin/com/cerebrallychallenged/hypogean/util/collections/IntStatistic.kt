package com.cerebrallychallenged.hypogean.util.collections

sealed class IntStatistic {
    companion object {
        val Empty = ArrayIntStatistic(0.0f, 1, intArrayOf(0), floatArrayOf(1.0f, 1.0f, 1.0f))
    }

    data class Point(val value: Int, val probability: Float) {
        fun flip(): Point = copy(value = -value)
    }

    data class FinerPoint(val value: Float, val probability: Float)

    abstract val expectedValue: Float

    abstract fun probabilityOf(value: Int): Float

    abstract fun probabilityOfAtMost(value: Int): Float

    abstract fun probabilityOfAtLeast(value: Int): Float

    abstract fun flip(): IntStatistic

    abstract fun points(): List<Point>

    abstract fun upperBoundPoints(): List<Point>

    abstract fun lowerBoundPoints(): List<Point>
}

fun List<IntStatistic.Point>.finer(): Array<IntStatistic.FinerPoint> = Array(size * 2 - 2) {
    val point = this[(it + 1) / 2]
    val sgn = -2 * (it and 1) + 1
    IntStatistic.FinerPoint(point.value.toFloat() + 0.49f * sgn, point.probability)
}

class FlippedIntStatistic(private val base: ArrayIntStatistic) : IntStatistic() {
    override val expectedValue: Float
        get() = -base.expectedValue

    override fun probabilityOf(value: Int): Float = base.probabilityOf(-value)

    override fun probabilityOfAtMost(value: Int): Float = base.probabilityOfAtLeast(-value)

    override fun probabilityOfAtLeast(value: Int): Float = base.probabilityOfAtMost(-value)

    override fun flip(): IntStatistic = base

    override fun points(): List<Point> = base.points().asReversed().map(Point::flip)

    override fun upperBoundPoints(): List<Point> =
        base.lowerBoundPoints().asReversed().map(Point::flip)

    override fun lowerBoundPoints(): List<Point> =
        base.upperBoundPoints().asReversed().map(Point::flip)
}

class ArrayIntStatistic(
    override val expectedValue: Float,
    private val distinctCount: Int,
    private val valueArray: IntArray,
    private val probabilityArray: FloatArray
) : IntStatistic() {
    override fun probabilityOf(value: Int): Float {
        val index = valueArray.binarySearch(value)
        return if (index >= 0) {
            probabilityArray[index]
        } else {
            0.0f
        }
    }

    override fun probabilityOfAtMost(value: Int): Float = when {
        distinctCount == 0 -> if (value < 0) 0.0f else 1.0f
        value < valueArray.first() -> 0.0f
        value > valueArray.last() -> 1.0f
        else -> {
            val index = valueArray.binarySearch(value)
            if (index >= 0) {
                probabilityArray[distinctCount + index]
            } else {
                val insertionIndex = -index - 1
                require(insertionIndex in 1 until distinctCount)
                probabilityArray[distinctCount + insertionIndex - 1]
            }
        }
    }

    override fun probabilityOfAtLeast(value: Int): Float = when {
        distinctCount == 0 -> if (value > 0) 0.0f else 1.0f
        value < valueArray.first() -> 1.0f
        value > valueArray.last() -> 0.0f
        else -> {
            val index = valueArray.binarySearch(value)
            if (index >= 0) {
                probabilityArray[2 * distinctCount + index]
            } else {
                val insertionIndex = -index - 1
                require(insertionIndex in 1 until distinctCount)
                probabilityArray[2 * distinctCount + insertionIndex]
            }
        }
    }

    override fun flip(): IntStatistic = FlippedIntStatistic(this)

    private fun createPointList(probability: (Int) -> Float): List<Point> = buildList {
        for (value in (valueArray.first() - 1)..(valueArray.last() + 1)) {
            add(Point(value, probability(value)))
        }
    }

    override fun points(): List<Point> = createPointList(::probabilityOf)

    override fun upperBoundPoints(): List<Point> = createPointList(::probabilityOfAtMost)

    override fun lowerBoundPoints(): List<Point> = createPointList(::probabilityOfAtLeast)
}
