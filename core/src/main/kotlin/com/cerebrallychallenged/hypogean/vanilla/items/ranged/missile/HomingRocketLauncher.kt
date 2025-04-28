package com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.model.effect.CausedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.effect.update
import com.cerebrallychallenged.hypogean.model.entityTypeOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.rounds
import com.cerebrallychallenged.hypogean.model.transformWhenDropped
import com.cerebrallychallenged.hypogean.vanilla.actions.Attack
import com.cerebrallychallenged.hypogean.vanilla.attackfx.AudioFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.BulletsFx
import com.cerebrallychallenged.hypogean.vanilla.attackfx.attackFx
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.hasAdjustableRange
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.attributes.range
import com.cerebrallychallenged.hypogean.vanilla.attributes.setupTime
import com.cerebrallychallenged.hypogean.vanilla.effects.BluntDamage
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.items.DirectShotWeapon
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.vanilla.refs.Images
import com.cerebrallychallenged.hypogean.vanilla.refs.Pixabay
import com.cerebrallychallenged.hypogean.vanilla.statuseffects.Burning
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings
import com.cerebrallychallenged.hypogean.view.actionbar.CrosshairBadge
import com.cerebrallychallenged.hypogean.view.tooltip.explanatoryTooltip
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f

/**
 * No inheritance from [RocketLauncher] because we don't want it to be a [DirectShotWeapon]. If it were a
 * [DirectShotWeapon], this would lead to the creation of
 * [com.cerebrallychallenged.hypogean.vanilla.actions.DirectShotActionInstance]s additionally to
 * [com.cerebrallychallenged.hypogean.vanilla.actions.HomingShotActionInstance]s, which we don't want because
 * they are mutually exclusive.
 */
open class HomingRocketLauncher(initializer: Initializer) : Weapon(initializer) {
    init {
        name = "Homing Rocket Launcher"
        icon = Images.RocketCrosshair
        asset = Asset_RocketLauncher
        initiativeCost = 2
        setupTime = 1
        directEffect = Effect(
            1 of BluntDamage,
        )
        causedStatusEffects = CausedStatusEffects(
            5 of Burning over 2.rounds
        )
        areaEffect = areaEffect.update(16..20 of ExplosionDamage)
        range = 12.0f
        explanatoryTooltip = "The rocket can track targets around corners."
        activeEnergyConsumption = ActiveEnergyConsumption.PerAction(2)
        transformWhenDropped = { _, _ -> Transform3f.scale(Vec3f.ONE * 0.2f) }
        attackFx = RocketShotFx(
            BulletsFx(Asset_Rocket, 3.0f, socketName = "rocket1"),
            AudioFx(Pixabay.Rocket, 0.0f, 1.0f, socketName = "rocket1"),
            0.3f
        )
        hasAdjustableRange = true
    }
}

internal object HomingRocketLauncherAppearance : ActionButtonStylings({
    defineStyling(
        category = ActionCategory.Attack,
        tool = entityTypeOf<HomingRocketLauncher>()
    ) {
        icon = ActionIcons.Rocket
        badge(CrosshairBadge)
    }
})
