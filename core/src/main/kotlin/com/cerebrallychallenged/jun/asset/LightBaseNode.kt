package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.light.LightComponentBaseLike
import com.cerebrallychallenged.jun.unreal.light.ULightComponentBase

abstract class LightBaseNode<out T : ULightComponentBase> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : SceneNode<T>(context, component), LightComponentBaseLike
