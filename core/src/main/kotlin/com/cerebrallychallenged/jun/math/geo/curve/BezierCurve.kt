package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.math.interpolate
import java.util.NavigableMap
import java.util.TreeMap

private const val THRESHOLD = 0.00000001f

// For https://en.wikipedia.org/wiki/De_Casteljau%27s_algorithm
private const val RECURSION_DEPTH = 4

class BezierCurve<T> private constructor(
    internal val segments: NavigableMap<Float, Segment<T>>,
    override val startTime: Float,
    override val endTime: Float,
    override val startPoint: T,
    override val endPoint: T,
    val length: Float,
    override val metric: Metric<T>,
    override val interpolator: LinearInterpolator<T>
) : Curve<T> {
    internal abstract class Segment<T> {
        abstract fun get(time: Float, interpolator: LinearInterpolator<T>): T

        abstract val source: T

        abstract val target: T
    }

    internal class Constant<T>(override val source: T) : Segment<T>() {
        override fun get(time: Float, interpolator: LinearInterpolator<T>): T = source

        override val target: T
            get() = source
    }

    internal abstract class InterpolatingSegment<T>(
            val startTime: Float,
            val duration: Float,
            override val source: T,
            override val target: T
    ) : Segment<T>() {
        override fun get(time: Float, interpolator: LinearInterpolator<T>): T {
            val alpha = (time - startTime) / duration
            return when {
                alpha <= 0.0 -> source
                alpha >= 1.0 -> target
                else -> getByAlpha(alpha, interpolator)
            }
        }

        abstract fun getByAlpha(alpha: Float, interpolator: LinearInterpolator<T>): T
    }

    internal class LinearSegment<T>(
            startTime: Float,
            duration: Float,
            source: T,
            target: T
    ) : InterpolatingSegment<T>(startTime, duration, source, target) {
        override fun getByAlpha(alpha: Float, interpolator: LinearInterpolator<T>): T = interpolator(source, alpha, target)
    }

    internal class QuadSegment<T>(
            startTime: Float,
            duration: Float,
            source: T,
            val control: T,
            target: T
    ) : InterpolatingSegment<T>(startTime, duration, source, target) {
        companion object {
            fun <T> estimateLength(
                    source: T,
                    control: T,
                    target: T,
                    metric: Metric<T>,
                    interpolator: LinearInterpolator<T>,
                    recursionDepth: Int
            ) : Float {
                return if (recursionDepth == 0) {
                    metric(source, control) + metric(control, target)
                } else {
                    val a0 = interpolator(source, 0.5f, control)
                    val a1 = interpolator(control, 0.5f, target)
                    val b = interpolator(a0, 0.5f, a1)
                    (
                            estimateLength(source, a0, b, metric, interpolator, recursionDepth - 1)
                            + estimateLength(b, a1, target, metric, interpolator, recursionDepth - 1)
                    )
                }
            }
        }

        override fun getByAlpha(alpha: Float, interpolator: LinearInterpolator<T>): T {
            val a0 = interpolator(source, alpha, control)
            val a1 = interpolator(control, alpha, target)
            return interpolator(a0, alpha, a1)
        }
    }

    internal class CubicSegment<T>(
            startTime: Float,
            duration: Float,
            source: T,
            val firstControl: T,
            val secondControl: T,
            target: T
    ) : InterpolatingSegment<T>(startTime, duration, source, target) {
        companion object {
            fun <T> estimateLength(
                    source: T,
                    firstControl: T,
                    secondControl: T,
                    target: T,
                    metric: Metric<T>,
                    interpolator: LinearInterpolator<T>,
                    recursionDepth: Int
            ) : Float {
                return if (recursionDepth == 0) {
                    metric(source, firstControl) + metric(firstControl, secondControl) + metric(secondControl, target)
                } else {
                    val a0 = interpolator(source, 0.5f, firstControl)
                    val a1 = interpolator(firstControl, 0.5f, secondControl)
                    val a2 = interpolator(secondControl, 0.5f, target)
                    val b0 = interpolator(a0, 0.5f, a1)
                    val b1 = interpolator(a1, 0.5f, a2)
                    val c = interpolator(b0, 0.5f, b1)
                    return (
                            estimateLength(source, a0, b0, c, metric, interpolator, recursionDepth - 1)
                            + estimateLength(c, b1, a2, target, metric, interpolator, recursionDepth - 1)
                    )
                }
            }
        }

        override fun getByAlpha(alpha: Float, interpolator: LinearInterpolator<T>): T {
            val a0 = interpolator(source, alpha, firstControl)
            val a1 = interpolator(firstControl, alpha, secondControl)
            val a2 = interpolator(secondControl, alpha, target)
            val b0 = interpolator(a0, alpha, a1)
            val b1 = interpolator(a1, alpha, a2)
            return interpolator(b0, alpha, b1)
        }
    }

    class Builder<T>(
            val startPoint: T,
            private val startTime: Float,
            val metric: Metric<T>,
            private val interpolator: LinearInterpolator<T>
    ) {
        var time: Float = startTime
            private set

        var speed: Float = 1.0f

        var latestPoint: T = startPoint
            private set

        private var length: Float = 0.0f

        private val segments = TreeMap<Float, Segment<T>>()//.also { it[startTime] = startPoint }

        fun stayUntil(time: Float): Builder<T> {
            if (time - this.time > THRESHOLD) {
                segments[time] = Constant(latestPoint)
            }
            this.time = time
            return this
        }

        fun stayFor(deltaTime: Float): Builder<T> {
            if (deltaTime > THRESHOLD) {
                time += deltaTime
                segments[time] = Constant(latestPoint)
            }
            return this
        }

        fun lineTo(target: T): Builder<T> {
            val distance = metric(latestPoint, target)
            if (distance > THRESHOLD) {
                val deltaTime = distance / speed
                segments[time] = LinearSegment(time, deltaTime, latestPoint, target)
                time += deltaTime
                latestPoint = target
                length += distance
            }
            return this
        }

        fun lineTo(target: T, time: Float): Builder<T> {
            require(this.time <= time)
            val distance = metric(latestPoint, target)
            val deltaTime = time - this.time
            if (distance > THRESHOLD && deltaTime > THRESHOLD) {
                segments[time] = LinearSegment(this.time, deltaTime, latestPoint, target)
                latestPoint = target
                length += distance
            }
            this.time = time
            return this
        }

        fun quadTo(control: T, target: T): Builder<T> {
            val distance = QuadSegment.estimateLength(
                    latestPoint,
                    control,
                    target,
                    metric,
                    interpolator,
                    RECURSION_DEPTH
            )
            if (distance > THRESHOLD) {
                val deltaTime = distance / speed
                segments[time] = QuadSegment(time, deltaTime, latestPoint, control, target)
                time += deltaTime
                latestPoint = target
                length += distance
            }
            return this
        }

        fun quadTo(control: T, target: T, time: Float): Builder<T> {
            require(this.time <= time)
            val distance = QuadSegment.estimateLength(
                    latestPoint,
                    control,
                    target,
                    metric,
                    interpolator,
                    RECURSION_DEPTH
            )
            val deltaTime = time - this.time
            if (distance > THRESHOLD && deltaTime > THRESHOLD) {
                segments[time] = QuadSegment(this.time, deltaTime, latestPoint, control, target)
                latestPoint = target
                length += distance
            }
            this.time = time
            return this
        }

        fun cubicTo(firstControl: T, secondControl: T, target: T): Builder<T> {
            val distance = CubicSegment.estimateLength(
                    latestPoint,
                    firstControl,
                    secondControl,
                    target,
                    metric,
                    interpolator,
                    RECURSION_DEPTH
            )
            if (distance > THRESHOLD) {
                val deltaTime = distance / speed
                segments[time] = CubicSegment(time, deltaTime, latestPoint, firstControl, secondControl, target)
                time += deltaTime
                latestPoint = target
                length += distance
            }
            return this
        }

        fun cubicTo(firstControl: T, secondControl: T, target: T, time: Float): Builder<T> {
            require(this.time < time)
            val distance = CubicSegment.estimateLength(
                    latestPoint,
                    firstControl,
                    secondControl,
                    target,
                    metric,
                    interpolator,
                    RECURSION_DEPTH
            )
            val deltaTime = time - this.time
            if (distance > THRESHOLD && deltaTime > THRESHOLD) {
                segments[time] = CubicSegment(this.time, deltaTime, latestPoint, firstControl, secondControl, target)
                latestPoint = target
                length += distance
            }
            this.time = time
            return this
        }

        fun build(): BezierCurve<T> = BezierCurve(
                segments,
                startTime,
                time,
                startPoint,
                latestPoint,
                length,
                metric,
                interpolator
        )
    }

    companion object {
        fun from(source: Float, time: Float = 0.0f): Builder<Float> = Builder(source, time, ::absDiff, ::interpolate)

        fun constant(value: Float, duration: Float = 0.0f): BezierCurve<Float> = from(value).stayFor(duration).build()

        fun <V : FloatVector<*, V, *>> from(source: V, time: Float = 0.0f): Builder<V>
                = Builder(source, time, FloatVector<*, V, *>::distanceTo, FloatVector<*, V, *>::interpolate)

        fun <V : FloatVector<*, V, *>> constant(value: V, duration: Float = 0.0f): BezierCurve<V>
                = from(value).stayFor(duration).build()

        fun from(source: Quaternion, time: Float = 0.0f): Builder<Quaternion>
                = Builder(source, time, Quaternion::angularDistanceTo, Quaternion::interpolate)

        fun constant(value: Quaternion, duration: Float = 0.0f): BezierCurve<Quaternion>
                = from(value).stayFor(duration).build()
    }

    override fun invoke(time: Float): T {
        val entry = segments.floorEntry(time)
        return if (entry != null) {
            entry.value.get(time, interpolator)
        } else {
            segments.firstEntry().value.source
        }
    }
}
