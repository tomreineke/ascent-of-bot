package com.cerebrallychallenged.hypogean.util.kryo.model

import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

class AttributeSerializer(world: World): Serializer<Attribute<*>>() {
    val attributes = world.rulebook.attributes

    override fun read(kryo: Kryo, input: Input, type: Class<out Attribute<*>>): Attribute<*> {
        return attributes[input.readInt()]
    }

    override fun write(kryo: Kryo, output: Output, obj: Attribute<*>) {
        output.writeInt(obj.id)
    }
}
