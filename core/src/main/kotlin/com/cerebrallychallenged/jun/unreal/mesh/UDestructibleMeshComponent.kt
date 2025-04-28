package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UDestructibleMeshComponent(ptr: CPointer) : USkinnedMeshComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}