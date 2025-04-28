package com.cerebrallychallenged.hypogean.util.kryo.model

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import kotlin.reflect.KClass

internal class KClassSerializer : Serializer<KClass<*>>() {
    override fun write(kryo: Kryo, output: Output, obj: KClass<*>) {
        output.writeString(obj.java.canonicalName)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out KClass<*>>): KClass<*> {
        return Class.forName(input.readString()).kotlin
    }
}
