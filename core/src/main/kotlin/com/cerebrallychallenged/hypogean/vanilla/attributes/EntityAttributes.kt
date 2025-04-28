package com.cerebrallychallenged.hypogean.vanilla.attributes

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

/**
 * Health of that entity.
 *
 * The health is influenced by taking damage, healing, and repairing.
 * The deterioration of tools is also modelled with this attribute.
 * If an entity reaches health 0, it is destroyed.
 */
var Entity.health: Int by attribute(100)

/**
 * Can be used for bosses, so that upon their death certain things, like doors, disappear.
 */
var Entity.removeOnDeath: List<Entity> by attribute(emptyList())

/**
 * The maximum value the [health] attribute can usually have.
 *
 * Weapons with {@link DamageAttributes#DIRECT_RELATIVE_DAMAGE} deal damage proportionate the maximum health
 * of the target (before deductions because of shields etc.).
 */
var Entity.maxHealth: Int by attribute(100)

object Health : SimpleIntAttribute<Entity>(
    Entity::class,
    Entity::health,
    Entity::maxHealth,
    ImageResource("Images/gage/icon_health.png"),
    FLinearColor.rgb(1.0f, 0.0f, 0.0f),
    "Health",
    "❤️"
)

val Entity.relativeHealth: Double
    get() = health / maxHealth.toDouble()


object Ini : SimpleIntAttribute<Actor>(
    Actor::class,
    Actor::scheduledIniTime,
    Actor::maxIniTime,
    ImageResource("Images/gage/icon_health.png"),
    FLinearColor.rgb(0.0f, 1.0f, 0.0f),
    "Ini",
    "⌛"
)