package com.cerebrallychallenged.jun.unreal.camera

import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class ACameraActor(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val cameraComponent: UCameraComponent
        get() = getCameraComponent(ptr).wrapUObject()
}

private external fun getCameraComponent(ptr: CPointer): CPointer