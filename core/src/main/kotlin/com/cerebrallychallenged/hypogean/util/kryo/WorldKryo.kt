package com.cerebrallychallenged.hypogean.util.kryo

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.util.kryo.model.AttributeSerializer
import com.cerebrallychallenged.hypogean.util.kryo.model.DialogNodeSerializer
import com.cerebrallychallenged.hypogean.util.kryo.model.EntitySerializer
import com.cerebrallychallenged.hypogean.util.kryo.model.EntityTypeSerializer
import com.cerebrallychallenged.hypogean.util.kryo.model.KClassSerializer
import com.cerebrallychallenged.hypogean.util.kryo.model.RulebookSerializer
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.esotericsoftware.kryo.serializers.FieldSerializer
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy
import kotlinx.coroutines.CoroutineDispatcher
import org.objenesis.strategy.StdInstantiatorStrategy
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

private val FieldSerializerConf = FieldSerializer.FieldSerializerConfig().apply {
    ignoreSyntheticFields = false
    serializeTransient = true
}

fun CoroutineContext.findDispatcher(): CoroutineDispatcher =
    requireNotNull(fold<CoroutineDispatcher?>(null) { old, new -> old ?: new as? CoroutineDispatcher })

class WorldKryo(world: World) : Kryo() {
    companion object {
        private val BaseContinuationImplClass: Class<*>

        private val DispatchedContinuationClass: Class<*>

        private val SafeContinuationClass: Class<*>

        private val StackTraceFrameClass: Class<*>

        private val StackTraceElementClass: Class<*>

        init {
            with(WorldKryo::class.java.classLoader) {
                BaseContinuationImplClass = loadClass("kotlin.coroutines.jvm.internal.BaseContinuationImpl")

                DispatchedContinuationClass = loadClass("kotlinx.coroutines.internal.DispatchedContinuation")

                SafeContinuationClass = loadClass("kotlin.coroutines.SafeContinuation")

                StackTraceFrameClass = loadClass("kotlinx.coroutines.debug.internal.StackTraceFrame")

                StackTraceElementClass = loadClass("java.lang.StackTraceElement")
            }
        }
    }

    object CoroutineContextKey

    inner class SubstitutionSerializer<T>(
        private val baseClass: Class<T>,
        private val key: Any = baseClass,
    ) : Serializer<T>() {
        override fun write(kryo: Kryo, output: Output, obj: T) {}

        override fun read(kryo: Kryo, input: Input, type: Class<out T>): T {
            return substitutions[key]?.let { baseClass.cast(it) }
                ?: error("No substitutions available for $key")
        }
    }

    private val substitutions = mutableMapOf<Any, Any>()

    init {
        isRegistrationRequired = false
        references = true
        setOptimizedGenerics(false)
        instantiatorStrategy = DefaultInstantiatorStrategy(StdInstantiatorStrategy())
        setDefaultSerializer(SerializerFactory.FieldSerializerFactory(FieldSerializerConf))
        addDefaultSerializer(ObjectSerializerFactory())
        registerBase(KClassSerializer())
        registerBase(RulebookSerializer(world))
        registerBase(EntitySerializer(world))
        registerBase(EntityTypeSerializer(world))
        registerBase(AttributeSerializer(world))
        registerBase(DialogNodeSerializer())
        register(SafeContinuationClass) {
            getField("result").serializer = ConstantSerializer(COROUTINE_SUSPENDED)
        }
        register(DispatchedContinuationClass) {}
        registerBase(BaseContinuationImplClass) {
            getField("_context").serializer =
                SubstitutionSerializer(CoroutineContext::class.java, CoroutineContextKey)
        }
        registerSubstitutedClass<CoroutineDispatcher>()
        registerBase(WeakReference::class.java, ConstantSerializer(WeakReference(null)))
        registerSubstitutedClass<Thread>()
        register(StackTraceFrameClass) {}
        register(StackTraceElementClass) {}
    }

    private fun <T : Any> fieldSerializer(type: Class<*>): FieldSerializer<T> =
        FieldSerializer<T>(this, type, FieldSerializerConf)

    inline fun <reified T> addDefaultSerializer(factory: SerializerFactory<out Serializer<out T>>) {
        addDefaultSerializer(T::class.java, factory)
    }

    fun <T : Any> register(type: Class<T>, block: FieldSerializer<T>.() -> Unit) {
        register(type, fieldSerializer<T>(type).apply(block))
    }

    fun <T : Any> registerBase(type: Class<T>, block: FieldSerializer<T>.() -> Unit) {
        addDefaultSerializer(type, object : SubclassSerializerFactory<T>(type) {
            override fun newSerializer(kryo: Kryo, tpe: Class<*>): Serializer<out T> =
                fieldSerializer<T>(tpe).apply(block)
        })
    }

    inline fun <reified T : Any> registerBase(serializer: Serializer<T>) {
        registerBase(T::class.java, serializer)
    }

    fun <T : Any> registerBase(type: Class<T>, serializer: Serializer<T>) {
        addDefaultSerializer(type, object : SubclassSerializerFactory<T>(type) {
            override fun newSerializer(kryo: Kryo, type: Class<*>): Serializer<out T> = serializer
        })
    }

    inline fun <reified T : Any> registerSubstitutedClass() {
        registerSubstitutedClass(T::class.java)
    }

    fun <T : Any> registerSubstitutedClass(baseClass: Class<T>, key: Any = baseClass) {
        registerBase(baseClass, SubstitutionSerializer(baseClass, key))
    }

    fun serializeToByteArray(obj: Any): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val output = Output(outputStream)
        writeClassAndObject(output, obj)
        output.close()
        return outputStream.toByteArray()
    }

    inline fun <reified T> deserializeFromByteArray(byteArray: ByteArray): T =
        readClassAndObject(Input(byteArray)) as T

    inline fun <reified T : Any> putSubstitution(value: T) {
        putSubstitution(T::class.java, value)
    }

    fun putSubstitution(key: Any, value: Any) {
        substitutions[key] = value
    }
}
