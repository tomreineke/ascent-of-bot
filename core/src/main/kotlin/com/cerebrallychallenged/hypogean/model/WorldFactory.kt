package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.attribute.attribute

interface WorldFactory {
    fun World.setup()
}

class WorldFactories : SimpleObjectRegistry<WorldFactory>()

var World.worldFactory: WorldFactory? by attribute(null)

fun World.setupBy(factory: WorldFactory) = with(factory) {
    worldFactory = factory
    setup()
}
