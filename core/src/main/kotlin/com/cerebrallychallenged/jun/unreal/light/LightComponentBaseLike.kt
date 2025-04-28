package com.cerebrallychallenged.jun.unreal.light

import com.cerebrallychallenged.jun.unreal.SceneComponentLike

interface LightComponentBaseLike : SceneComponentLike {
    var castShadows: Boolean

    val intensity: Float
}