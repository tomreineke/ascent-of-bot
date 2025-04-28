package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.util.CPointer

open class ULightComponentBase(ptr: CPointer) : USceneComponent(ptr), LightComponentBaseLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    final override var castShadows: Boolean
        get() = getCastShadows(ptr)
        set(value) {
            setCastShadows(ptr, value)
        }

    override val intensity: Float
        get() = getIntensity(ptr)
}

private external fun getCastShadows(ptr: CPointer): Boolean

private external fun setCastShadows(ptr: CPointer, newValue: Boolean)

private external fun getIntensity(ptr: CPointer): Float
