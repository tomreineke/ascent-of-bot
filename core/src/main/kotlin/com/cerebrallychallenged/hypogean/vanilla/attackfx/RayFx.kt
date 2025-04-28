package com.cerebrallychallenged.hypogean.vanilla.attackfx

import com.cerebrallychallenged.hypogean.view.map.events.Position
import com.cerebrallychallenged.hypogean.view.map.events.RayEvent
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface

class RayFx(private val duration: Float, private val material: UnrealRef<UMaterialInterface>, private val diameter: Float) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        world.notifyViewEvent(RayEvent(
            shootingPosition("muzzle", skipIfSocketNotFound = false),
            Position.Absolute(hitPosition),
            duration,
            material,
            diameter
        ))
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        refs.add(material)
    }

    context(AttackFx.Context)
    override fun estimateDuration(): Float = duration
}
