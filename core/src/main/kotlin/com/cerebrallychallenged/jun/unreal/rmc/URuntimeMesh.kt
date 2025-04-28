package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.unreal.UBodySetup
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.math.FBoxSphereBounds
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProvider
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class URuntimeMesh(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val bodySetup: UBodySetup?
        get() = getBodySetup(ptr).wrapNullableUObject()

    fun getMaterial(slotIndex: Int): UMaterialInterface? = getMaterial(ptr, slotIndex).wrapNullableUObject()

    fun getMaterialIndex(materialSlotName: String): Int = getMaterialIndex(ptr, materialSlotName)

    fun initialize(provider: URuntimeMeshProvider) {
        initialize(ptr, provider.ptr)
    }

    fun isMaterialSlotNameValid(materialSlotName: String): Boolean = isMaterialSlotNameValid(ptr, materialSlotName)

    val localBounds: FBoxSphereBounds
        get() = getLocalBounds(ptr)

    val numMaterials: Int
        get() = getNumMaterials(ptr)

    fun reset() {
        reset(ptr)
    }

    fun setupMaterialSlot(materialSlot: Int, slotName: String, material: UMaterialInterface) {
        setupMaterialSlot(ptr, materialSlot, slotName, material.ptr)
    }
}

private external fun getBodySetup(ptr: CPointer): CPointer

private external fun getLocalBounds(ptr: CPointer): FBoxSphereBounds

private external fun getMaterial(ptr: CPointer, slotIndex: Int): CPointer

private external fun getMaterialIndex(ptr: CPointer, materialSlotName: String): Int

private external fun getNumMaterials(ptr: CPointer): Int

private external fun initialize(ptr: CPointer, providerPtr: CPointer)

private external fun isMaterialSlotNameValid(ptr: CPointer, materialSlotName: String): Boolean

private external fun reset(ptr: CPointer)

private external fun setupMaterialSlot(ptr: CPointer, materialSlot: Int, slotName: String, materialPtr: CPointer)
