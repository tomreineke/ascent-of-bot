package com.cerebrallychallenged.hypogean.model

/**
 * Any [Entity] besides the [World].
 */
abstract class NonWorldEntity(initializer: Initializer) : AbstractEntity(initializer) {
    final override val world: World = (initializer as NonWorldInitializer).world

    init {
        require(world.entityUnderConstruction == null) { "Entity construction must not be nested" }
        // Safe as entityUnderConstruction is used only for === comparison by the isUnderConstruction property.
        @Suppress("LeakingThis")
        world.entityUnderConstruction = this
    }

    inline fun <reified T: StatusEffect> Initializer.addStatusEffect(noinline f: (T.() -> Unit)? = null) {
        this@addStatusEffect as NonWorldInitializer
        val entityType = entityTypeOf<T>()
        addStatusEffectInitialization(entityType, f, true)
    }

    inline fun <reified T: StatusEffect> Initializer.modifyStatusEffect(noinline f: T.() -> Unit) {
        this@modifyStatusEffect as NonWorldInitializer
        val entityType = entityTypeOf<T>()
        addStatusEffectInitialization(entityType, f, false)
    }
}
