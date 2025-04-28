package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class AVolume(ptr: CPointer) : ABrush(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}