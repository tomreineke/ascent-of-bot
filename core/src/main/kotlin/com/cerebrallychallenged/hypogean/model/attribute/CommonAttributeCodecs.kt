package com.cerebrallychallenged.hypogean.model.attribute

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.modding.Features
import com.cerebrallychallenged.hypogean.modding.IdRegistry
import com.cerebrallychallenged.hypogean.modding.ModContext
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.jun.asset.UnrealRef
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec3i
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.math.geo.Vec4i
import com.cerebrallychallenged.jun.math.radians
import com.cerebrallychallenged.jun.unreal.UObject
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmName

class NullableAttributeCodec<T>(private val codec: AttributeCodec<T>) : AttributeCodec<T?>

open class BiMappedAttributeCodec<S, T>(
        private val sCodec: AttributeCodec<S>,
        private val sToT: (S) -> T,
        private val tToS: (T) -> S
) : AttributeCodec<T>

internal object BooleanAttributeCodec : AttributeCodec<Boolean>

internal object IntAttributeCodec : AttributeCodec<Int>

internal object DoubleAttributeCodec : AttributeCodec<Double>

internal object FloatAttributeCodec : AttributeCodec<Float>

internal object AngleAttributeCodec : BiMappedAttributeCodec<Float, Angle>(
    FloatAttributeCodec,
    Float::radians,
    Angle::value
)

internal object StringAttributeCodec : AttributeCodec<String>

internal object ImageResourceAttributeCodec : BiMappedAttributeCodec<String, ImageResource>(StringAttributeCodec,
    ::ImageResource,
    ImageResource::resourcePath
)


internal object Vec2iAttributeCodec : AttributeCodec<Vec2i>

internal object Vec3iAttributeCodec : AttributeCodec<Vec3i>

internal object Vec4iAttributeCodec : AttributeCodec<Vec4i>

internal object Vec2fAttributeCodec : AttributeCodec<Vec2f>

internal object Vec3fAttributeCodec : AttributeCodec<Vec3f>

internal object Vec4fAttributeCodec : AttributeCodec<Vec4f>

internal object QuaternionAttributeCodec : BiMappedAttributeCodec<Vec4f, Quaternion>(
    Vec4fAttributeCodec,
    Quaternion::create,
    { it.toVec4f() }
)

internal object Transform3fAttributeCodec : AttributeCodec<Transform3f>

internal class EnumAttributeCodec<T : Enum<*>>(private val enumClass: KClass<T>) : AttributeCodec<T> {
    private val constants = enumClass.java.enumConstants
}

internal class ByteArrayAttributeCodec : AttributeCodec<ByteArray>

internal class PairAttributeCodec<T1, T2>(
        private val firstCodec: AttributeCodec<T1>,
        private val secondCodec: AttributeCodec<T2>
) : AttributeCodec<Pair<T1, T2>> {
    override fun toDebugString(value: Pair<T1, T2>): String {
        return "(${firstCodec.toDebugString(value.first)}, ${secondCodec.toDebugString(value.second)})"
    }
}

internal class ListAttributeCodec<T>(private val elementCodec: AttributeCodec<T>) : AttributeCodec<List<T>> {
    override fun toDebugString(value: List<T>): String {
        return value.joinToString(prefix = "[", postfix = "]") { elementCodec.toDebugString(it) }
    }
}

internal class SetAttributeCodec<T>(private val elementCodec: AttributeCodec<T>): AttributeCodec<Set<T>> {
    override fun toDebugString(value: Set<T>): String {
        return value.joinToString(prefix = "{", postfix = "}") { elementCodec.toDebugString(it) }
    }
}

internal class MapAttributeCodec<K, V>(
        private val keyCodec: AttributeCodec<K>,
        private val valueCodec: AttributeCodec<V>
) : AttributeCodec<Map<K, V>> {
    override fun toDebugString(value: Map<K, V>): String {
        return value.entries.joinToString(prefix = "{", postfix = "}") {
            "${keyCodec.toDebugString(it.key)} -> ${valueCodec.toDebugString(it.value)}"
        }
    }
}

open class StreamableAttributeCodec<T> : AttributeCodec<T>

internal class EntityAttributeCodec<T : Entity>(private val entityClass: KClass<T>) : AttributeCodec<T>

internal class FeatureCodecFactory(context: ModContext): (KType) -> AttributeCodec<*>? {
    private val features = context.feature<Features>()

    override fun invoke(type: KType): AttributeCodec<*>? {
        val interfaceClass = type.classifier as? KClass<*> ?: return null
        @Suppress("UNCHECKED_CAST")
        val registry =
                (features.registryForInterfaceClass(interfaceClass) ?: return null) as IdRegistry<Any>
        return object : AttributeCodec<Any> {
            override fun toString(): String = "FeatureCodec<${interfaceClass.jvmName}>"
        }
    }
}

class UnrealRefAttributeCodec<T : UObject>(_clazz: KClass<T>) : AttributeCodec<UnrealRef<T>>
