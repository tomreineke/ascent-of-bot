package com.cerebrallychallenged.hypogean.util.kryo

import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.SerializerFactory

abstract class SubclassSerializerFactory<T>(protected val baseClass: Class<*>) : SerializerFactory<Serializer<out T>> {
    override fun isSupported(type: Class<*>): Boolean = baseClass.isAssignableFrom(type)
}
