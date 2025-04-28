package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

class UWorld(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}