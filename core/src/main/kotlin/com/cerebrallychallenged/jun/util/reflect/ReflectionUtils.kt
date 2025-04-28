@file:Suppress("UnstableApiUsage")

package com.cerebrallychallenged.jun.util.reflect

import java.lang.reflect.Field
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVariance
import kotlin.reflect.full.*
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind

/**
 * Casts this [KClass] object to represent a subclass of the specified type T.
 *
 * @return this [KClass] object cast to represent a subclass of the specified type T or null if the represented class
 *          is no subtype of T.
 * @see Class.asSubclass
 */
inline fun <reified T : Any> KClass<*>.asSubclassOf(): KClass<out T>? {
    return if (this.isSubclassOf(T::class)) {
        @Suppress("UNCHECKED_CAST")
        this as KClass<out T>
    } else {
        null
    }
}

/**
 * Provides an alternative implementation to [KClass.objectInstance] as a workaround to
 * https://youtrack.jetbrains.com/issue/KT-22792
 */
fun <T : Any> KClass<T>.safeObjectInstance(): T? {
    val lazyData = invokeDynamic("getData") as Any
    val data = lazyData.invokeDynamic("invoke") as Any
    val descriptor = data.invokeDynamic("getDescriptor") as ClassDescriptor
    if (descriptor.kind != ClassKind.OBJECT) return null
    val field = if (descriptor.isCompanionObject) {
        java.enclosingClass.getDeclaredField(descriptor.name.asString())
    } else {
        java.getDeclaredField("INSTANCE")
    }
    field.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    return field.get(null) as T
}

fun KClass<*>.safeCompanionObjectInstance(): Any? = companionObject?.safeObjectInstance()

fun KClass<*>.isCompatible(other: KClass<*>, variance: KVariance): Boolean = when (variance) {
    KVariance.OUT -> isSuperclassOf(other)
    KVariance.IN -> isSubclassOf(other)
    KVariance.INVARIANT -> this == other
}

fun KType.match(concreteType: KType): List<Pair<KTypeParameter, KType>>? {
    val concreteClass = concreteType.classifier as? KClass<*> ?: return null
    return when (val classifier = classifier ?: return null) {
        is KTypeParameter -> {
            if (classifier.upperBounds.any { !it.isSupertypeOf(concreteType) }) return null
            if (isMarkedNullable) {
                if (!concreteType.isMarkedNullable) return null
                listOf(classifier to concreteType.withNullability(false))
            } else {
                listOf(classifier to concreteType)
            }
        }
        is KClass<*> -> {
            if (classifier != concreteClass || isMarkedNullable != concreteType.isMarkedNullable) return null
            arguments.zip(concreteType.arguments).flatMap { (ownProjection, concreteProjection) ->
                val (ownVariance, ownProjectionType) = ownProjection
                val (concreteVariance, concreteProjectionType) = concreteProjection
                if (ownProjectionType == null || concreteProjectionType == null) return null
                if (ownVariance != concreteVariance) return null
                ownProjectionType.match(concreteProjectionType) ?: return null
            }
        }
        else -> null
    }
}

fun KClass<*>.resolveTypeParameters(requestedSuperClass: KClass<*>): List<KType?> = requireNotNull(
    allSupertypes.find { it.classifier == requestedSuperClass }?.arguments?.map { it.type }
) { "Cannot resolve generic parameters of $this for $requestedSuperClass" }

fun KClass<*>.resolveTypeParameter(requestedSuperClass: KClass<*>): KType =
    requireNotNull(resolveTypeParameters(requestedSuperClass).singleOrNull())

val KClass<*>.primitiveJavaClass: Class<*> get() = when (this) {
    Int::class -> Int::class.java
    Float::class -> Float::class.java
    Double::class -> Double::class.java
    Long::class -> Long::class.java
    Byte::class -> Byte::class.java
    Short::class -> Short::class.java
    else -> java
}

private fun Class<*>.allSuperclasses(): Sequence<Class<*>> = generateSequence(this, Class<*>::getSuperclass)

private fun Class<*>.allDeclaredFields(): Sequence<Field> = allSuperclasses().flatMap { it.declaredFields.asSequence() }

fun <T : Any> T.getFieldContent(fieldName: String): Any? {
    val field = this::class.java.allDeclaredFields().firstOrNull { it.name == fieldName } ?: error("Cannot find field $fieldName")
    field.isAccessible = true
    return field.get(this)
}
