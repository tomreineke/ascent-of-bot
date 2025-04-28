package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.unreal.PrimitiveComponentLike
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent

abstract class PrimitiveNode<out T : UPrimitiveComponent> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : SceneNode<T>(context, component), PrimitiveComponentLike {
    override fun addInputListener(listener: (InputEvent) -> Unit) {
        component.inputListeners.add(listener)
        super.addInputListener(listener)
    }
}
