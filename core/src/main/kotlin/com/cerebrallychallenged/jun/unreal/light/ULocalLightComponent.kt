package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.util.CPointer

open class ULocalLightComponent(ptr: CPointer) : ULightComponent(ptr), LocalLightComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    final override var intensityUnits: ELightUnits
        get() = ELightUnits.values()[getIntensityUnits(ptr).toInt()]
        set(value) {
            setIntensityUnits(ptr, value.ordinal.toByte())
        }
}

private external fun getIntensityUnits(ptr: CPointer): Byte

private external fun setIntensityUnits(ptr: CPointer, newValue: Byte)