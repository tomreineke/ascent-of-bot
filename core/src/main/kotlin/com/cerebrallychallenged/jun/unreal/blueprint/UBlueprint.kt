package com.cerebrallychallenged.jun.unreal.blueprint

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class UBlueprint(ptr: CPointer) : UBlueprintCore(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    val generatedClass: UClass
        get() = getGeneratedClass(ptr).wrapUObject()
}

private external fun getGeneratedClass(ptr: CPointer): CPointer