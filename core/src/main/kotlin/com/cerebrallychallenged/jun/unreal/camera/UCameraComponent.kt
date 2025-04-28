package com.cerebrallychallenged.jun.unreal.camera

import com.cerebrallychallenged.jun.unreal.*
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

open class UCameraComponent(ptr: CPointer) : USceneComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var aspectRatio: Float
        get() = getAspectRatio(ptr)
        set(value) {
            setAspectRatio(ptr, value)
        }

    var constraintAspectRatio: Boolean
        get() = getConstraintAspectRatio(ptr)
        set(value) {
            setConstraintAspectRatio(ptr, value)
        }

    fun getCameraView(deltaTime: Double): TSharedRef<FMinimalViewInfo>
            = getCameraView(ptr, deltaTime.toFloat()).wrapSharedRef()

    var orthoWidth: Double
        get() = getOrthoWidth(ptr).toDouble()
        set(value) {
            setOrthoWidth(ptr, value.toFloat())
        }

    var projectionMode: ECameraProjectionMode
        get() = ECameraProjectionMode.values()[getProjectionMode(ptr)]
        set(value) {
            setProjectionMode(ptr, value.ordinal)
        }
}

private external fun getAspectRatio(ptr: CPointer): Float

private external fun getCameraView(ptr: CPointer, deltaTime: Float): CPointer

private external fun getConstraintAspectRatio(ptr: CPointer): Boolean

private external fun getOrthoWidth(ptr: CPointer): Float

private external fun getProjectionMode(ptr: CPointer): Int

private external fun setAspectRatio(ptr: CPointer, aspectRatio: Float)

private external fun setConstraintAspectRatio(ptr: CPointer, constraintAspectRatio: Boolean)

private external fun setOrthoWidth(ptr: CPointer, orthoWidth: Float)

private external fun setProjectionMode(ptr: CPointer, projectionMode: Int)
