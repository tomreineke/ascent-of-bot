package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.rmc.RuntimeMeshComponentLike
import com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponent

abstract class RuntimeMeshNode<out T : URuntimeMeshComponent> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : MeshNode<T>(context, component), RuntimeMeshComponentLike

suspend fun CompositeNodeContainer.runtimeMeshComponent(
        block: suspend RuntimeMeshNode<URuntimeMeshComponent>.() -> Unit
): RuntimeMeshNode<URuntimeMeshComponent> {
    val component = newObject<URuntimeMeshComponent>()
    return object : RuntimeMeshNode<URuntimeMeshComponent>(
            context,
            component
    ), RuntimeMeshComponentLike by component {}.also {
        it.block()
        attachChild(it)
    }
}


