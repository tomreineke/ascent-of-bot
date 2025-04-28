package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class ALight(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val lightComponent: ULightComponent
        get() = getLightComponent(ptr).wrapUObject()
}

private external fun getLightComponent(ptr: CPointer): CPointer