package com.cerebrallychallenged.hypogean.util.kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

internal class ConstantSerializer<T>(val constant: T) : Serializer<T>() {
    override fun write(kryo: Kryo, output: Output, obj: T) {}

    override fun read(kryo: Kryo, input: Input, type: Class<out T>): T = constant
}
