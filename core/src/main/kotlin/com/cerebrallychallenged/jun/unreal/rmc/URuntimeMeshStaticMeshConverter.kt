package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.unreal.UBlueprintFunctionLibrary
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class URuntimeMeshStaticMeshConverter(ptr: CPointer) : UBlueprintFunctionLibrary(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}