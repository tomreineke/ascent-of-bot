package com.cerebrallychallenged.jun.unreal.rmc.provider

import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.material.UMaterial
import com.cerebrallychallenged.jun.unreal.nullablePtr
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class URuntimeMeshProviderBox(ptr: CPointer) : URuntimeMeshProvider(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    var boxRadius: Vec3f
        get() = getBoxRadius(ptr)
        set(value) {
            setBoxRadius(ptr, value)
        }

    var boxMaterial: UMaterial?
        get() = getBoxMaterial(ptr).wrapNullableUObject()
        set(value) {
            setBoxMaterial(ptr, value.nullablePtr)
        }
}

private external fun getBoxRadius(ptr: CPointer): Vec3f

private external fun getBoxMaterial(ptr: CPointer): CPointer

private external fun setBoxRadius(ptr: CPointer, newValue: Vec3f)

private external fun setBoxMaterial(ptr: CPointer, materialPtr: CPointer)