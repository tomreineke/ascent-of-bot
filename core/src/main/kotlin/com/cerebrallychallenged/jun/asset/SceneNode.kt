package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.EComponentMobility
import com.cerebrallychallenged.jun.unreal.SceneComponentLike
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class SceneNode<out T : USceneComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    final override val component: T
) : CompositeNode(context) {
    init {
        component.mobility = EComponentMobility.Movable
        component.registerComponent()
    }
}

suspend fun CompositeNodeContainer.sceneComponent(
    socketName: String? = null,
    block: suspend SceneNode<USceneComponent>.() -> Unit
): SceneNode<USceneComponent> {
    val component = newObject<USceneComponent>()
    return object : SceneNode<USceneComponent>(
            context,
            component
    ), SceneComponentLike by component {}.also {
        it.block()
        attachChild(it, socketName)
    }
}
