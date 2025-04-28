package com.cerebrallychallenged.jun.unreal.light

interface LocalLightComponentLike : LightComponentLike {
    var intensityUnits: ELightUnits

    var lightIntensity: LightIntensity
        get() = LightIntensity(intensity, intensityUnits)
        set(value) {
            intensity = value.intensity
            intensityUnits = value.units
        }
}