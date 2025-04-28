package com.cerebrallychallenged.jun.unreal.sound

import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class AAmbientSound(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}
