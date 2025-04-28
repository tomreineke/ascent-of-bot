package com.cerebrallychallenged.jun.unreal.mesh

import com.cerebrallychallenged.jun.unreal.SkinnedMeshComponentLike
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class USkinnedMeshComponent(ptr: CPointer) : UMeshComponent(ptr), SkinnedMeshComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}