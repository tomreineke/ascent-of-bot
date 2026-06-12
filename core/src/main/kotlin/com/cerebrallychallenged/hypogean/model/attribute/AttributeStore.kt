package com.cerebrallychallenged.hypogean.model.attribute

import com.cerebrallychallenged.hypogean.model.AbstractEntity
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.util.appendBits
import com.cerebrallychallenged.hypogean.util.separateBits
import com.cerebrallychallenged.jun.log.log
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

// TODO Determine optimal size once we know the typical number of entities and attributes.
private const val EXPECTED_SIZE = 1 shl 16

private fun key(attribute: Attribute<*>, entity: Entity): Long = attribute.id.appendBits(entity.id)

internal class AttributeStore(rulebook: Rulebook, private val world: World) {
    private val attributeValues: Long2ObjectOpenHashMap<Any> = Long2ObjectOpenHashMap(EXPECTED_SIZE)

    private val attributes = rulebook.attributes

    operator fun get(attribute: Attribute<*>, entity: Entity): Any? = attributeValues[key(attribute, entity)]

    operator fun <T> set(attribute: Attribute<T>, entity: Entity, value: T) {
        val attributeEntityKey = key(attribute, entity)
        @Suppress("UNCHECKED_CAST")
        val prevValue = (attributeValues.put(attributeEntityKey, value) as T?) ?: attribute.defaultValue
        val change = WorldChange.AttributeChanged(entity, attribute, value, prevValue)

        attribute.referringHandler?.let { handler ->
            for (prevReferredEntity in handler.referredEntities(prevValue)) {
                (prevReferredEntity as AbstractEntity).referrers.remove(attributeEntityKey)
            }
            for (referredEntity in handler.referredEntities(value)) {
                (referredEntity as AbstractEntity).referrers.add(attributeEntityKey)
            }
        }
        (entity as AbstractEntity).onAttributeChanged(change)
        // During the construction the entity is set up with the _same_ given initial values on server and clients.
        // So no notifications have to be sent.
        if (!entity.isUnderConstruction) {
            world.notify(change)
        }
    }

    private fun Pair<Int, Int>.toAttributeAndEntity(): Pair<Attribute<*>, Entity> =
            Pair(attributes[first], world.entityById(second))

    private fun <T> removeAttributeReference(referringEntity: Entity, attribute: Attribute<T>, removedEntity: Entity) {
        attribute.referringHandler?.let { handler ->
            val prevValue = attribute.getValue(referringEntity)
            val newValue = handler.substituteOnRemove(removedEntity, prevValue)
            if (prevValue != newValue) {
                if (newValue == null) {
                    if (attribute.isNullable) {
                        @Suppress("UNCHECKED_CAST")
                        (attribute as Attribute<T?>).setValue(referringEntity, null)
                    } else {
                        modelError(
                                "Removing $removedEntity results in invalid attribute $attribute for $referringEntity"
                        )
                    }
                } else {
                    attribute.setValue(referringEntity, newValue)
                }
            }
        }
    }

    internal fun removeAllAttributesOfEntity(removedEntity: Entity) {
        val referrerIterator = (removedEntity as AbstractEntity).referrers.iterator()
        log.info { "removing entity with ID ${removedEntity.id} and name ${removedEntity.name}" }
        while (referrerIterator.hasNext()) {
            val pair = referrerIterator.nextLong().separateBits()
            log.info { "trying to remove referring entity with ID ${pair.second}"}
            val (attribute, referringEntity) = pair.toAttributeAndEntity()
            log.info { "and name ${referringEntity.name}" }
            removeAttributeReference(referringEntity, attribute, removedEntity)
        }
        for (attribute in removedEntity.changedAttributes) {
            attributeValues.remove(key(attribute, removedEntity))
        }
    }

    fun clear() {
        attributeValues.clear()
    }

    val changedValueCount: Int
        get() = attributeValues.size
}
