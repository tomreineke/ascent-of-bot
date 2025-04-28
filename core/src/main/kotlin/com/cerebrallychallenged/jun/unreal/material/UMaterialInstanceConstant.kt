package com.cerebrallychallenged.jun.unreal.material

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UMaterialInstanceConstant(ptr: CPointer) : UMaterialInstance(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}