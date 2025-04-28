package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class ULocalPlayer(ptr: CPointer) : UPlayer(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}