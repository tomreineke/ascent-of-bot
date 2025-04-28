package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.effect.AreaEffect
import com.cerebrallychallenged.hypogean.model.effect.destructionEffect
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.cascade.LinearFalloff
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.effects.ExplosionDamage
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Hypogean
import com.cerebrallychallenged.hypogean.vanilla.refs.Images.PortraitMine
import com.cerebrallychallenged.hypogean.vanilla.refs.Pixabay
import com.cerebrallychallenged.hypogean.vanilla.triggers.MineTrigger

open class LandMine(initializer: Initializer) : Prop(initializer) {
    init {
        name = "Mine"
        description = "Explodes when something comes within a range of $TRIGGER_RANGE."
        asset = Asset_LandMine
        icon = PortraitMine
        health = 20
        height = 0.3f
        ballisticBlocking = BlockingValue { 1.0f }
        showHealthChangesInDamageReport = true
        destructionEffect = AreaEffect(
            30..40 of ExplosionDamage,
            radius = 3.0f,
            falloff = LinearFalloff,
            particleAsset = Hypogean.P_Explosion,
            soundAsset = Pixabay.HQExplosion
        )
        initializer.addStatusEffect<MineTrigger>()
    }

    companion object {
        const val TRIGGER_RANGE = 1.0f
    }
}
