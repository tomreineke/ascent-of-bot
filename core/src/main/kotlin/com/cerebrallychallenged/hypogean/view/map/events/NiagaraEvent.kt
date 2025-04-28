package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules.Companion.KeepRelativeTransform
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraComponent
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraSystem

class NiagaraEvent(
    private val niagaraSystem: UnrealRef<UNiagaraSystem>,
    override val duration: Float,
    private val position: Position,
    private val relativeTransform: Transform3f = Transform3f.IDENTITY,
    private val skipIfSocketNotFound: Boolean = false
) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        val (positionParent, socketName) = when (position) {
            is Position.Absolute -> Pair(null, null)
            is Position.Node -> Pair(position.findNode()?.component, position.socketName)
        }
        val parent = positionParent ?: newObject<USceneComponent>().apply {
            val transform = position.computeTransform()
            when {
                transform != null -> relativeTransform = transform
                skipIfSocketNotFound -> return
                else -> modelError("")
            }
            registerComponent()
        }
        val component = newObject<UNiagaraComponent>().apply {
            asset = assetLibrary.load(niagaraSystem)
            relativeTransform = this@NiagaraEvent.relativeTransform
            attachToComponent(parent, KeepRelativeTransform, socketName)
            registerComponent()
        }
        addAnimation(object : Animation(duration) {
            override fun onEnd() {
                component.unregisterComponent()
                positionParent?.unregisterComponent()
            }
        })
    }
}
