package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.light.LocalLightComponentLike
import com.cerebrallychallenged.jun.unreal.light.ULocalLightComponent

abstract class LocalLightNode<out T : ULocalLightComponent> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : LightNode<T>(context, component), LocalLightComponentLike
