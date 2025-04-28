package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.sound.UInitialActiveSoundParams
import com.cerebrallychallenged.jun.unreal.sound.USoundAttenuation
import com.cerebrallychallenged.jun.unreal.sound.USoundBase
import com.cerebrallychallenged.jun.unreal.sound.USoundConcurrency
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject

open class UGameplayStatics(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        fun getPlayerCharacter(worldContextObject: UObject = JunManager.defaultActor, playerIndex: Int): ACharacter
                = getPlayerCharacter(worldContextObject.ptr, playerIndex).wrapUObject()

        fun getPlayerController(
                worldContextObject: UObject = JunManager.defaultActor,
                playerIndex: Int
        ): APlayerController = getPlayerController(worldContextObject.ptr, playerIndex).wrapUObject()

        fun playSound2D(
            worldContextObject: UObject = JunManager.defaultActor,
            sound: USoundBase,
            volumeMultiplier: Float = 1.0f,
            pitchMultiplier: Float = 1.0f,
            startTime: Float = 0.0f,
            concurrencySettings: USoundConcurrency? = null,
            owningActor: AActor? = null,
            isUISound: Boolean
        ) {
            playSound2D(
                worldContextObject.ptr,
                sound.ptr,
                volumeMultiplier,
                pitchMultiplier,
                startTime,
                concurrencySettings.nullablePtr,
                owningActor.nullablePtr,
                isUISound
            )
        }

        fun playSoundAtLocation(
            worldContextObject: UObject = JunManager.defaultActor,
            sound: USoundBase,
            location: Vec3f,
            volumeMultiplier: Float = 1.0f,
            pitchMultiplier: Float = 1.0f,
            startTime: Float = 0.0f,
            attenuationSettings: USoundAttenuation? = null,
            concurrencySettings: USoundConcurrency? = null,
            initialParams: UInitialActiveSoundParams? = null
        ) {
            playSoundAtLocation(
                worldContextObject.ptr,
                sound.ptr,
                location,
                volumeMultiplier,
                pitchMultiplier,
                startTime,
                attenuationSettings.nullablePtr,
                concurrencySettings.nullablePtr,
                initialParams.nullablePtr
            )
        }

        fun spawnEmitterAtLocation(
            worldContextObject: UObject = JunManager.defaultActor,
            emitterTemplate: UParticleSystem,
            location: Vec3f,
            rotation: Quaternion = Quaternion.IDENTITY,
            scale: Vec3f = Vec3f.ONE,
            autoDestroy: Boolean = true,
            poolingMethod: EPSCPoolMethod = EPSCPoolMethod.AutoRelease
        ): UParticleSystemComponent = spawnEmitterAtLocation(
            worldContextObject.ptr,
            emitterTemplate.ptr,
            location,
            rotation,
            scale,
            autoDestroy,
            poolingMethod.ordinal.toByte()
        ).wrapUObject()
    }
}

private external fun getPlayerCharacter(worldContextObjectPtr: CPointer, playerIndex: Int): CPointer

private external fun getPlayerController(worldContextObjectPtr: CPointer, playerIndex: Int): CPointer

private external fun playSound2D(
    worldContextObjectPtr: CPointer,
    sound: CPointer,
    volumeMultiplier: Float,
    pitchMultiplier: Float,
    startTime: Float,
    concurrencySettingsPtr: CPointer,
    owningActor: CPointer,
    isUISound: Boolean
)

private external fun playSoundAtLocation(
    worldContextObjectPtr: CPointer,
    sound: CPointer,
    location: Vec3f,
    volumeMultiplier: Float,
    pitchMultiplier: Float,
    startTime: Float,
    attenuationSettingsPtr: CPointer,
    concurrencySettingsPtr: CPointer,
    initialParams: CPointer
)

private external fun spawnEmitterAtLocation(
        worldContextObjectPtr: CPointer,
        emitterTemplatePtr: CPointer,
        location: Vec3f,
        rotation: Quaternion,
        scale: Vec3f,
        autoDestroy: Boolean,
        poolingMethod: Byte
): CPointer
