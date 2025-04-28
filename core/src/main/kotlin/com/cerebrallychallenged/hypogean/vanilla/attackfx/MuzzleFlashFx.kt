package com.cerebrallychallenged.hypogean.vanilla.attackfx

import com.cerebrallychallenged.hypogean.view.map.events.NiagaraEvent
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraSystem

class MuzzleFlashFx(
    private val system: UnrealRef<UNiagaraSystem>,
    private val duration: Float,
    private val socketName: String? = null,
    private val relativeTransform: Transform3f = Transform3f.IDENTITY
) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        world.notifyViewEvent(
            NiagaraEvent(
                system,
                duration,
                shootingPosition(socketName, skipIfSocketNotFound = true),
                relativeTransform,
                skipIfSocketNotFound = true
            )
        )
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        refs.add(system)
    }
}
