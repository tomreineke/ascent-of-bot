package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.util.CPointer

open class USkeletalBodySetup(ptr: CPointer) : UBodySetup(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}