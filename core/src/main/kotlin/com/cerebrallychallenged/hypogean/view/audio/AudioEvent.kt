package com.cerebrallychallenged.hypogean.view.audio

import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.hypogean.view.map.events.MapViewEvent
import com.cerebrallychallenged.hypogean.view.map.events.Position
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.sound.EAudioFaderCurve
import com.cerebrallychallenged.jun.unreal.sound.UAudioComponent
import com.cerebrallychallenged.jun.unreal.sound.USoundBase

class AudioEvent(
    val asset: UnrealRef<USoundBase>,
    val position: Position,
    val startTime: Float = 0.0f,
    override val duration: Float = 0.0f
) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        val component = newObject<UAudioComponent>().apply {
            sound = assetLibrary.load(asset)
            val transform = when (position) {
                is Position.Absolute -> position.estimatedTransform
                is Position.Node -> {
                    val parent = position.findNode()?.component
                    if (parent != null) {
                        attachToComponent(parent, FAttachmentTransformRules.KeepRelativeTransform, position.socketName)
                        null
                    } else {
                        position.estimatedTransform
                    }
                }
            }
            if (transform != null) {
                relativeTransform = transform
            }
            registerComponent()
            activateCallbacks()
            onAudioFinished += {
                unregisterComponent()
            }
            play(startTime)
        }
        if (duration > 0.0f) {
            viewModel.addAnimation(object : Animation(duration) {
                override fun onEnd() {
                    component.fadeOut(0.5f, 0.0f, EAudioFaderCurve.Sin)
                }
            })
        }
    }
}
