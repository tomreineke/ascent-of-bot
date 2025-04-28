package com.cerebrallychallenged.jun.unreal.rmc.provider

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionProperties
import com.cerebrallychallenged.jun.util.CPointer

open class URuntimeMeshProvider(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun initialize() {
        initialize(ptr)
    }

    fun createSection(lodIndex: Int, sectionId: Int, sectionProperties: TSharedRef<FRuntimeMeshSectionProperties>) {
        createSection(ptr, lodIndex, sectionId, sectionProperties.sharedPtrPtr)
    }

    fun getMaterialIndex(materialSlotName: String): Int = getMaterialIndex(ptr, materialSlotName)

    fun markAllLODsDirty() {
        markAllLODsDirty(ptr)
    }

    fun markCollisionDirty() {
        markCollisionDirty(ptr)
    }

    fun markLODDirty(lodIndex: Int) {
        markLODDirty(ptr, lodIndex)
    }

    fun markSectionDirty(lodIndex: Int, sectionId: Int) {
        markSectionDirty(ptr, lodIndex, sectionId)
    }

    fun removeSection(lodIndex: Int, sectionId: Int) {
        removeSection(ptr, lodIndex, sectionId)
    }

    fun setSectionCastsShadow(lodIndex: Int, sectionId: Int, castsShadow: Boolean) {
        setSectionCastsShadow(ptr, lodIndex, sectionId, castsShadow)
    }

    fun setSectionVisibility(lodIndex: Int, sectionId: Int, isVisible: Boolean) {
        setSectionVisibility(ptr, lodIndex, sectionId, isVisible)
    }

    fun setupMaterialSlot(materialSlot: Int, slotName: String, material: UMaterialInterface) {
        setupMaterialSlot(ptr, materialSlot, slotName, material.ptr)
    }
}

// configureLODs

private external fun initialize(ptr: CPointer)

private external fun createSection(ptr: CPointer, lodIndex: Int, sectionId: Int, sectionPropertiesPtr: CPointer)

private external fun getMaterialIndex(ptr: CPointer, materialSlotName: String): Int

private external fun markAllLODsDirty(ptr: CPointer)

private external fun markCollisionDirty(ptr: CPointer)

private external fun markLODDirty(ptr: CPointer, lodIndex: Int)

private external fun markSectionDirty(ptr: CPointer, lodIndex: Int, sectionId: Int)

private external fun removeSection(ptr: CPointer, lodIndex: Int, sectionId: Int)

private external fun setSectionCastsShadow(ptr: CPointer, lodIndex: Int, sectionId: Int, castsShadow: Boolean)

private external fun setSectionVisibility(ptr: CPointer, lodIndex: Int, sectionId: Int, isVisible: Boolean)

private external fun setupMaterialSlot(ptr: CPointer, materialSlot: Int, slotName: String, materialPtr: CPointer)
