package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.UTextureLightProfile
import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.nullablePtr
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject

open class ULightComponent(ptr: CPointer) : ULightComponentBase(ptr), LightComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    final override var affectTranslucentLighting: Boolean
        get() = getAffectTranslucentLighting(ptr)
        set(value) {
            setAffectTranslucentLighting(ptr, value)
        }

    final override var bloomScale: Float
        get() = getBloomScale(ptr)
        set(value) {
            setBloomScale(ptr, value)
        }

    final override var bloomThreshold: Float
        get() = getBloomThreshold(ptr)
        set(value) {
            setBloomThreshold(ptr, value)
        }

    final override var bloomTint: FColor
        get() = FColor.fromPackedARGB(getBloomTint(ptr))
        set(value) {
            setBloomTint(ptr, value.packedARGB)
        }

    final override var enableLightShaftBloom: Boolean
        get() = getEnableLightShaftBloom(ptr)
        set(value) {
            setEnableLightShaftBloom(ptr, value)
        }

    final override var forceCachedShadowsForMovablePrimitives: Boolean
        get() = getForceCachedShadowsForMovablePrimitives(ptr)
        set(value) {
            setForceCachedShadowsForMovablePrimitives(ptr, value)
        }

    final override var iesBrightnessScale: Float
        get() = getIESBrightnessScale(ptr)
        set(value) {
            setIESBrightnessScale(ptr, value)
        }

    final override var iesTexture: UTextureLightProfile?
        get() = getIESTexture(ptr).wrapNullableUObject()
        set(value) {
            setIESTexture(ptr, value.nullablePtr)
        }

    final override var indirectLightingIntensity: Float
        get() = getIndirectLightingIntensity(ptr)
        set(value) {
            setIndirectLightingIntensity(ptr, value)
        }

    final override var intensity: Float
        get() = super.intensity
        set(value) {
            setIntensity(ptr, value)
        }

    final override var lightColor: FLinearColor
        get() = FLinearColor.rgba(getLightColor(ptr))
        set(value) {
            setLightColor(ptr, value.rgba)
        }

    fun updateColorAndBrightness() {
        updateColorAndBrightness(ptr)
    }
}

private external fun getAffectTranslucentLighting(ptr: CPointer): Boolean

private external fun getBloomScale(ptr: CPointer): Float

private external fun getBloomThreshold(ptr: CPointer): Float

private external fun getBloomTint(ptr: CPointer): Int

private external fun getEnableLightShaftBloom(ptr: CPointer): Boolean

private external fun getForceCachedShadowsForMovablePrimitives(ptr: CPointer): Boolean

private external fun getIESBrightnessScale(ptr: CPointer): Float

private external fun getIESTexture(ptr: CPointer): CPointer

private external fun getIndirectLightingIntensity(ptr: CPointer): Float

private external fun getLightColor(ptr: CPointer): Vec4f

private external fun setAffectTranslucentLighting(ptr: CPointer, newValue: Boolean)

private external fun setBloomScale(ptr: CPointer, newValue: Float)

private external fun setBloomThreshold(ptr: CPointer, newValue: Float)

private external fun setBloomTint(ptr: CPointer, newValue: Int)

private external fun setEnableLightShaftBloom(ptr: CPointer, newValue: Boolean)

private external fun setForceCachedShadowsForMovablePrimitives(ptr: CPointer, newValue: Boolean)

private external fun setIESBrightnessScale(ptr: CPointer, newValue: Float)

private external fun setIESTexture(ptr: CPointer, newValuePtr: CPointer)

private external fun setIndirectLightingIntensity(ptr: CPointer, newValue: Float)

private external fun setIntensity(ptr: CPointer, newValue: Float)

private external fun setLightColor(ptr: CPointer, newValue: Vec4f)

private external fun updateColorAndBrightness(ptr: CPointer)