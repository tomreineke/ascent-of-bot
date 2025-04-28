package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class USplineMeshComponent(ptr: CPointer) : UStaticMeshComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun getAxisMask(axis: ESplineMeshAxis): Vec3f = when (axis) {
            ESplineMeshAxis.X -> vec(0.0f, 1.0f, 1.0f)
            ESplineMeshAxis.Y -> vec(1.0f, 0.0f, 1.0f)
            ESplineMeshAxis.Z -> vec(1.0f, 1.0f, 0.0f)
        }

        fun getAxisValue(vector: Vec3f, axis: ESplineMeshAxis): Float = vector[axis.ordinal]
    }

    fun calcSliceTransform(distanceAlong: Float): Transform3f = calcSliceTransform(ptr, distanceAlong)

    fun calcSliceTransformAtSplineOffset(alpha: Float): Transform3f = calcSliceTransformAtSplineOffset(ptr, alpha)

    fun calculateScaleZAndMinZ(): Vec2f = calculateScaleZAndMinZ(ptr)

    fun destroyBodySetup() {
        destroyBodySetup(ptr)
    }

    var boundaryMax: Float
        get() = getBoundaryMax(ptr)
        set(value) {
            setBoundaryMax(ptr, value, false)
        }

    var boundaryMin: Float
        get() = getBoundaryMin(ptr)
        set(value) {
            setBoundaryMin(ptr, value, false)
        }

    var endOffset: Vec2f
        get() = getEndOffset(ptr)
        set(value) {
            setEndOffset(ptr, value, false)
        }

    var endPosition: Vec3f
        get() = getEndPosition(ptr)
        set(value) {
            setEndPosition(ptr, value, false)
        }

    var endRoll: Float
        get() = getEndRoll(ptr)
        set(value) {
            setEndRoll(ptr, value, false)
        }

    var endScale: Vec2f
        get() = getEndScale(ptr)
        set(value) {
            setEndScale(ptr, value, false)
        }

    var endTangent: Vec3f
        get() = getEndTangent(ptr)
        set(value) {
            setEndTangent(ptr, value, false)
        }

    var forwardAxis: ESplineMeshAxis
        get() = ESplineMeshAxis.values()[getForwardAxis(ptr)]
        set(value) {
            setForwardAxis(ptr, value.ordinal, false)
        }

    var splineUpDir: Vec3f
        get() = getSplineUpDir(ptr)
        set(value) {
            setSplineUpDir(ptr, value, false)
        }

    var startOffset: Vec2f
        get() = getStartOffset(ptr)
        set(value) {
            setStartOffset(ptr, value, false)
        }

    var startPosition: Vec3f
        get() = getStartPosition(ptr)
        set(value) {
            setStartPosition(ptr, value, false)
        }

    var startRoll: Float
        get() = getStartRoll(ptr)
        set(value) {
            setStartRoll(ptr, value, false)
        }

    var startScale: Vec2f
        get() = getStartScale(ptr)
        set(value) {
            setStartScale(ptr, value, false)
        }

    var startTangent: Vec3f
        get() = getStartTangent(ptr)
        set(value) {
            setStartTangent(ptr, value, false)
        }

    fun recreateCollision() {
        recreateCollision(ptr)
    }

    fun setStartAndEnd(
            startPos: Vec3f,
            startTangent: Vec3f,
            endPos: Vec3f,
            endTangent: Vec3f,
            updateMesh: Boolean = false
    ) {
        setStartAndEnd(ptr, startPos, startTangent, endPos, endTangent, updateMesh)
    }

    fun updateMesh() {
        updateMesh(ptr)
    }

    fun updateMeshConcurrent() {
        updateMeshConcurrent(ptr)
    }

    fun updateRenderStateAndCollision() {
        updateRenderStateAndCollision(ptr)
    }
}

private external fun calcSliceTransform(ptr: CPointer, distanceAlong: Float): Transform3f

private external fun calcSliceTransformAtSplineOffset(ptr: CPointer, alpha: Float): Transform3f

private external fun calculateScaleZAndMinZ(ptr: CPointer): Vec2f

private external fun destroyBodySetup(ptr: CPointer)

private external fun getBoundaryMax(ptr: CPointer): Float

private external fun getBoundaryMin(ptr: CPointer): Float

private external fun getEndOffset(ptr: CPointer): Vec2f

private external fun getEndPosition(ptr: CPointer): Vec3f

private external fun getEndRoll(ptr: CPointer): Float

private external fun getEndScale(ptr: CPointer): Vec2f

private external fun getEndTangent(ptr: CPointer): Vec3f

private external fun getForwardAxis(ptr: CPointer): Int

private external fun getSplineUpDir(ptr: CPointer): Vec3f

private external fun getStartOffset(ptr: CPointer): Vec2f

private external fun getStartPosition(ptr: CPointer): Vec3f

private external fun getStartRoll(ptr: CPointer): Float

private external fun getStartScale(ptr: CPointer): Vec2f

private external fun getStartTangent(ptr: CPointer): Vec3f

private external fun recreateCollision(ptr: CPointer)

private external fun setBoundaryMax(ptr: CPointer, value: Float, updateMesh: Boolean)

private external fun setBoundaryMin(ptr: CPointer, value: Float, updateMesh: Boolean)

private external fun setEndOffset(ptr: CPointer, value: Vec2f, updateMesh: Boolean)

private external fun setEndPosition(ptr: CPointer, value: Vec3f, updateMesh: Boolean)

private external fun setEndRoll(ptr: CPointer, value: Float, updateMesh: Boolean)

private external fun setEndScale(ptr: CPointer, value: Vec2f, updateMesh: Boolean)

private external fun setEndTangent(ptr: CPointer, value: Vec3f, updateMesh: Boolean)

private external fun setForwardAxis(ptr: CPointer, value: Int, updateMesh: Boolean)

private external fun setSplineUpDir(ptr: CPointer, value: Vec3f, updateMesh: Boolean)

private external fun setStartAndEnd(
        ptr: CPointer,
        startPos: Vec3f,
        startTangent: Vec3f,
        endPos: Vec3f,
        endTangent: Vec3f,
        updateMesh: Boolean
)

private external fun setStartOffset(ptr: CPointer, value: Vec2f, updateMesh: Boolean)

private external fun setStartPosition(ptr: CPointer, value: Vec3f, updateMesh: Boolean)

private external fun setStartRoll(ptr: CPointer, value: Float, updateMesh: Boolean)

private external fun setStartScale(ptr: CPointer, value: Vec2f, updateMesh: Boolean)

private external fun setStartTangent(ptr: CPointer, value: Vec3f, updateMesh: Boolean)

private external fun updateMesh(ptr: CPointer)

private external fun updateMeshConcurrent(ptr: CPointer)

private external fun updateRenderStateAndCollision(ptr: CPointer)