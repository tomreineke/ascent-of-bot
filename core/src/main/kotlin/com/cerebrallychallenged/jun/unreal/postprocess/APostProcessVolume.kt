package com.cerebrallychallenged.jun.unreal.postprocess

import com.cerebrallychallenged.jun.unreal.AVolume
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.util.CPointer

open class APostProcessVolume(ptr: CPointer) : AVolume(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun addOrUpdateBlendable(blendableObject: UMaterialInterface, weight: Float) {
        addOrUpdateBlendable(ptr, blendableObject.ptr, weight)
    }

    var unbound: Boolean
        get() = getUnbound(ptr)
        set(value) {
            setUnbound(ptr, value)
        }
}

private external fun addOrUpdateBlendable(ptr: CPointer, blendableObjectPtr: CPointer, weight: Float)

private external fun getUnbound(ptr: CPointer): Boolean

private external fun setUnbound(ptr: CPointer, unbound: Boolean)