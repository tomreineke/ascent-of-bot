package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.light.SpotLightComponentLike
import com.cerebrallychallenged.jun.unreal.light.USpotLightComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class SpotLightNode<out T : USpotLightComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : PointLightNode<T>(context, component), SpotLightComponentLike

suspend fun CompositeNodeContainer.spotLightComponent(
    socketName: String? = null,
    block: suspend SpotLightNode<USpotLightComponent>.() -> Unit
): SpotLightNode<USpotLightComponent> {
    val component = newObject<USpotLightComponent>()
    return object : SpotLightNode<USpotLightComponent>(
            context,
            component
    ), SpotLightComponentLike by component {}.also {
        it.block()
        it.component.updateColorAndBrightness()
        attachChild(it, socketName)
    }
}
