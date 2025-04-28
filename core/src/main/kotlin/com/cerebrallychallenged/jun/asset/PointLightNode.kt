package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.light.PointLightComponentLike
import com.cerebrallychallenged.jun.unreal.light.UPointLightComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class PointLightNode<out T : UPointLightComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : LocalLightNode<T>(context, component), PointLightComponentLike

suspend fun CompositeNodeContainer.pointLightComponent(
    socketName: String? = null,
    block: suspend PointLightNode<UPointLightComponent>.() -> Unit
): PointLightNode<UPointLightComponent> {
    val component = newObject<UPointLightComponent>()
    return object : PointLightNode<UPointLightComponent>(
            context,
            component
    ), PointLightComponentLike by component {}.also {
        it.block()
        it.component.updateColorAndBrightness()
        attachChild(it, socketName)
    }
}
