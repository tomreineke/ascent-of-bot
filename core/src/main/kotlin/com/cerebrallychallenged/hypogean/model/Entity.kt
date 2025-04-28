package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.effect.StatusEffectWithIntensity
import com.cerebrallychallenged.hypogean.model.effect.StatusEffectWithIntensityAndDuration
import com.cerebrallychallenged.hypogean.vanilla.attributes.health

// When loading save games it can happen that an entity is alive, even though
// it was removed from the game and has health 0.
// I couldn't track down why this happens, and as a workaround, we have this
// method that can be used by views like the MapView.
// Cf. server.loadWorld in GameState->HypogeanApplicationFactory.execute()
fun Entity.isAlive(): Boolean = isAlive && health > 0

interface Entity : WorldContext {
    val id: Int

    /**
     * Returns if this entity is alive, i.e., has not already been removed.
     * @return if this entity is alive, i.e., has not already been removed.
     */
    val isAlive: Boolean

    fun remove()

    fun collectInitialChanges(collector: (WorldChange) -> Unit)

    val isDummy: Boolean

    /**
     * Is `true` while the constructors of this entity are running.
     * Allows to suppress attribute change events.
     */
    val isUnderConstruction: Boolean
        get() = world.entityUnderConstruction === this

    val changedAttributes: Set<Attribute<*>>

    val statusEffects: List<StatusEffect>

    /**
     * Creates a [StatusEffect] for this entity using the specified factory.
     * This is the preferred method if the desired subclass of [StatusEffect] is statically known.
     * Example:
     *     actor.createStatusEffect(::StatusEffectBurning)
     */
    fun <T: StatusEffect> createStatusEffect(statusEffectFactory: (Initializer) -> T): T

    /**
     * Creates a [StatusEffect] for this entity using the specified [EntityType].
     * Example:
     *     val statusEffectType: EntityType<StatusEffect> = ...
     *     actor.createStatusEffect(statusEffectType)
     */
    fun <T: StatusEffect> createStatusEffect(statusEffectType: EntityType<T>): T =
        createStatusEffect(statusEffectType.factory)

    fun addStatusEffect(statusEffectCompanion: StatusEffectCompanion): StatusEffect =
        statusEffectCompanion.createFor(this)

    fun addStatusEffect(statusEffectWithIntensity: StatusEffectWithIntensity): StatusEffect =
        statusEffectWithIntensity.createFor(this)

    fun addStatusEffect(statusEffectWithIntensityAndDuration: StatusEffectWithIntensityAndDuration): StatusEffect =
        statusEffectWithIntensityAndDuration.createFor(this)
}

const val DEFAULT_NAME: String = "<unnamed>"

/**
 * User-readable name for this entity.
 */
var Entity.name: String by attribute(DEFAULT_NAME)

inline fun <reified T : Entity> Entity.checkedType(): T
        = this as? T ?: modelError("Entity with id $id is no ${T::class}")

fun <T : Entity> Entity.checkedType(entityClass: Class<T>): T {
    if (entityClass.isInstance(this)) {
        @Suppress("UNCHECKED_CAST")
        return this as T
    } else {
        modelError("Entity with id $id is no $entityClass")
    }
}

val <T : Entity> T.type: EntityType<T>
    get() = entityTypeOf(this::class)
