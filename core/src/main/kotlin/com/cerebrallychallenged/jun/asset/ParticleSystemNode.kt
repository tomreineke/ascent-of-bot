package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.ParticleSystemComponentLike
import com.cerebrallychallenged.jun.unreal.UParticleSystemComponent
import com.cerebrallychallenged.jun.unreal.newObject

abstract class ParticleSystemNode<out T : UParticleSystemComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : PrimitiveNode<T>(context, component), ParticleSystemComponentLike

suspend fun CompositeNodeContainer.particleSystemComponent(
    socketName: String? = null,
    block: suspend ParticleSystemNode<UParticleSystemComponent>.() -> Unit
): ParticleSystemNode<UParticleSystemComponent> {
    val component = newObject<UParticleSystemComponent>()
    return object : ParticleSystemNode<UParticleSystemComponent>(
            context,
            component
    ), ParticleSystemComponentLike by component {}.also {
        it.block()
        attachChild(it, socketName)
    }
}
