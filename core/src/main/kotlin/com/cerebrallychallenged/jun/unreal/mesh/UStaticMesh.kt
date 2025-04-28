package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.material.MaterialBearer
import com.cerebrallychallenged.jun.unreal.material.Materials
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UStaticMesh(ptr: CPointer) : UObject(ptr), MaterialBearer {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    override fun getMaterial(index: Int): UMaterialInterface?
            = getMaterial(ptr, index).wrapNullableUObject()

    val materials: Materials
        get() = Materials(this)

    override val numMaterials: Int
        get() = getNumMaterials(ptr)
}

private external fun getMaterial(ptr: CPointer, index: Int): CPointer

private external fun getNumMaterials(ptr: CPointer): Int