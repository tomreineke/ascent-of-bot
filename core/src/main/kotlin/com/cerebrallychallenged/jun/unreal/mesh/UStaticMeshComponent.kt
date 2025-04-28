package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.nullablePtr
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class UStaticMeshComponent(ptr: CPointer) : UMeshComponent(ptr), StaticMeshComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    final override var staticMesh: UStaticMesh?
        get() = getStaticMesh(ptr).wrapNullableUObject()
        set(value) {
            setStaticMesh(ptr, value.nullablePtr)
        }
}

private external fun getStaticMesh(ptr: CPointer): CPointer

private external fun setStaticMesh(ptr: CPointer, meshPtr: CPointer)