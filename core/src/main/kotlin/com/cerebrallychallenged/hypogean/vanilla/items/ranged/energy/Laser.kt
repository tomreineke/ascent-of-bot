package com.cerebrallychallenged.hypogean.vanilla.items.ranged.energy

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.transformWhenDropped
import com.cerebrallychallenged.hypogean.vanilla.accuracy
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AttackFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AudioFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.RayFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.attackFx
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.LaserDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_Laser
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.Pixabay
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.UObject

open class Laser(initializer: Initializer) : DirectShotWeapon(initializer) {
    init {
        name = "Laser"
        weight = 1.0f
        icon = Images.RayGun2
        asset = Asset_Laser
        transformWhenDropped = { _, _ -> Transform3f.scale(Vec3f.ONE * 0.5f) }
        attackFx = LaserFx(
            RayFx(0.3f, Hypogean.MI_Laser, 0.05f),
            AudioFx(Pixabay.Blaster2, 0.0f, null, "muzzle")
        )
        cooldown = 1
        initiativeCost = 2
        directEffect = Effect(4..6 of LaserDamage)
        accuracy = 0.9f
        range = 16.0f
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(2)
    }
}

internal object MediumLaserAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<Laser>()
    ) {
        icon = ActionIcons.RayGun
        badge(RankBadge, 2)
    }
})

class LaserFx(
    private val ray: RayFx?,
    val sound: AudioFx?
) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        ray?.executeFx()
        sound?.executeFx()
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        ray?.collectAssetRefs(refs)
        sound?.collectAssetRefs(refs)
    }

    context(AttackFx.Context)
    override fun estimateDuration(): Float = ray?.estimateDuration() ?: 0.0f
}
