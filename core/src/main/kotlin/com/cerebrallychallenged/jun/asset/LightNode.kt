package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.light.LightComponentLike
import com.cerebrallychallenged.jun.unreal.light.ULightComponent

abstract class LightNode<out T : ULightComponent> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : LightBaseNode<T>(context, component), LightComponentLike
