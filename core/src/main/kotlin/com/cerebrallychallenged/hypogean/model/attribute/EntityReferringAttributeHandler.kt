package com.cerebrallychallenged.hypogean.model.attribute

import com.cerebrallychallenged.hypogean.modding.ModContext
import com.cerebrallychallenged.hypogean.modding.Registry
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.jun.util.reflect.CachingTypeClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Handling of attributes which refer to other entities.
 * @param T type
 */
interface EntityReferringAttributeHandler<T> {
    /**
     * All entities referred by the specified attribute value.
     */
    fun referredEntities(value: T): Set<Entity>

    /**
     * Substitutes the value such that it does not anymore refer to the specified deleted entity.
     */
    fun substituteOnRemove(removedEntity: Entity, value: T): T?
}

class SingleEntityReferringAttributeHandler<T : Entity>(
        @Suppress("unused")
        private val _entityClass: KClass<T>
) : EntityReferringAttributeHandler<T> {
    override fun referredEntities(value: T): Set<T> = setOf(value)

    override fun substituteOnRemove(removedEntity: Entity, value: T): T? =
            value.takeUnless { value == removedEntity }
}

class OptionalReferringAttributeHandler<T>(
        private val handler: EntityReferringAttributeHandler<T>
) : EntityReferringAttributeHandler<T?> {
    override fun referredEntities(value: T?): Set<Entity> = value?.let { handler.referredEntities(it) } ?: setOf()

    override fun substituteOnRemove(removedEntity: Entity, value: T?): T? = value.takeUnless { value == removedEntity }
}

class ListReferringAttributeHandler<T>(
        private val elementHandler: EntityReferringAttributeHandler<T>
) : EntityReferringAttributeHandler<List<T>> {
    override fun referredEntities(value: List<T>): Set<Entity> =
            value.flatMapTo(mutableSetOf()) { elementHandler.referredEntities(it) }

    override fun substituteOnRemove(removedEntity: Entity, value: List<T>): List<T> =
            value.mapNotNull { elementHandler.substituteOnRemove(removedEntity, it) }
}

class EntityReferringAttributeHandlers : Registry {
    @PublishedApi
    internal val handlers =
            CachingTypeClass(EntityReferringAttributeHandler::class)

    inline fun <reified T> get(): EntityReferringAttributeHandler<T>? = handlers.getForAppliedType()

    @Suppress("UNCHECKED_CAST")
    fun <T> get(type: KType): EntityReferringAttributeHandler<T>? =
            handlers.get(type) as EntityReferringAttributeHandler<T>?

    @Suppress("unused") // ModContext is required to prevent registering after rulebook construction.
    inline fun <reified T : EntityReferringAttributeHandler<*>> ModContext.register() {
        handlers.add<T>()
    }
}
