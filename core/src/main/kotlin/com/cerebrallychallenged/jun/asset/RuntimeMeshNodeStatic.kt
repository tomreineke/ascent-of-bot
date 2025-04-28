package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.rmc.RuntimeMeshComponentStaticLike
import com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentStatic

abstract class RuntimeMeshNodeStatic<out T : URuntimeMeshComponentStatic> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : RuntimeMeshNode<T>(context, component), RuntimeMeshComponentStaticLike

suspend fun CompositeNodeContainer.runtimeMeshComponentStatic(
        block: suspend RuntimeMeshNodeStatic<URuntimeMeshComponentStatic>.() -> Unit
): RuntimeMeshNodeStatic<URuntimeMeshComponentStatic> {
    val component = newObject<URuntimeMeshComponentStatic>()
    return object : RuntimeMeshNodeStatic<URuntimeMeshComponentStatic>(
            context,
            component
    ), RuntimeMeshComponentStaticLike by component {}.also {
        it.block()
        attachChild(it)
    }
}

