package com.cerebrallychallenged.hypogean.vanilla.items.chassis

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.weight
import com.cerebrallychallenged.hypogean.vanilla.items.Asset_WheelChassis

open class WheelChassis(initializer: Initializer) : Chassis(initializer) {
    init {
        name = "Wheel Chassis"
        weight = 10.0f
        asset = Asset_WheelChassis
        height = 0.18f
    }
}
