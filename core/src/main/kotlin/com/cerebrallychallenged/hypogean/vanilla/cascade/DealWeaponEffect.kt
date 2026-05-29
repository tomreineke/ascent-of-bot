package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.effect.MutableEffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.causedStatusEffects
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.rays.HitResult
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.jun.math.geo.Vec2i


context(CascadeBlock)
suspend fun dealWeaponEffects(
    weapon: Weapon,
    hitResult: HitResult
) {
    dealWeaponEffects(weapon, hitResult.position, hitResult.hitEntities)
}

context(CascadeBlock)
suspend fun dealWeaponEffects(
    weapon: Weapon,
    hitPosition: Vec2i,
    hitEntities: List<LocatedEntity>,
) {
    val baseModifiers = MutableEffectModifiers()
    // FIXME: add StatusEffects etc. that increase weapon effect to baseModifiers.
    for (hitEntity in hitEntities) {
        val directEffect = weapon.directEffect
        val causedStatusEffects = weapon.causedStatusEffects
        schedule {
            dealDirectEffect(hitEntity, directEffect, baseModifiers, EffectReason.ByEntity(weapon))
            causeStatusEffects(hitEntity, causedStatusEffects)
        }
    }
    weapon.areaEffect?.let { areaEffect ->
        schedule {
            dealAreaEffects(areaEffect, null, hitPosition, EffectReason.ByEntity(weapon))
        }
    }
}
