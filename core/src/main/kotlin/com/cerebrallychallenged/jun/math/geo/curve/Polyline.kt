package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.clamp
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.math.interpolate
import kotlin.math.abs
import kotlin.math.ceil
import java.util.*

private const val THRESHOLD = 0.00000001f

class Polyline<T>(
        val function: InterpolatingFunction<T>,
        override val startTime: Float,
        override val endTime: Float,
        override val startPoint: T,
        override val endPoint: T,
        val length: Float,
        override val metric: Metric<T>
) : Curve<T>, (Float) -> T by function {
    class Builder<T>(
            private val startPoint: T,
            private val startTime: Float,
            val metric: Metric<T>,
            private val interpolator: LinearInterpolator<T>
    ) {
        var time: Float = startTime
            private set

        var speed: Float = 1.0f

        private var latest: T = startPoint

        private var length: Float = 0.0f

        private val points = TreeMap<Float, T>().also { it[startTime] = startPoint }

        //FIXME
        fun withSpeed(speed: Float): Builder<T> {
            this.speed = speed
            return this
        }

        fun stayUntil(time: Float): Builder<T> {
            if (time - this.time > THRESHOLD) {
                points[time] = latest
            }
            this.time = time
            return this
        }

        fun stayFor(deltaTime: Float): Builder<T> {
            if (deltaTime > THRESHOLD) {
                time += deltaTime
                points[time] = latest
            }
            return this
        }

        fun lineTo(target: T): Builder<T> {
            val distance = metric(latest, target)
            if (distance > THRESHOLD) {
                val deltaTime = distance / speed
                time += deltaTime
                points[time] = target
                latest = target
                length += distance
            }
            return this
        }

        fun lineTo(target: T, time: Float): Builder<T> {
            require(this.time <= time)
            val distance = metric(latest, target)
            if (distance > THRESHOLD && time - this.time > THRESHOLD) {
                points[time] = target
                latest = target
                length += distance
            }
            this.time = time
            return this
        }

        fun build(): Polyline<T> = Polyline(
                InterpolatingFunction(points, interpolator),
                startTime,
                time,
                startPoint,
                latest,
                length,
                metric
        )
    }

    companion object {
        fun from(source: Float, time: Float = 0.0f): Builder<Float> = Builder(source, time, ::absDiff, ::interpolate)

        fun constant(value: Float, duration: Float = 0.0f): Polyline<Float> = from(value).stayFor(duration).build()

        fun from(source: Angle, time: Float = 0.0f): Builder<Angle> = Builder(source, time, ::absDiff, ::interpolate)

        fun constant(value: Angle, duration: Float = 0.0f): Polyline<Angle> = from(value).stayFor(duration).build()

        fun <V : FloatVector<*, V, *>> from(source: V, time: Float = 0.0f): Builder<V>
                = Builder(source, time, FloatVector<*, V, *>::distanceTo, FloatVector<*, V, *>::interpolate)

        fun <V : FloatVector<*, V, *>> constant(value: V, duration: Float = 0.0f): Polyline<V>
                = from(value).stayFor(duration).build()

        fun from(source: Quaternion, time: Float = 0.0f): Builder<Quaternion>
                = Builder(source, time, Quaternion::angularDistanceTo, Quaternion::interpolate)

        fun constant(value: Quaternion, duration: Float = 0.0f): Polyline<Quaternion>
                = from(value).stayFor(duration).build()

        enum class ZRotationInterpolation { SHORTEST, CLOCKWISE, COUNTERCLOCKWISE }

        fun linearZRotation(fromAngle: Angle, toAngle: Angle, speed: Float = 1.0f,
                             variant: ZRotationInterpolation = ZRotationInterpolation.SHORTEST): Polyline<Quaternion> =
                buildPolyline(Quaternion.fromAxisAngle(Vec3f.UNIT_Z, fromAngle)) {
                    this.speed = speed

                    when (variant) {
                        ZRotationInterpolation.SHORTEST ->
                            lineTo(Quaternion.fromAxisAngle(Vec3f.UNIT_Z, toAngle))

                        ZRotationInterpolation.CLOCKWISE, ZRotationInterpolation.COUNTERCLOCKWISE -> {
                            val from = fromAngle
                            val to = toAngle

                            // unsigned delta in [0, 2PI)
                            val unsignedDelta = (to - from).floorMod(Angle.DEGREE_360)

                            val signedDelta = when (variant) {
                                ZRotationInterpolation.CLOCKWISE -> unsignedDelta
                                ZRotationInterpolation.COUNTERCLOCKWISE -> unsignedDelta - Angle.DEGREE_360
                                else -> unsignedDelta
                            }

                            val deltaF = signedDelta.toRadians()
                            val maxStep = (Angle.DEGREE_180 * 0.99f).toRadians() // keep each segment < PI
                            val steps = kotlin.math.max(1, ceil(abs(deltaF) / maxStep).toInt())

                            for (i in 1..steps) {
                                val t = i.toFloat() / steps.toFloat()
                                val intermediate = from + signedDelta * t
                                lineTo(Quaternion.fromAxisAngle(Vec3f.UNIT_Z, intermediate))
                            }
                        }
                    }
                }

        fun constantZRotation(angle: Angle, duration: Float = 0.0f): Polyline<Quaternion> =
                constant(Quaternion.fromAxisAngle(Vec3f.UNIT_Z, angle), duration)

        fun constantLookAt(from: Vec2f, to: Vec2f, up: Vec3f = Vec3f.UNIT_Z, duration: Float = 0.0f): Polyline<Quaternion> =
                constant((to - from).append(0.0f).toLookAtWith(up), duration)
    }

    override val interpolator: LinearInterpolator<T>
        get() = function.interpolator

    val vertices: Collection<T>
        get() = function.points.values
}

data class PointOnPolylineProjection<V: FloatVector<*, V, *>>(
        val projectionTime: Float,
        val imagePoint: V,
        val forward: V,
        val beam: V,
        val distance: Float
)

fun <V : FloatVector<*, V, *>> Polyline<V>.project(point: V): PointOnPolylineProjection<V> {
    data class SegmentProjection(val fromTime: Float, val fromPoint: V, val toTime: Float, val toPoint: V) {
        val clampedAlpha: Float = clamp(point.projectionAlphaOnSegment(fromPoint, toPoint), 0.0f, 1.0f)
        val imagePoint = fromPoint.interpolate(clampedAlpha, toPoint)
        val beam = imagePoint - point
        val distance = beam.length
    }
    return function.points
            .entries
            .asSequence()
            .zipWithNext()
            .map { (firstEntry, secondEntry) ->
                SegmentProjection(firstEntry.key, firstEntry.value, secondEntry.key, secondEntry.value)
            }.minByOrNull { it.distance }?.let { projection ->
                PointOnPolylineProjection(
                        interpolate(projection.fromTime, projection.clampedAlpha, projection.toTime),
                        projection.imagePoint,
                        (projection.toPoint - projection.fromPoint).normalized(),
                        projection.beam,
                        projection.distance
                )
            }
            ?: let {
                val beam = startPoint - point
                PointOnPolylineProjection(
                        startTime,
                        startPoint,
                        startPoint.vectorSpace.basis(0),
                        beam,
                        beam.length
                )
            }
}

fun <V : FloatVector<*, V, *>> buildPolyline(from: V, f: Polyline.Builder<V>.() -> Unit): Polyline<V> =
        Polyline.from(from).apply(f).build()

fun buildPolyline(from: Quaternion, f: Polyline.Builder<Quaternion>.() -> Unit): Polyline<Quaternion> =
        Polyline.from(from).apply(f).build()
