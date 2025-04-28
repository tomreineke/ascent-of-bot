package com.cerebrallychallenged.jun.unreal.sound

import com.cerebrallychallenged.hypogean.view.audio.AudioComponentLike
import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.nullablePtr
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject
import com.cerebrallychallenged.jun.wrapUObject

open class UAudioComponent(ptr: CPointer) : USceneComponent(ptr), AudioComponentLike {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        @Suppress("unused") // Called from JNI.
        @JvmStatic
        private fun onAudioFinished(ptr: CPointer) {
            for (listener in ptr.wrapUObject<UAudioComponent>().onAudioFinished) {
                listener()
            }
        }
    }

    @Convenience
    fun activateCallbacks() {
        activateCallbacks(ptr)
    }

    fun adjustAttenuation(attenuationSettings: TSharedRef<FSoundAttenuationSettings>) {
        adjustAttenuation(ptr, attenuationSettings.directPtr)
    }

    var allowSpatialization: Boolean
        get() = getAllowSpatialization(ptr)
        set(value) {
            setAllowSpatialization(ptr, value)
        }

    override fun fadeOut(fadeOutDuration: Float, fadeVolumeLevel: Float, fadeCurve: EAudioFaderCurve) {
        fadeOut(ptr, fadeOutDuration, fadeVolumeLevel, fadeCurve.ordinal.toByte())
    }

    var isPaused: Boolean
        get() = getPaused(ptr)
        set(value) {
            setPaused(ptr, value)
        }

    @Convenience
    var onAudioFinished: List<() -> Unit> = listOf()

    override var pitchMultiplier: Float
        get() = getPitchMultiplier(ptr)
        set(value) {
            setPitchMultiplier(ptr, value)
        }

    override fun play(startTime: Float) {
        play(ptr, startTime)
    }

    override var sound: USoundBase?
        get() = getSound(ptr).wrapNullableUObject()
        set(value) {
            setSound(ptr, value.nullablePtr)
        }

    override fun stop() {
        stop(ptr)
    }

    override var volumeMultiplier: Float
        get() = getVolumeMultiplier(ptr)
        set(value) {
            setVolumeMultiplier(ptr, value)
        }
}

private external fun activateCallbacks(ptr: CPointer)

private external fun adjustAttenuation(ptr: CPointer, settingsPtr: CPointer)

private external fun fadeOut(ptr: CPointer, fadeOutDuration: Float, fadeVolumeLevel: Float, fadeCurveMagic: Byte)

private external fun getAllowSpatialization(ptr: CPointer): Boolean

private external fun getPaused(ptr: CPointer): Boolean

private external fun getPitchMultiplier(ptr: CPointer): Float

private external fun getSound(ptr: CPointer): CPointer

private external fun getVolumeMultiplier(ptr: CPointer): Float

private external fun play(ptr: CPointer, startTime: Float)

private external fun setAllowSpatialization(ptr: CPointer, paused: Boolean)

private external fun setPaused(ptr: CPointer, paused: Boolean)

private external fun setPitchMultiplier(ptr: CPointer, newPitchMultiplier: Float)

private external fun setSound(ptr: CPointer, soundPtr: CPointer)

private external fun setVolumeMultiplier(ptr: CPointer, newVolumeMultiplier: Float)

private external fun stop(ptr: CPointer)
