package com.cerebrallychallenged.jun.unreal.material

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.UTexture
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

@JvmInline
value class MaterialInstanceParameters(val materialInstance: UMaterialInstanceDynamic) {
    operator fun set(name: String, value: Float) {
        materialInstance.setScalarParameterValue(name, value)
    }

    operator fun set(name: String, value: UTexture) {
        materialInstance.setTextureParameterValue(name, value)
    }

    operator fun set(name: String, value: FLinearColor) {
        materialInstance.setVectorParameterValue(name, value)
    }
}

class UMaterialInstanceDynamic(ptr: CPointer) : UMaterialInstance(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun create(
                parentMaterial: UMaterialInterface,
                outer: UObject = JunManager.defaultActor
        ): UMaterialInstanceDynamic = create(parentMaterial.ptr, outer.ptr).wrapUObject()
    }

    @Convenience
    val parameters: MaterialInstanceParameters = MaterialInstanceParameters(this)

    fun setScalarParameterValue(name: String, value: Float) {
        setScalarParameterValue(ptr, name, value)
    }

    fun setTextureParameterValue(name: String, value: UTexture) {
        setTextureParameterValue(ptr, name, value.ptr)
    }

    fun setVectorParameterValue(name: String, value: FLinearColor) {
        setVectorParameterValue(ptr, name, value.rgba)
    }
}

private external fun create(parentMaterialPtr: CPointer, outerPtr: CPointer): CPointer

private external fun setScalarParameterValue(ptr: CPointer, name: String, value: Float)

private external fun setTextureParameterValue(ptr: CPointer, name: String, valuePtr: CPointer)

private external fun setVectorParameterValue(ptr: CPointer, name: String, value: Vec4f)
