package com.cerebrallychallenged.hypogean.util.kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.SerializerFactory

internal class ObjectSerializerFactory : SerializerFactory<ConstantSerializer<*>> {
    override fun newSerializer(kryo: Kryo, type: Class<*>): ConstantSerializer<*> =
        ConstantSerializer(requireNotNull(type.kotlin.objectInstance))

    override fun isSupported(type: Class<*>): Boolean = try {
        type.kotlin.objectInstance != null
    } catch (e: Throwable) {
        false
    }
}
