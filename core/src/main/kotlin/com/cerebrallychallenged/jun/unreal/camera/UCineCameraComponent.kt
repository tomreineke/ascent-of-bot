package com.cerebrallychallenged.jun.unreal.camera

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UCineCameraComponent(ptr: CPointer) : UCameraComponent(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}