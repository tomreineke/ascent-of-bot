package com.cerebrallychallenged.jun.unreal.camera

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class ACineCameraActor(ptr: CPointer) : ACameraActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}