package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.UTextureLightProfile
import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

interface LightComponentLike : LightComponentBaseLike {
    var affectTranslucentLighting: Boolean

    var bloomScale: Float

    var bloomThreshold: Float

    var bloomTint: FColor

    var enableLightShaftBloom: Boolean

    var forceCachedShadowsForMovablePrimitives: Boolean

    var iesBrightnessScale: Float

    var iesTexture: UTextureLightProfile?

    var indirectLightingIntensity: Float

    override var intensity: Float

    var lightColor: FLinearColor
}