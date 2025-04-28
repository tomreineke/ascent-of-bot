package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.SkinnedMeshComponentLike
import com.cerebrallychallenged.jun.unreal.mesh.USkinnedMeshComponent

abstract class SkinnedMeshNode<out T : USkinnedMeshComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : MeshNode<T>(context, component), SkinnedMeshComponentLike
