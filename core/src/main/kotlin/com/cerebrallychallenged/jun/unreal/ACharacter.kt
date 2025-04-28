package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class ACharacter(ptr: CPointer) : APawn(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}