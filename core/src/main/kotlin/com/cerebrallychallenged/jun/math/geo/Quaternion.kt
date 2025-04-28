package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.cos
import com.cerebrallychallenged.jun.math.radians
import com.cerebrallychallenged.jun.math.sin
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.acos
import kotlin.math.sin
import kotlin.math.sqrt

data class AxisAngle(val axis: Vec3f, val angle: Angle)

class Quaternion internal constructor(private val vector: Vec4f) {

    companion object {
        @JvmField
        val IDENTITY = Quaternion(Vec4f.UNIT_W)

        @JvmStatic
        fun create(vector: Vec4f): Quaternion {
            return Quaternion(vector)
        }

        @JvmStatic
        fun fromAxisAngle(axis: Vec3f, angle: Float): Quaternion
                = fromAxisAngle(axis, angle.radians)

        fun fromAxisAngle(axis: Vec3f, angle: Angle): Quaternion {
            return fromNormalAxisAngle(axis.normalized(), angle)
        }

        @JvmStatic
        fun fromNormalAxisAngle(normalAxis: Vec3f, angle: Float): Quaternion
                = fromNormalAxisAngle(normalAxis, angle.radians)

        fun fromNormalAxisAngle(normalAxis: Vec3f, angle: Angle): Quaternion {
            val halfAngle = angle * 0.5f
            return Quaternion((normalAxis * sin(halfAngle)).append(cos(halfAngle)))
        }

        private fun fromRotationMatrix(
                mat00: Float, mat01: Float, mat02: Float,
                mat10: Float, mat11: Float, mat12: Float,
                mat20: Float, mat21: Float, mat22: Float
        ): Quaternion {
            var m00 = mat00
            var m01 = mat01
            var m02 = mat02
            var m10 = mat10
            var m11 = mat11
            var m12 = mat12
            var m20 = mat20
            var m21 = mat21
            var m22 = mat22
            // TODO[A] direct implementation
            var lengthSquared = m00 * m00 + m10 * m10 + m20 * m20
            if (lengthSquared != 1.0f && lengthSquared != 0.0f) {
                lengthSquared = 1.0f / sqrt(lengthSquared)
                m00 *= lengthSquared
                m10 *= lengthSquared
                m20 *= lengthSquared
            }
            lengthSquared = m01 * m01 + m11 * m11 + m21 * m21
            if (lengthSquared != 1.0f && lengthSquared != 0.0f) {
                lengthSquared = 1.0f / sqrt(lengthSquared)
                m01 *= lengthSquared
                m11 *= lengthSquared
                m21 *= lengthSquared
            }
            lengthSquared = m02 * m02 + m12 * m12 + m22 * m22
            if (lengthSquared != 1.0f && lengthSquared != 0.0f) {
                lengthSquared = 1.0f / sqrt(lengthSquared)
                m02 *= lengthSquared
                m12 *= lengthSquared
                m22 *= lengthSquared
            }

            val t = m00 + m11 + m22

            val x: Float
            val y: Float
            val z: Float
            val w: Float
            if (t >= 0.0f) {
                var s = sqrt(t + 1.0f)
                w = 0.5f * s
                s = 0.5f / s
                x = (m21 - m12) * s
                y = (m02 - m20) * s
                z = (m10 - m01) * s
            } else if (m00 > m11 && m00 > m22) {
                var s = sqrt(1.0f + m00 - m11 - m22)
                x = s * 0.5f
                s = 0.5f / s
                y = (m10 + m01) * s
                z = (m02 + m20) * s
                w = (m21 - m12) * s
            } else if (m11 > m22) {
                var s = sqrt(1.0f + m11 - m00 - m22)
                y = s * 0.5f
                s = 0.5f / s
                x = (m10 + m01) * s
                z = (m21 + m12) * s
                w = (m02 - m20) * s
            } else {
                var s = sqrt(1.0f + m22 - m00 - m11)
                z = s * 0.5f
                s = 0.5f / s
                x = (m02 + m20) * s
                y = (m21 + m12) * s
                w = (m10 - m01) * s
            }
            return Quaternion(vec(x, y, z, w))
        }

        internal fun fromOrthogonalAxes(xAxis: Vec3f, yAxis: Vec3f, zAxis: Vec3f): Quaternion {
            return fromRotationMatrix(
                    xAxis.x, yAxis.x, zAxis.x,
                    xAxis.y, yAxis.y, zAxis.y,
                    xAxis.z, yAxis.z, zAxis.z
            )
        }
    }

    val isIdentity: Boolean
        get() = vector == Vec4f.UNIT_W

    override fun hashCode(): Int = vector.hashCode()

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is Quaternion -> false
            else -> vector == other.vector
        }
    }

    fun dot(other: Quaternion): Float = vector.dot(other.vector)

    fun angularDistanceTo(other: Quaternion): Float {
        val dot = dot(other)
        return acos(2.0f * dot * dot - 1.0f)
    }

    /**
     * Computes the Slerp interpolation between this and the specified other quaternion.
     */
    fun interpolate(alpha: Float, other: Quaternion): Quaternion {
        if (this == other) {
            return this
        } else {
            var dotResult = dot(other)
            var otherVector = other.vector
            if (dotResult < 0.0f) {
                otherVector = -otherVector
                dotResult = -dotResult
            }
            var scale0 = 1.0f - alpha
            var scale1 = alpha
            if (1.0f - dotResult > 0.1f) {
                val theta = acos(dotResult)
                val invSinTheta = 1.0f / sin(theta)
                scale0 = sin((1.0f - alpha) * theta) * invSinTheta
                scale1 = sin(alpha * theta) * invSinTheta
            }
            return Quaternion(vector * scale0 + otherVector * scale1)
        }
    }

    fun toVec4f(): Vec4f = vector

    fun toAxisAngle(): AxisAngle {
        val w = vector.w
        val angle = (2.0f * acos(w)).radians
        return AxisAngle(vector.xyz / sqrt(1.0f - w * w), angle)
    }

    operator fun times(other: Quaternion): Quaternion {
        val a = vector
        val b = other.vector
        return Quaternion(vec(
                a.x * b.w - a.y * b.z + a.z * b.y + a.w * b.x,
                a.x * b.z + a.y * b.w - a.z * b.x + a.w * b.y,
                -a.x * b.y + a.y * b.x + a.z * b.w + a.w * b.z,
                -a.x * b.x - a.y * b.y - a.z * b.z + a.w * b.w
        ))
    }

    operator fun times(v: Vec3f): Vec3f {
        // From https://gamedev.stackexchange.com/questions/28395/rotating-vector3-by-a-quaternion
        // Vector3 u(q.x, q.y, q.z);
        //
        // // Extract the scalar part of the quaternion
        // float s = q.w;
        //
        // // Do the math
        // vprime = 2.0f * dot(u, v) * u
        //        + (s*s - dot(u, u)) * v
        //        + 2.0f * s * cross(u, v);

        val u = vector.xyz
        val s = vector.w
        return 2.0f * (u dot v) * u + (s * s - (u dot u)) * v + 2.0f * s * (u cross v)
    }

    override fun toString(): String = "Quaternion(${vector.x}, ${vector.y}, ${vector.z}, ${vector.w})"
}

fun DataOutput.writeQuaternion(quaternion: Quaternion) = writeVec4f(quaternion.toVec4f())

fun DataInput.readQuaternion(): Quaternion = Quaternion.create(readVec4f())
