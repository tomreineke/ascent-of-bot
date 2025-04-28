package com.cerebrallychallenged.hypogean.vanilla.factions

import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.name

object MerchantFaction : Faction("Merchant Guild", {
    name = "ProtagonistFaction"
    ProtagonistFaction to Relation.NEUTRAL
})