package com.cerebrallychallenged.hypogean.model.attribute

import com.cerebrallychallenged.hypogean.modding.Registry
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.model.rulebookSpecificationError
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.getExtensionDelegate
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.typeOf

private class DefaultValueBox<T>(val value: T)

class Attribute<T>(
        val property: KMutableProperty1<out Entity, T>,
        val id: Int,
        val codec: AttributeCodec<T>,
        val referringHandler: EntityReferringAttributeHandler<T>?,
        val isNullable: Boolean
) : ReadWriteProperty<Entity, T> {
    // It is no problem if the property is overwritten multiple times,
    // since it is always the same value (that was passed to attribute(...) at the declaration site).
    // Hence, we need no thread safety.
    private var defaultValueBox: DefaultValueBox<T>? = null

    internal var defaultValue: T
        get() {
            val box
                    = defaultValueBox
                    ?: throw IllegalStateException("Default value of attribute for $property has not been set")
            return box.value
        }

        // Must only be called from AttributeDelegateProvider.provideDelegate.
        set(value) {
            if (defaultValueBox == null) {
                defaultValueBox = DefaultValueBox(value)
            }
        }

    override fun getValue(thisRef: Entity, property: KProperty<*>): T {
        return getValue(thisRef)
    }

    fun getValue(entity: Entity): T {
        @Suppress("UNCHECKED_CAST")
        return entity.world.attributeStore[this, entity] as T? ?: defaultValue
    }

    override fun setValue(thisRef: Entity, property: KProperty<*>, value: T) {
        setValue(thisRef, value)
    }

    fun setValue(entity: Entity, value: T) {
        val world = entity.world
        world.attributeStore[this, entity] = value
    }

    fun collectInitialChanges(entity: Entity, collector: (WorldChange) -> Unit) {
        collector(WorldChange.AttributeChanged(entity, this, getValue(entity, property), defaultValue))
    }

    fun getFor(entity: Entity): T {
        return getValue(entity, property)
    }

    fun toDebugStringFor(entity: Entity): String {
        return codec.toDebugString(getFor(entity))
    }

    override fun toString(): String = "Attribute(id=$id, property=$property)"
}

class Attributes(
    private val codecs: AttributeCodecs,
    private val entityReferringHandlers: EntityReferringAttributeHandlers,
    @PublishedApi
    internal val attributes: MutableList<Attribute<*>> = mutableListOf()
) : Registry, List<Attribute<*>> by attributes {
    companion object {
        internal val globalAttributes = java.util.concurrent.atomic.AtomicReference<Attributes?>(null)
    }

    init {
        globalAttributes.set(this)
    }

    private val attributeByProperty =
            mutableMapOf<KMutableProperty1<out Entity, *>, Attribute<*>>()

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(property: KMutableProperty1<out Entity, T>): Attribute<T> =
            (attributeByProperty[property] ?: modelError("No attribute found for $property")) as Attribute<T>

    override fun get(index: Int): Attribute<*> = if (index in indices) {
        attributes[index]
    } else {
        modelError("No attribute with id $index")
    }

    inline fun <reified T> register(property: KMutableProperty1<out Entity, T>) {
        register(property, typeOf<T>())
    }

    @PublishedApi
    internal fun <T> register(property: KMutableProperty1<out Entity, T>, type: KType) {
        val codec = codecs.get<T>(type) ?: rulebookSpecificationError("No codec found for $property")
        val entityReferringHandler = entityReferringHandlers.get<T>(type)
        val attribute = Attribute(
                property,
                attributes.size,
                codec,
                entityReferringHandler,
                type.isMarkedNullable
        )
        attributes.add(attribute)
        val prev = attributeByProperty.put(property, attribute)
        if (prev != null) {
            rulebookSpecificationError("Attribute for $property initialized twice")
        }
    }

    fun triggerDelegateProviders() {
        for (attribute in attributes) {
            val property = attribute.property
            property.isAccessible = true
            try {
                property.getExtensionDelegate()
            } catch (_: Exception) {
                // Ignore the case that the delegate is no extension delegate.
            }
        }
    }
}

class AttributeDelegateProvider<T>(private val defaultValue: T) {
    operator fun provideDelegate(thisRef: Entity?, prop: KProperty<*>): Attribute<T> {
        // Safe since this instance of AttributeDelegateProvider is only used for the one property
        // whose compatibility is guaranteed by attribute(...)
        @Suppress("UNCHECKED_CAST")
        val property = prop as KMutableProperty1<out Entity, T>
        val attributes =
                thisRef?.rulebook?.attributes ?: Attributes.globalAttributes.get() ?: modelError("Attributes not found")
        val attribute = attributes[property]
        attribute.defaultValue = defaultValue
        return attribute
    }
}

fun <T> attribute(defaultValue: T): AttributeDelegateProvider<T> {
    return AttributeDelegateProvider(defaultValue)
}
