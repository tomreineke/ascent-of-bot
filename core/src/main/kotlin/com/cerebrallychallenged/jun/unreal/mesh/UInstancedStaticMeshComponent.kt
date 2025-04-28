package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UInstancedStaticMeshComponent(ptr: CPointer) : UStaticMeshComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}