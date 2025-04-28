package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UDirectionalLightComponent(ptr: CPointer) : ULightComponent(ptr), DirectionalLightComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}