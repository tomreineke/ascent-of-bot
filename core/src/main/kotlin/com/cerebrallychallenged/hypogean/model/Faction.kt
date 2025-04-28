package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry

abstract class Faction(val name: String, internal val initialize: FactionEntity.() -> Unit) {
    enum class Relation(val isSameOrAllied: Boolean) {
        SAME(true),
        ALLIED(true),
        NEUTRAL(false),
        HOSTILE(false);
    }
}

class Factions : SimpleObjectRegistry<Faction>()
