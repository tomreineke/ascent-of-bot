package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class UPointLightComponent(ptr: CPointer) : ULocalLightComponent(ptr), PointLightComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }
}