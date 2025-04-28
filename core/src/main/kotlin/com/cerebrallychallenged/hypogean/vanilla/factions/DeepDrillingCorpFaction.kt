package com.cerebrallychallenged.hypogean.vanilla.factions

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.name

object DeepDrillingCorpFaction : Faction("Deep Drilling Corp", {
    name = "DeepDrillingCorpFaction"
    relations = mapOf(ProtagonistFaction to Relation.HOSTILE)
})
