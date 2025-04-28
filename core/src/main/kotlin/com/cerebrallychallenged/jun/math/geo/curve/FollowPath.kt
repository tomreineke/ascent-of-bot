package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.*
import com.cerebrallychallenged.jun.math.interpolate

private const val INITIAL_H = 0.0005f

private const val BASE_H = 2.0f

private const val MAX_EXPONENT = 22

private const val THRESHOLD = 0.00000001f

/**
 * Returns the rotation curve of objects following this path.
 */
fun Curve<Vec2f>.toFollowCurve(): Curve<Angle> {
    fun tangentialAngle(time: Float): Angle {
        var h = INITIAL_H
        for (i in 0 until MAX_EXPONENT) {
            h *= BASE_H
            val direction = this(time + h) - this(time - h)
            if (direction.length >= THRESHOLD) {
                return direction.angle()
            }
        }
        return Angle.ZERO
    }
    return object : Curve<Angle> {
        override val startTime: Float = this@toFollowCurve.startTime

        override val endTime: Float = this@toFollowCurve.endTime

        override val startPoint: Angle = tangentialAngle(startTime)

        override val endPoint: Angle = tangentialAngle(endTime)

        override val metric: Metric<Angle> = ::absDiff

        override val interpolator: LinearInterpolator<Angle> = ::interpolate

        override fun invoke(time: Float): Angle {
            return when {
                time <= startTime -> startPoint
                time >= endTime -> endPoint
                else -> tangentialAngle(time)
            }
        }
    }
}

/**
 * Returns the rotation curve of objects following this path.
 */
fun Curve<Vec3f>.toFollowCurve(up: Vec3f): Curve<Quaternion> = toFollowCurve(Polyline.constant(up))

/**
 * Returns the rotation curve of objects following this path.
 */
fun Curve<Vec3f>.toFollowCurve(upCurve: Curve<Vec3f>): Curve<Quaternion> {
    fun tangentialQuaternion(time: Float): Quaternion {
        var h = INITIAL_H
        for (i in 0 until MAX_EXPONENT) {
            h *= BASE_H
            val direction = this(time + h) - this(time - h)
            if (direction.length >= THRESHOLD) {
                return direction.toLookAtWith(upCurve(time))
            }
        }
        return Quaternion.IDENTITY
    }
    return object : Curve<Quaternion> {
        override val startTime: Float = this@toFollowCurve.startTime

        override val endTime: Float = this@toFollowCurve.endTime

        override val startPoint: Quaternion = tangentialQuaternion(startTime)

        override val endPoint: Quaternion = tangentialQuaternion(endTime)

        override val metric: Metric<Quaternion> = Quaternion::angularDistanceTo

        override val interpolator: LinearInterpolator<Quaternion> = Quaternion::interpolate

        override fun invoke(time: Float): Quaternion {
            return when {
                time <= startTime -> startPoint
                time >= endTime -> endPoint
                else -> tangentialQuaternion(time)
            }
        }
    }
}
