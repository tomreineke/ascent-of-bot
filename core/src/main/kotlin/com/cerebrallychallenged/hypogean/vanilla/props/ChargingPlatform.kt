package com.cerebrallychallenged.hypogean.vanilla.props

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.base.Prop
import com.cerebrallychallenged.hypogean.model.height
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.asset
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.attributes.energyCharging
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.rays.BlockingValue
import com.cerebrallychallenged.hypogean.vanilla.rays.ballisticBlocking
import com.cerebrallychallenged.hypogean.vanilla.refs.Images

open class ChargingPlatform(initializer: Initializer) : Prop(initializer) {
    init {
        name = "Charging Platform"
        description = "Going onto a charging panel or skipping your turn on a charging panel will restore energy"
        asset = Asset_ChargingPlatform
        icon = Images.PortraitChargingPlatform
        health = 20
        energyCharging = 20
        height = 0.2f
        ballisticBlocking = BlockingValue { 1.0f }
    }
}