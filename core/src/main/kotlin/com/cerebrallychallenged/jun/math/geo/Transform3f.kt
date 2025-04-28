package com.cerebrallychallenged.jun.math.geo

import java.io.DataInput
import java.io.DataOutput

data class Transform3f(val rotation: Quaternion, val translation: Vec3f, val scale: Vec3f) {
    companion object {
        @JvmField
        val IDENTITY = Transform3f(Quaternion.IDENTITY, Vec3f.ZERO, Vec3f.ONE)

        fun rotation(rotation: Quaternion): Transform3f = IDENTITY.withRotation(rotation)

        fun translation(translation: Vec3f): Transform3f = IDENTITY.withTranslation(translation)

        fun scale(scale: Vec3f): Transform3f = IDENTITY.withScale(scale)
    }

    fun withRotation(rotation: Quaternion): Transform3f = copy(rotation = rotation)

    fun withTranslation(translation: Vec3f): Transform3f = copy(translation = translation)

    fun withScale(scale: Vec3f): Transform3f = copy(scale = scale)
}

fun DataOutput.writeTransform3f(transform3f: Transform3f) {
    writeQuaternion(transform3f.rotation)
    writeVec3f(transform3f.translation)
    writeVec3f(transform3f.scale)
}

fun DataInput.readTransform3f(): Transform3f = Transform3f(
        readQuaternion(),
        readVec3f(),
        readVec3f()
)