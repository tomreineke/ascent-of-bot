package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.SkeletalMeshComponentLike
import com.cerebrallychallenged.jun.unreal.mesh.USkeletalMeshComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class SkeletalMeshNode<out T : USkeletalMeshComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : SkinnedMeshNode<T>(context, component), SkeletalMeshComponentLike

suspend fun CompositeNodeContainer.skeletalMeshComponent(
    socketName: String? = null,
    block: suspend SkeletalMeshNode<USkeletalMeshComponent>.() -> Unit
): SkeletalMeshNode<USkeletalMeshComponent> {
    val component = newObject<USkeletalMeshComponent>()
    return object : SkeletalMeshNode<USkeletalMeshComponent>(
        context,
        component
    ), SkeletalMeshComponentLike by component {}.also {
        it.block()
        attachChild(it, socketName)
    }
}

