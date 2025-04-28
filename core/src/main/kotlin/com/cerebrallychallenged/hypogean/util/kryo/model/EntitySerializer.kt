package com.cerebrallychallenged.hypogean.util.kryo.model

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.jun.log.log
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

internal class EntitySerializer(private val world: World) : Serializer<Entity>() {
    override fun write(kryo: Kryo, output: Output, obj: Entity) {
        output.writeInt(obj.id)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out Entity>): Entity {
        log.trace { "Reading from Kryo, type: $type" }
        return world.entityById(input.readInt())
    }
}
