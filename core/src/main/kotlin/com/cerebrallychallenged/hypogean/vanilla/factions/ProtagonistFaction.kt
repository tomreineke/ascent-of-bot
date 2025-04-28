package com.cerebrallychallenged.hypogean.vanilla.factions

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.name

object ProtagonistFaction : Faction("Protagonist", {
    name = "ProtagonistFaction"
    relations = mapOf(
         DeepDrillingCorpFaction to Relation.HOSTILE,
         MerchantFaction to Relation.NEUTRAL
    )
})
