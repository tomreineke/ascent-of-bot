package com.cerebrallychallenged.jun.unreal.sound

import com.cerebrallychallenged.jun.unreal.AVolume
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class AAudioVolume(ptr: CPointer) : AVolume(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}
