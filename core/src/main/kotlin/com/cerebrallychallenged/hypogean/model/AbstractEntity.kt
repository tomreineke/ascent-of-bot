package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.removeOnDeath
import com.cerebrallychallenged.jun.log.log
import it.unimi.dsi.fastutil.longs.LongOpenHashSet

abstract class AbstractEntity(initializer: Initializer) : Entity {
    final override val id: Int = initializer.id

    private var _isAlive: Boolean = true

    final override val isAlive: Boolean
        get() = _isAlive

    final override var statusEffects: List<StatusEffect> = listOf()
        private set

    override fun remove() {
        if (!isAlive) {
            modelError("This entity $this has already been removed")
        }
        health = 0
        _isAlive = false
        for (entity in removeOnDeath) {
            if (entity.isAlive) {
                entity.health = 0
                entity.remove()
            }
        }
        for (statusEffect in statusEffects) {
            statusEffect.remove()
        }
        world.removeMe(this)
    }

    private val _changedAttributes = mutableSetOf<Attribute<*>>()

    final override val changedAttributes: Set<Attribute<*>>
        get() = _changedAttributes

    // Used from AttributeStore,
    // all pairs of entities and attributes referring to this entity.
    internal val referrers = LongOpenHashSet()

    open fun onAttributeChanged(change: WorldChange.AttributeChanged<*>) {
        _changedAttributes.add(change.attribute)
    }

    override fun collectInitialChanges(collector: (WorldChange) -> Unit) {
        for (attribute in changedAttributes) {
            attribute.collectInitialChanges(this, collector)
        }
    }

    override fun toString(): String {
        val hexId = "%08X".format(id)
        return "${javaClass.simpleName} [$hexId]"
    }

    final override val isDummy: Boolean
        get() = world.dummyEntity === this

    internal fun internalAddStatusEffect(statusEffect: StatusEffect) {
        statusEffects += statusEffect
    }

    internal fun internalRemoveStatusEffect(statusEffect: StatusEffect) {
        statusEffects -= statusEffect
    }

    final override fun <T: StatusEffect> createStatusEffect(statusEffectFactory: (Initializer) -> T): T {
        return world.internalCreate(statusEffectFactory, this, isAlive = true)
    }
}
