package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.mesh.MeshComponentLike
import com.cerebrallychallenged.jun.unreal.mesh.UMeshComponent

abstract class MeshNode<out T : UMeshComponent> protected constructor(
        context: CompositeAssetDefinitionContext,
        component: T
) : PrimitiveNode<T>(context, component), MeshComponentLike
