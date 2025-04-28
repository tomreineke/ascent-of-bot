package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class AController(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}