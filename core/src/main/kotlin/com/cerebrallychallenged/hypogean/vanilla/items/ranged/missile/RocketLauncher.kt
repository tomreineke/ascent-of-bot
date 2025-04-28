@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.AreaEffect
import com.cerebrallychallenged.hypogean.model.effect.CausedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.rounds
import com.cerebrallychallenged.hypogean.model.transformWhenDropped
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AttackFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AudioFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.BulletsFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.attackFx
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.hasAdjustableRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.remainingUseCount
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.ModularLowPolyRobots.SM_HeavyRocket
import com.cerebrallychallenged.hypogean.vanilla.refs.ModularLowPolyRobots.SM_HeavyRocketPlatform
import com.cerebrallychallenged.hypogean.vanilla.refs.Pixabay
import com.cerebrallychallenged.hypogean.vanilla.refs.ShootingVfxPack
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.Burning
import com.cerebrallychallenged.hypogean.view.Visibility
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge
import com.cerebrallychallenged.hypogean.view.map.events.NodeVisibilityEvent
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.niagaraComponent
import com.cerebrallychallenged.jun.asset.sceneComponent
import com.cerebrallychallenged.jun.asset.staticMeshComponent
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.UObject

open class RocketLauncher(initializer: Initializer) : DirectShotWeapon(initializer) {
    init {
        name = "Rocket Launcher"
        remainingUseCount = 1
        explanatoryTooltip = "Can only be used $remainingUseCount times."
        weight = 5.0f
        icon = Images.Rocket1
        asset = Asset_RocketLauncher
        transformWhenDropped = { _, _ -> Transform3f.scale(Vec3f.ONE * 0.2f) }
        attackFx = RocketShotFx(
            BulletsFx(Asset_Rocket, 3.0f, socketName = "rocket1"),
            AudioFx(Pixabay.Rocket, 0.0f, 1.0f, socketName = "rocket1"),
            0.3f
        )
        cooldown = 0
        initiativeCost = 1
        directEffect = Effect(
            2 of BluntDamage
        )
        causedStatusEffects = CausedStatusEffects(
            5 of Burning over 3.rounds
        )
        areaEffect = AreaEffect(
            20..30 of ExplosionDamage,
            radius = 4.0f,
            penetrationStrength = 1.0f,
            particleAsset = Hypogean.P_Explosion,
            soundAsset = Pixabay.HQExplosion
        )
        range = 8.0f
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(1)
    }
}

object Asset_RocketLauncher : CompositeAsset({
    staticMeshComponent {
        staticMesh = load(SM_HeavyRocketPlatform)
        staticMeshComponent("rocket1") {
            declareSocket("rocket1")
            staticMesh = load(SM_HeavyRocket)
            Visibility.bind {
                visibility = it
            }
        }
    }
})

object Asset_Rocket : CompositeAsset({
    sceneComponent {
        staticMeshComponent {
            staticMesh = load(SM_HeavyRocket)
            relativeScale3D = Vec3f.ONE * 0.2f
            niagaraComponent("exhaust") {
                asset = load(ShootingVfxPack.NS_ROCKET_Trail)
            }
        }
    }
}) {
    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        refs.add(SM_HeavyRocket)
        refs.add(ShootingVfxPack.NS_ROCKET_Trail)
    }
}

class RocketShotFx(
    private val rocketFlight: BulletsFx?,
    private val sound: AudioFx?,
    private val startDelay: Float
) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        val soundWithDuration = rocketFlight?.estimateDuration()?.let { duration ->
            sound?.copy(duration = duration + startDelay)
        } ?: sound
        soundWithDuration?.executeFx()
        schedule {
            delay(startDelay)
            world.notifyViewEvent(NodeVisibilityEvent(false, attackingActor, weapon))
            rocketFlight?.executeFx()
        }
        val remainingUseCount = weapon.remainingUseCount
        if (remainingUseCount == null || remainingUseCount > 0) {
            schedule {
                delay(2.0f)
                world.notifyViewEvent(NodeVisibilityEvent(true, attackingActor, weapon))
            }
        }
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        rocketFlight?.collectAssetRefs(refs)
        sound?.collectAssetRefs(refs)
    }

    context(AttackFx.Context)
    override fun estimateDuration(): Float = startDelay + (rocketFlight?.estimateDuration() ?: 0.0f)
}

internal object RocketLauncherAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<RocketLauncher>()
    ) {
        icon = ActionIcons.Rocket
        badge(RankBadge, 1)
    }
})
