package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class UTexture2DDynamic(ptr: CPointer) : UTexture2D(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}