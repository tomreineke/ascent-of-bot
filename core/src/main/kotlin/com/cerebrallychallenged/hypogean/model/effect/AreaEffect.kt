package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.rays.BlockerValueExtractor
import com.cerebrallychallenged.hypogean.vanilla.cascade.Falloff
import com.cerebrallychallenged.hypogean.vanilla.cascade.FlatFalloff
import com.cerebrallychallenged.hypogean.vanilla.cascade.LinearFalloff
import com.cerebrallychallenged.hypogean.vanilla.rays.BallisticExtractor
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.unreal.UParticleSystem
import com.cerebrallychallenged.jun.unreal.sound.USoundBase


data class AreaEffect(
    val effect: Effect,

    /**
     * Only entities within that radius are affected by the explosion.
     */
    val radius: Float,

    /**
     * [Falloff] of the explosion damage.
     *
     * @see LinearFalloff
     * @see FlatFalloff
     */
    val falloff: Falloff,

    /**
     * [BlockerValueExtractor], which determines how good potential blockers shield from the explosion.
     */
    val blockerValueExtractor: BlockerValueExtractor,

    /**
     * Stronger explosions overcome the shielding of blockers.
     */
    val penetrationStrength: Float,

    /**
     * Asset id of the particle effect representing the explosion.
     */
    val particleAsset: UnrealRef<UParticleSystem>?,

    /**
     * Asset id of the sound of the explosion.
     */
    val soundAsset: UnrealRef<USoundBase>?
) {
    constructor(
        vararg effect: EffectValue,
        radius: Float = 1.0f,
        falloff: Falloff = LinearFalloff,
        blockerValueExtractor: BlockerValueExtractor = BallisticExtractor,
        penetrationStrength: Float = 0.0f,
        particleAsset: UnrealRef<UParticleSystem>? = null,
        soundAsset: UnrealRef<USoundBase>? = null
    ) : this(Effect(*effect), radius, falloff, blockerValueExtractor, penetrationStrength, particleAsset, soundAsset)

    fun isEmpty(): Boolean = effect.isEmpty()

    fun update(vararg effects: EffectValue): AreaEffect = copy(effect = effect.update(*effects))

    fun computeIntensity(distance: Float): Float = falloff.compute(distance, radius)
}

fun AreaEffect?.update(vararg effects: EffectValue): AreaEffect = this?.update(*effects) ?: AreaEffect(*effects)

/**
 * Area effect originating at the target when this `Entity` is used as a tool.
 */
var Entity.areaEffect: AreaEffect? by attribute(null)

/**
 * Area effect origination at this `Entity` when it is destroyed.
 */
var Entity.destructionEffect: AreaEffect? by attribute(null)
