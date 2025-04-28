package com.cerebrallychallenged.hypogean.vanilla.cascade

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.base.presentProps
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.effect.AreaEffect
import com.cerebrallychallenged.hypogean.model.effect.EffectKind
import com.cerebrallychallenged.hypogean.model.effect.EffectModifier
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.MutableEffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.addPassiveModifiersAndImmunitiesForTarget
import com.cerebrallychallenged.hypogean.model.effect.any
import com.cerebrallychallenged.hypogean.model.effect.percentOf
import com.cerebrallychallenged.hypogean.model.maps.Entity2ObjectMap
import com.cerebrallychallenged.hypogean.view.audio.AudioEvent
import com.cerebrallychallenged.hypogean.view.globalDirectionalBrightness
import com.cerebrallychallenged.hypogean.view.map.events.HideEvent
import com.cerebrallychallenged.hypogean.view.map.events.ParticleEvent
import com.cerebrallychallenged.hypogean.view.map.events.Position
import com.cerebrallychallenged.jun.math.ceilToInt
import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.points
import com.cerebrallychallenged.jun.math.truncateToInt
import kotlin.math.max


/**
 * Falloff of the explosion damage. Determines how strong an entity is damaged by an explosion with a specific radius
 * at a specific distance from its center.
 */
interface Falloff {
    fun compute(distance: Float, radius: Float): Float
}

class Falloffs : SimpleObjectRegistry<Falloff>()

/**
 * Linear decrease from 1.0 at the center of the explosion to 0.0 at radius distance.
 */
object LinearFalloff : Falloff {
    override fun compute(distance: Float, radius: Float): Float = max(1.0f - distance / radius, 0.0f)
}

/**
 * Constant 1.0 within the explosion radius, and 0.0 outside the explosion radius.
 */
object FlatFalloff : Falloff {
    override fun compute(distance: Float, radius: Float): Float =
        if (distance <= radius) 1.0f else 0.0f
}

val Cell.presentEntities: Sequence<LocatedEntity>
    get() = sequence {
        yield(this@presentEntities)
        presentActor?.let { yield(it) }
        yieldAll(presentProps)
    }

data class EffectAttenuation(
    val exposure: Float,
    val intensity: Float,
    val distance: Float,
) : Comparable<EffectAttenuation> {
    private val product: Float = exposure * intensity

    override fun compareTo(other: EffectAttenuation): Int = product.compareTo(other.product)
}


context(CascadeBlock)
suspend fun dealAreaEffects(
    areaEffect: AreaEffect,
    explodingEntity: Entity?,
    center: Vec2i,
    reason: EffectReason
) {
    if (isReal) {
        val position3f = world.cellAt(center)?.basePoint ?: center.toFloat().append(0.0f)
        areaEffect.particleAsset?.let { particleSystem ->
            if (explodingEntity != null) {
                world.notifyViewEvent(HideEvent(explodingEntity))
            }

            world.notifyViewEvent(ParticleEvent(
                particleSystem,
                1.0f,
                position3f,
                scale = Vec3f.ONE * areaEffect.radius
            ))
            schedule {
                delay(2.0f)
                world.globalDirectionalBrightness = 0.7f
            }
        }
        areaEffect.soundAsset?.let { sound ->
            world.notifyViewEvent(AudioEvent(sound, Position.Absolute(position3f)))
        }
        explodingEntity?.remove()
    }


    val effectMap = Entity2ObjectMap<Entity, EffectAttenuation>(world)
    val raysQuery = world.queryRays(center, areaEffect.blockerValueExtractor, explodingEntity)
    for (pos in Bounds.centered(center, Vec2i.ONE * areaEffect.radius.ceilToInt()).points) {
        val distance = center.distanceTo(pos)
        if (distance <= areaEffect.radius) {
            world.cellAt(pos)?.let { cell ->
                // 1.0 at center of explosion
                // 0.0 at radius distance
                // Some value between 0.0 and 1.0 for intermediate distance depending on falloff.
                val intensity = areaEffect.computeIntensity(distance)
                if (intensity > 0.0) {
                    // Value between 0.0 (hidden behind blockers) and 1.0 (fully exposed).
                    val exposure = raysQuery.computeExposure(pos, areaEffect.penetrationStrength)
                    if (exposure > 0.0f) {
                        val attenuation = EffectAttenuation(exposure, intensity, distance)
                        for (entity in cell.presentEntities) {
                            // If a large actor occupies multiple cells which are all affected by that explosion,
                            // we register only the cell receiving the maximum effect rather than adding up the effects.
                            effectMap.merge(entity, attenuation) { a, b -> maxOf(a, b) }
                        }
                    }
                }
            }
        }
    }
    val effect = areaEffect.effect
    for ((target, attenuation) in effectMap) {
        val modifiers = MutableEffectModifiers()
        modifiers.addPassiveModifiersAndImmunitiesForTarget(target)
        val (exposure, intensity, distance) = attenuation
        val cover = ((1.0 - exposure) * 100).truncateToInt()
        if (cover > 0) {
            modifiers.add(
                EffectModifiers.Phase.Cover,
                EffectModifier(-cover percentOf any<EffectKind>()),
                EffectModifiers.Reason.Cover
            )
        }
        val falloff = ((1.0 - intensity) * 100).truncateToInt()
        if (falloff > 0) {
            modifiers.add(
                EffectModifiers.Phase.Falloff,
                EffectModifier(-falloff percentOf any<EffectKind>()),
                EffectModifiers.Reason.Distance(distance)
            )
        }
        schedule {
            dealEffect(target, effect, modifiers, reason)
        }
    }
}
