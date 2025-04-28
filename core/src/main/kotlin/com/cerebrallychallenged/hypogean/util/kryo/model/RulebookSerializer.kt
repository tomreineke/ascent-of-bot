package com.cerebrallychallenged.hypogean.util.kryo.model

import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.World
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

internal class RulebookSerializer(world: World) : Serializer<Rulebook>() {
    private val rulebook = world.rulebook

    override fun write(kryo: Kryo, output: Output, obj: Rulebook) {}

    override fun read(kryo: Kryo, input: Input, type: Class<out Rulebook>): Rulebook = rulebook
}
