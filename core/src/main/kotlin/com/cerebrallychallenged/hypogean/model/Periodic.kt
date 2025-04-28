package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock

var Entity.periodics: List<Periodic> by attribute(listOf())

interface Periodic {
    context(CascadeBlock)
    suspend fun execute(bearer: Entity)
}

class Periodics : SimpleObjectRegistry<Periodic>()
