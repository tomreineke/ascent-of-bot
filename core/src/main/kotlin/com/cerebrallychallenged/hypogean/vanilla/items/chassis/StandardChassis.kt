package com.cerebrallychallenged.hypogean.vanilla.items.chassis

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_TankChassis

open class StandardChassis(initializer: Initializer) : Chassis(initializer) {
    init {
        name = "Standard Chassis"
        weight = 10.0f
        asset = Asset_TankChassis
        height = 0.18f
    }
}
