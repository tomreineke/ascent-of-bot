package com.cerebrallychallenged.hypogean.modding

import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.model.rulebookSpecificationError
import com.cerebrallychallenged.jun.util.reflect.resolveTypeParameter
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.jvm.jvmName

interface Feature

interface Registry : Feature

interface ObjectRegistry<T : Any> : Registry

/**
 * Registry where each item is associated with an id for serialization.
 */
interface IdRegistry<T : Any> : Registry {
    val interfaceClass: KClass<T>

    fun idForItem(item: T): String

    fun itemForId(id: String): T
}

abstract class SimpleObjectRegistry<T : Any> : AbstractSet<T>(), IdRegistry<T>, ObjectRegistry<T> {

    @Suppress("UNCHECKED_CAST") // Safe since every subclass conforms to generic parameter T.
    override val interfaceClass =
            this::class.resolveTypeParameter(SimpleObjectRegistry::class).classifier as KClass<T>

    private val itemById = mutableMapOf<String, T>()

    private val idByItem = mutableMapOf<T, String>()

    override val size: Int
        get() = idByItem.keys.size

    override fun iterator(): Iterator<T> = idByItem.keys.iterator()

    override fun contains(element: T): Boolean = element in idByItem

    override fun idForItem(item: T): String =
            idByItem[item] ?: modelError("Item $item not registered by ${this::class}")

    override fun itemForId(id: String): T =
            itemById[id] ?: modelError("""No item with id "$id" registered by ${this::class}""")

    inline fun <reified U : T> itemByClass(): U = itemForId(U::class.jvmName) as U

    fun <U : T> itemByClass(clazz: KClass<U>): U = clazz.cast(itemForId(clazz.jvmName))

    @Suppress("Unused") // ModContext is required to prevent registering after rulebook construction.
    inline fun <reified U : T> ModContext.register(item: U) {
        register(item, U::class)
    }

    @PublishedApi
    internal fun register(item: T, clazz: KClass<out T>) {
        val id = clazz.jvmName
        val prevItem = itemById.put(id, item)
        if (prevItem != null) {
            rulebookSpecificationError(
                    """${this::class} cannot register item $item for id "$id", since the id is already taken by $prevItem"""
            )
        }
        idByItem[item] = id
    }

    override fun hashCode(): Int = System.identityHashCode(this)

    override fun equals(other: Any?): Boolean = this === other
}

class Features : SimpleObjectRegistry<Feature>() {
    fun <T : Any> registryForInterfaceClass(interfaceClass: KClass<T>): IdRegistry<T>? =
            asSequence().filterIsInstance<IdRegistry<T>>().find { it.interfaceClass == interfaceClass }
}
