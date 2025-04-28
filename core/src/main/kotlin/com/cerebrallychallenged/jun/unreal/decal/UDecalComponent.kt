package com.cerebrallychallenged.jun.unreal.decal

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class UDecalComponent(ptr: CPointer) : USceneComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var decalMaterial: UMaterialInterface
        get() = getDecalMaterial(ptr).wrapUObject()
        set(value) {
            setDecalMaterial(ptr, value.ptr)
        }
}

private external fun getDecalMaterial(ptr: CPointer): CPointer

private external fun setDecalMaterial(ptr: CPointer, materialPtr: CPointer)