package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class ABrush(ptr: CPointer) : AActor(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}