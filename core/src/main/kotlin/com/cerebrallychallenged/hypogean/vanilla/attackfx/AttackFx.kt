package com.cerebrallychallenged.hypogean.vanilla.attackfx

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.vanilla.actions.AttackSituation
import com.cerebrallychallenged.hypogean.view.map.events.Position
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.Transform3f.Companion.translation
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.UObject

interface AttackFx {
    class Context(
        block: CascadeBlock,
        val situation: AttackSituation,
        val weapon: Item,
        val hitPosition: Vec3f
    ) : CascadeBlock by block {
        val attackingActor: Actor
            get() = situation.activeActor

        fun shootingPosition(socketName: String?, skipIfSocketNotFound: Boolean): Position = Position.Node(
            attackingActor,
            weapon,
            socketName,
            if (skipIfSocketNotFound) null else translation(
                situation.shootingPosition2f.append(situation.activeActor.centerPoint.z) * 100.0f
            )
        )
    }

    context(CascadeBlock)
    suspend fun executeFx(situation: AttackSituation, weapon: Item, hitPosition: Vec3f) {
        with(Context(this@CascadeBlock, situation, weapon, hitPosition)) {
            executeFx()
        }
    }

    context(Context)
    suspend fun executeFx()

    fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {}

    context(Context)
    fun estimateDuration(): Float = 0.0f

    context(CascadeBlock)
    fun estimateDuration(situation: AttackSituation, weapon: Item, hitPosition: Vec3f): Float {
        return with(Context(this@CascadeBlock, situation, weapon, hitPosition)) {
            estimateDuration()
        }
    }
}

var Item.attackFx: AttackFx? by attribute(null)
