package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.modding.Registry
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.safeCast

/**
 * EntityType is used to refer to the type of entities known only at runtime.
 */
class EntityType<out T: Entity>(internal val clazz: KClass<out T>, val factory: (Initializer) -> T) {
    val id = clazz.qualifiedName ?: modelError("Entity class $clazz has no qualified name")

    val simpleName = clazz.simpleName ?: modelError("Entity class $clazz has no simple name")

    /**
     * Must only be called from [EntityType.asSubTypeOf].
     */
    private fun isClassSubclassOf(otherClass: KClass<*>): Boolean = clazz.isSubclassOf(otherClass)

    fun isInstance(entity: Entity): Boolean = clazz.isInstance(entity)

    fun asInstance(entity: Entity): T? = clazz.safeCast(entity)

    inline fun <reified U: Entity> asSubTypeOf(): EntityType<U>? = asSubTypeOf(U::class)

    // Safe as the compatibility is ensured by runtime reflection in `isClassSubclassOf`.
    @Suppress("UNCHECKED_CAST")
    fun <U: Entity> asSubTypeOf(baseClass: KClass<U>): EntityType<U>?
            = if (isClassSubclassOf(baseClass)) this as EntityType<U> else null

    fun asWorldType(): EntityType<World>? = asSubTypeOf()

    fun asCellType(): EntityType<Cell>? = asSubTypeOf()

    fun asActorType(): EntityType<Actor>? = asSubTypeOf()

    fun asItemType(): EntityType<Item>? = asSubTypeOf()

    fun asFactionType(): EntityType<FactionEntity>? = asSubTypeOf()

    fun asEventType(): EntityType<Event>? = asSubTypeOf()

    fun asStatusEffectType(): EntityType<StatusEffect>? = asSubTypeOf()

    fun asTransientType(): EntityType<Transient>? = asSubTypeOf()

    fun spaceSeparatedHierarchyString(): String =
        clazz.java.hierarchy().map { it.canonicalName }.joinToString(" ")

    override fun equals(other: Any?): Boolean {
        return when {
            other === this -> true
            other !is EntityType<*> -> false
            else -> this.clazz == other.clazz
        }
    }

    override fun hashCode(): Int = clazz.hashCode()
}

class EntityTypes : Registry, Iterable<EntityType<*>> {
    private val entityTypes = mutableMapOf<String, EntityType<*>>()

    inline fun <reified T : Entity> register(noinline factory: (Initializer) -> T) {
        register(T::class, factory)
    }

    @PublishedApi
    internal fun <T : Entity> register(entityClass: KClass<out Entity>, factory: (Initializer) -> T) {
        val entityType = EntityType(entityClass, factory)
        entityTypes[entityType.id] = entityType
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Entity> get(id: String): EntityType<T> =
            (entityTypes[id] ?: modelError("Entity class $id not found")) as EntityType<T>

    operator fun <T : Entity> get(clazz: KClass<T>): EntityType<T> = this[clazz.jvmName]

    inline fun <reified T : Entity> get(): EntityType<T> = this[T::class]

    override fun iterator(): Iterator<EntityType<*>> = entityTypes.values.iterator()
}

/**
 * Returns the lower (more special) subtype of this and other.
 *
 * @throws ModelException if no entity type is subtype of the other.
 */
fun <T : Entity> min(first: EntityType<T>, second: EntityType<T>): EntityType<T> {
    // Safe as compatibility guaranteed by runtime reflection.
    @Suppress("UNCHECKED_CAST")
    return when {
        first.clazz.isSubclassOf(second.clazz) -> first
        second.clazz.isSubclassOf(first.clazz) -> second
        else ->
            modelError("Cannot obtain common subtype of $first and $second as none is subtype of the other")
    }
}

internal class EntityTypeAttributeCodec<T : Entity>(private val baseClass: KClass<T>) : AttributeCodec<EntityType<T>>

private fun Class<*>.hierarchy(): Sequence<Class<*>> = sequence {
    yield(this@hierarchy)
    superclass?.let { yieldAll(it.hierarchy()) }
}
