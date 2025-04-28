package com.cerebrallychallenged.jun.unreal.blueprint

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UBlueprintCore(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}