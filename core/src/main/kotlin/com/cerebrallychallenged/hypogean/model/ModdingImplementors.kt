package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.jun.util.reflect.safeObjectInstance
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

sealed class ModdingInterfaceRegistry {

    /**
     * All interfaces annotated by `@ModdingApi`.
     */
    abstract val moddingInterfaces: List<KClass<*>>

    abstract fun <T : Any> implementorsFor(moddingInterface: KClass<T>): ModdingImplementors<T>
}

/**
 * Knows all objects in classpath implementing a specific interface annotated by `@ModdingApi`.
 */
class ModdingImplementors<T : Any>(private val moddingInterface: KClass<T>) {
    private val objectById = mutableMapOf<String, T>()

    private val idByObject = mutableMapOf<T, String>()

    private val idByClass = mutableMapOf<Class<*>, String>()

    fun put(id: String, obj: Any) {
        // Safe as MutableModdingImplementors.process adds only objects checked by isSubclassOf,
        // i.e., only instances of the right interface.
        @Suppress("UNCHECKED_CAST")
        obj as T
        objectById[id] = obj
        idByObject[obj] = id
        idByClass[obj.javaClass] = id
    }

    val objects: Collection<T>
        get() = objectById.values

    fun objectForId(id: String): T
            = objectById[id]
            ?: modelError("No object with id $id known for interface $moddingInterface")

    fun idForObject(obj: T): String
            = idByObject[obj]
            ?: idByClass[obj.javaClass]
            ?: modelError("Unknown object $obj for interface $moddingInterface")
}

internal class MutableModdingInterfaceRegistry(override val moddingInterfaces: List<KClass<*>>) : ModdingInterfaceRegistry() {
    private val implementorsByClass: Map<KClass<out Any>, ModdingImplementors<out Any>>
            = moddingInterfaces.associateWith { ModdingImplementors(it) }

    private fun constructImplementor(clazz: Class<*>): Any? =
            clazz.declaredConstructors.firstOrNull { it.parameters.isEmpty() }?.let { constructor ->
                try {
                    constructor.isAccessible = true
                    constructor.newInstance()
                } catch (_: Throwable) {
                    null
                }
            }

    fun process(clazz: KClass<*>) {
        // Associates a String ID with a Kotlin singleton (object) that implements an interface
        // which is annotated with @ModdingApi, and vice versa. See ModdingImplementors members
        // objectById and idByObject.
        val id = clazz.jvmName
        for ((moddingInterface, implementors) in implementorsByClass) {
            if (clazz.isSubclassOf(moddingInterface)) {
                val objectInstance = clazz.safeObjectInstance()
                when {
                    objectInstance != null -> implementors.put(id, objectInstance)
                    clazz.isFinal -> {
                        constructImplementor(clazz.java)?.let {
                            implementors.put(id, it)
                        }
                    }
                }
            }
        }
    }

    fun processSyntheticClass(clazz: Class<*>) {
        val id = clazz.name
        for ((moddingInterface, implementors) in implementorsByClass) {
            if (moddingInterface.java.isAssignableFrom(clazz)) {
                constructImplementor(clazz)?.let {
                    implementors.put(id, it)
                }
            }
        }
    }

    override fun <T : Any> implementorsFor(moddingInterface: KClass<T>): ModdingImplementors<T> {
        // Safe as guaranteed by runtime reflection.
        @Suppress("UNCHECKED_CAST")
        // Instead of a ClassCastException we want to throw an intelligent exception. To do this we get null
        // if the cast fails (as?) and in case of null we throw a NoSuchElementException.
        return implementorsByClass[moddingInterface] as? ModdingImplementors<T>
                ?: throw NoSuchElementException("Class $moddingInterface is no interface tracked by @ModdingApi")
    }
}
