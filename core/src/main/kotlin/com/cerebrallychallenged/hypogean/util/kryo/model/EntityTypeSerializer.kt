package com.cerebrallychallenged.hypogean.util.kryo.model

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.EntityType
import com.cerebrallychallenged.hypogean.model.EntityTypes
import com.cerebrallychallenged.hypogean.model.World
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

internal class EntityTypeSerializer(world: World) : Serializer<EntityType<*>>() {
    private val entityTypes: EntityTypes = world.rulebook.entityTypes

    override fun write(kryo: Kryo, output: Output, obj: EntityType<*>) {
        output.writeString(obj.id)
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out EntityType<*>>): EntityType<*> {
        return entityTypes.get<Entity>(input.readString())
    }
}
