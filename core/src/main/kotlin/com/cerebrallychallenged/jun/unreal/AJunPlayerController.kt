package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class AJunPlayerController(ptr: CPointer) : APlayerController(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}