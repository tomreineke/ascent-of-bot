package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

class UTextureLightProfile(ptr: CPointer) : UTexture2D(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}