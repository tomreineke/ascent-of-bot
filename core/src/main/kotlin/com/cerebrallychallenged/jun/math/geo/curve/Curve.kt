package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.geo.FloatVector
import com.cerebrallychallenged.jun.math.geo.LinearInterpolator
import com.cerebrallychallenged.jun.math.geo.Metric

interface Curve<T> : (Float) -> T {
    val startTime: Float

    val endTime: Float

    val startPoint: T

    val endPoint: T

    val metric: Metric<T>

    val interpolator: LinearInterpolator<T>

    fun <U> map(forward: (T) -> U, backward: (U) -> T): Curve<U> {
        return object : Curve<U> {
            override fun invoke(time: Float): U {
                return forward(this@Curve(time))
            }

            override val startTime: Float
                get() = this@Curve.startTime


            override val endTime: Float
                get() = this@Curve.endTime

            override val startPoint: U
                get() = forward(this@Curve.startPoint)

            override val endPoint: U
                get() = forward(this@Curve.endPoint)

            override val metric: Metric<U>
                get() {
                    val metric = this@Curve.metric
                    return fun(first: U, second: U) = metric(backward(first), backward(second))
                }

            override val interpolator: LinearInterpolator<U>
                get() {
                    val interpolator = this@Curve.interpolator
                    return fun(first: U, alpha: Float, second: U): U
                            = forward(interpolator(backward(first), alpha, backward(second)))
                }
        }
    }

    fun flatten(step: Float): Polyline<T> {
        val builder = Polyline.Builder(startPoint, startTime, metric, interpolator)
        var t = startTime
        while (t <= endTime) {
            builder.lineTo(this(t), t)
            t += step
        }
        builder.lineTo(endPoint)
        return builder.build()
    }

    fun reparameterizeAtUnitSpeed(step: Float, speed: Float): Polyline<T> {
        val builder = Polyline.Builder(startPoint, startTime, metric, interpolator)
        builder.speed = speed
        var t = startTime
        while (t <= endTime) {
            builder.lineTo(this(t))
            t += step
        }
        builder.lineTo(endPoint)
        return builder.build()
    }
}

operator fun <T : FloatVector<*, T, *>> Curve<T>.times(factor: Float): Curve<T> = object : Curve<T> {
    override val startPoint: T
        get() = this@times.startPoint * factor
    override val endPoint: T
        get() = this@times.endPoint * factor
    override fun invoke(time: Float): T = this@times.invoke(time) * factor

    override val startTime: Float
        get() = this@times.startTime
    override val endTime: Float
        get() = this@times.endTime
    override val metric: Metric<T>
        get() = this@times.metric
    override val interpolator: LinearInterpolator<T>
        get() = this@times.interpolator
}