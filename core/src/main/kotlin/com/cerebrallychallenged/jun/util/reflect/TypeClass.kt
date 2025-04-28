package com.cerebrallychallenged.jun.util.reflect

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import kotlin.reflect.*

open class TypeClass<T : Any>(val interfaceClass: KClass<T>) {
    companion object {
        inline operator fun <reified T : Any> invoke(): TypeClass<T> = TypeClass(T::class)
    }

    data class Entry<T>(val type: KType, val factory: Factory<T>)

    sealed class Factory<T> {
        data class Singleton<T>(val instance: T) : Factory<T>()
        data class Constructed<T>(
                val typeParameters: List<KTypeParameter>,
                val constructorParameters: List<ConstructorParameter>,
                val constructor: KFunction<T>
        ) : Factory<T>()
    }

    sealed class ConstructorParameter {
        data class KClassParameter(val index: Int) : ConstructorParameter()
        data class RecursiveParameter(val index: Int) : ConstructorParameter()
        data class ContextParameter(val context: Any) : ConstructorParameter()
    }

    private val contextObjects = mutableListOf<Any>()

    private val entries = mutableListOf<Entry<T>>()

    private val factories = ArrayDeque<(KType) -> T?>()

    fun addContextObject(context: Any) {
        contextObjects.add(context)
    }

    inline fun <reified U : T> add() {
        add(U::class)
    }

    @PublishedApi
    internal fun add(clazz: KClass<out T>) {
        val type = clazz.resolveTypeParameter(interfaceClass)
        val typeParameters = type.deepParameters().distinct().toList()

        clazz.objectInstance?.let { singleton ->
            entries.add(Entry(type, Factory.Singleton(singleton)))
        }

        clazz.constructors.forEach { constructor ->
            val constructorParameters = constructor.parameters.map { parameter ->
                val parameterType = parameter.type
                fun KType.extractTypeParameter(): KTypeParameter? =
                        arguments.single().type?.classifier as? KTypeParameter
                when (val classifier = parameterType.classifier) {
                    KClass::class -> {
                        val index = typeParameters.indexOf(parameterType.extractTypeParameter())
                        if (index == -1) return@forEach
                        ConstructorParameter.KClassParameter(index)
                    }
                    interfaceClass -> {
                        val index = typeParameters.indexOf(parameterType.extractTypeParameter())
                        if (index == -1) return@forEach
                        ConstructorParameter.RecursiveParameter(index)
                    }
                    else -> {
                        val contextObject = contextObjects.find { it::class == classifier } ?: return@forEach
                        ConstructorParameter.ContextParameter(contextObject)
                    }
                }
            }
            entries.add(Entry(type, Factory.Constructed(typeParameters, constructorParameters, constructor)))
        }
    }

    fun add(type: KType, value: T) {
        entries.add(Entry(type, Factory.Singleton(value)))
    }

    fun addFactory(factory: (KType) -> T?) {
        factories.addFirst(factory)
    }

    inline fun <reified U : T> getForAppliedType(): U? = getForAppliedType(typeOf<U>()) as U?

    @PublishedApi
    internal fun getForAppliedType(appliedType: KType): T? {
        require(appliedType.classifier == interfaceClass)
        return get(requireNotNull(appliedType.arguments.single().type))
    }

    open fun get(requestedType: KType): T? {
        for ((type, factory) in entries) {
            type.match(requestedType)?.let { match ->
                return when (factory) {
                    is Factory.Singleton -> {
                        factory.instance
                    }
                    is Factory.Constructed -> {
                        val typeByParameter = Object2ObjectArrayMap<KTypeParameter, KType>()
                        for ((param, associatedType) in match) {
                            val prev = typeByParameter.put(param, associatedType)
                            if (prev != null && prev != associatedType) return@let
                        }
                        val (
                                typeParameters,
                                constructorParameters,
                                constructor
                        ) = factory
                        val args = constructorParameters.map { param ->
                            when (param) {
                                is ConstructorParameter.KClassParameter -> {
                                    val tpe = typeByParameter[typeParameters[param.index]] ?: return@let
                                    (tpe.classifier as? KClass<*>) ?: return@let
                                }
                                is ConstructorParameter.RecursiveParameter -> {
                                    val tpe = typeByParameter[typeParameters[param.index]] ?: return@let
                                    get(tpe) ?: return@let
                                }
                                is ConstructorParameter.ContextParameter -> {
                                    param.context
                                }
                            }
                        }
                        constructor.call(*args.toTypedArray())
                    }
                }
            }
        }
        for (factory in factories) {
            factory(requestedType)?.let { return it }
        }
        return null
    }
}

class CachingTypeClass<T : Any>(interfaceClass: KClass<T>) : TypeClass<T>(interfaceClass) {
    private val cachedCodecs: ThreadLocal<MutableMap<KType, T>> = ThreadLocal.withInitial(::mutableMapOf)

    override fun get(requestedType: KType): T? {
        return cachedCodecs.get().getOrPut(requestedType) {
            super.get(requestedType) ?: return null
        }
    }
}

private fun KType.deepParameters(): Sequence<KTypeParameter> = sequence {
    when (val classifier = classifier) {
        is KClass<*> -> {
            for (argument in arguments) {
                argument.type?.let { type -> yieldAll(type.deepParameters()) }
            }
        }
        is KTypeParameter -> {
            yield(classifier)
        }
    }
}
