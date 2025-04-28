@file:Suppress("ClassName")

package com.cerebrallychallenged.hypogean.vanilla.items.ranged.ballistic

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AttackFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AudioFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.BulletsFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.MuzzleFlashFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.attackFx
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_MachineGun
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.Pixabay
import com.cerebrallychallenged.hypogean.vanilla.refs.ShootingVfxPack
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.RankBadge
import com.cerebrallychallenged.jun.asset.CompositeAsset
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.asset.niagaraComponent
import com.cerebrallychallenged.jun.unreal.UObject

open class MachineGun(initializer: Initializer) : DirectShotWeapon(initializer) {
    init {
        name = "Machine Gun"
        weight = 12.0f
        icon = Images.MachineGun2
        asset = Asset_MachineGun
        attackFx = MachineGunFx(
            MuzzleFlashFx(ShootingVfxPack.AR_Muzzleflash_1_INFINITE, 1.0f, "muzzle"),
            BulletsFx(Asset_BulletTrail, 100.0f, 8, 0.05f, "muzzle"),
            AudioFx(Pixabay.MachineGun, 1.0f, 0.5f, "muzzle")
        )
        cooldown = 2
        initiativeCost = 2
        directEffect = Effect(
            9..11 of BluntDamage
        )
        range = 10.0f
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(1)
    }
}

object Asset_BulletTrail : CompositeAsset({
    niagaraComponent {
        asset = load(ShootingVfxPack.NS_BulletTrail_1)
    }
})

class MachineGunFx(
    private val muzzleFlash: MuzzleFlashFx?,
    private val bullets: BulletsFx?,
    private val sound: AudioFx?,
) : AttackFx {
    context(AttackFx.Context)
    override suspend fun executeFx() {
        muzzleFlash?.executeFx()
        sound?.executeFx()
        bullets?.executeFx()
    }

    override fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {
        muzzleFlash?.collectAssetRefs(refs)
        bullets?.collectAssetRefs(refs)
        sound?.collectAssetRefs(refs)
    }

    context(AttackFx.Context)
    override fun estimateDuration(): Float = bullets?.estimateDuration() ?: 0.0f
}

internal object MachineGunAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<MachineGun>()
    ) {
        icon = ActionIcons.MachineGun
        badge(RankBadge, 2)
    }
})
