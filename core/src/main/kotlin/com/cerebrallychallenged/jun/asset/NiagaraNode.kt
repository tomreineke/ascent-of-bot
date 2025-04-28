package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.niagara.NiagaraComponentLike
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraComponent

abstract class NiagaraNode<out T : UNiagaraComponent> protected constructor(
    context: CompositeAssetDefinitionContext,
    component: T
) : PrimitiveNode<T>(context, component), NiagaraComponentLike

suspend fun CompositeNodeContainer.niagaraComponent(
    socketName: String? = null,
    block: suspend NiagaraNode<UNiagaraComponent>.() -> Unit
): NiagaraNode<UNiagaraComponent> {
    val component = newObject<UNiagaraComponent>()
    return object : NiagaraNode<UNiagaraComponent>(
        context,
        component
    ), NiagaraComponentLike by component {}.also {
        it.block()
        attachChild(it, socketName)
    }
}
