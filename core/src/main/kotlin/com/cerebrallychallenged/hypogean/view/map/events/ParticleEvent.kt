package com.cerebrallychallenged.hypogean.view.map.events

import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.view.map.MapViewContext
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.EPSCPoolMethod
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.UParticleSystem
import com.cerebrallychallenged.jun.unreal.UParticleSystemComponent
import com.cerebrallychallenged.jun.unreal.newObject

class ParticleEvent(
    private val particleSystem: UnrealRef<UParticleSystem>,
    override val duration: Float,
    private val position: Vec3f,
    private val rotation: Quaternion = Quaternion.IDENTITY,
    private val scale: Vec3f = Vec3f.ONE
) : MapViewEvent() {
    context(MapViewContext)
    override suspend fun execute() {
        val emitterTemplate = assetLibrary.load(particleSystem)
        if (duration == 0.0f) {
            UGameplayStatics.spawnEmitterAtLocation(
                emitterTemplate = emitterTemplate,
                location = position * 100.0f,
                rotation = rotation,
                scale = scale,
                autoDestroy = true,
                poolingMethod = EPSCPoolMethod.AutoRelease
            )
        } else {
            addAnimation(object : Animation(duration) {
                val particleSystemComponent = newObject<UParticleSystemComponent>().apply {
                    template = emitterTemplate
                    relativeLocation = position * 100.0f
                    relativeRotation = rotation
                    relativeScale3D = scale
                    registerComponent()
                }

                override fun onEnd() {
                    particleSystemComponent.unregisterComponent()
                }
            })
        }
    }
}
