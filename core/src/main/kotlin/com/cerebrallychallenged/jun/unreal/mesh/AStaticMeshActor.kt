package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class AStaticMeshActor(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val staticMeshComponent: UStaticMeshComponent
        get() = getStaticMeshComponent(ptr).wrapUObject()
}

private external fun getStaticMeshComponent(ptr: CPointer): CPointer