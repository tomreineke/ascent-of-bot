package com.cerebrallychallenged.hypogean.vanilla.attackfx

import com.cerebrallychallenged.hypogean.view.audio.AudioEvent
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.sound.USoundBase

data class AudioFx(val asset: UnrealRef<USoundBase>, val startTime: Float, val duration: Float?, val socketName: String?) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        world.notifyViewEvent(AudioEvent(
            asset,
            shootingPosition(socketName, skipIfSocketNotFound = false),
            startTime,
            duration ?: 0.0f
        ))
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        refs.add(asset)
    }
}
