package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class URuntimeMeshComponentStatic(ptr: CPointer) : URuntimeMeshComponent(ptr), RuntimeMeshComponentStaticLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}