package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.light.DirectionalLightComponentLike
import com.cerebrallychallenged.jun.unreal.light.UDirectionalLightComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class DirectionalLightNode<out T : UDirectionalLightComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : LightNode<T>(context, component), DirectionalLightComponentLike

suspend fun CompositeNodeContainer.directionalLightComponent(
    socketName: String? = null,
    block: suspend DirectionalLightNode<UDirectionalLightComponent>.() -> Unit
): DirectionalLightNode<UDirectionalLightComponent> {
    val component = newObject<UDirectionalLightComponent>()
    return object : DirectionalLightNode<UDirectionalLightComponent>(
            context,
            component
    ), DirectionalLightComponentLike by component {}.also {
        it.block()
        it.component.updateColorAndBrightness()
        attachChild(it, socketName)
    }
}
