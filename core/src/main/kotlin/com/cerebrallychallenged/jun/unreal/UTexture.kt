package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class UTexture(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun updateResource() {
        updateResource(ptr)
    }
}

private external fun updateResource(ptr: CPointer)