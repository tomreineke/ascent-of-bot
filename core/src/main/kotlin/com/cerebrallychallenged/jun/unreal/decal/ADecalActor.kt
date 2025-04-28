package com.cerebrallychallenged.jun.unreal.decal

import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class ADecalActor(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val decal: UDecalComponent
        get() = getDecal(ptr).wrapUObject()
}

private external fun getDecal(ptr: CPointer): CPointer