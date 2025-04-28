package com.cerebrallychallenged.hypogean.vanilla.attackfx

import com.cerebrallychallenged.hypogean.view.map.events.LinearCompositeEvent
import com.cerebrallychallenged.hypogean.view.map.events.Position
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UObject

class BulletsFx(
    val asset: CompositeAsset,
    val speed: Float,
    val count: Int = 1,
    val delay: Float = 0.0f,
    val socketName: String
) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        repeat(count) {
            if (it > 0) {
                delay(delay)
            }
            LinearCompositeEvent(
                asset,
                speed,
                shootingPosition(socketName, skipIfSocketNotFound = false),
                Position.Absolute(hitPosition)
            ).notifyWorldAndDelay()
        }
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        asset.collectAssetRefs(refs)
    }

    context(AttackFx.Context)
    override fun estimateDuration(): Float {
        val distance =
            requireNotNull(shootingPosition(socketName, skipIfSocketNotFound = false).estimatedTransform).translation
                .distanceTo(Position.Absolute(hitPosition).estimatedTransform.translation) * 0.01f
        return distance / speed
    }
}
