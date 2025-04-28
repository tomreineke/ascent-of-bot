package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.Convenience

@Convenience
data class LightIntensity(val intensity: Float, val units: ELightUnits)

val Float.lumen: LightIntensity
    get() = LightIntensity(this, ELightUnits.Lumens)

val Number.lumen: LightIntensity
    get() = toFloat().lumen

val Float.candela: LightIntensity
    get() = LightIntensity(this, ELightUnits.Candelas)

val Number.candela: LightIntensity
    get() = toFloat().candela