package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.mesh.StaticMeshComponentLike
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMeshComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class StaticMeshNode<out T : UStaticMeshComponent> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : MeshNode<T>(context, component), StaticMeshComponentLike

suspend fun CompositeNodeContainer.staticMeshComponent(
        socketName: String? = null,
        block: suspend StaticMeshNode<UStaticMeshComponent>.() -> Unit
): StaticMeshNode<UStaticMeshComponent> {
    val component = newObject<UStaticMeshComponent>()
    return object : StaticMeshNode<UStaticMeshComponent>(
            context,
            component
    ), StaticMeshComponentLike by component {}.also {
        it.block()
        attachChild(it, socketName)
    }
}
