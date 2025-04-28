package com.cerebrallychallenged.hypogean.vanilla.items.chassis

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_HoverChassis

open class HoverChassis(initializer: Initializer) : Chassis(initializer) {
    init {
        name = "Hover Chassis"
        weight = 10.0f
        asset = Asset_HoverChassis
        height = 0.18f
    }
}
