package com.cerebrallychallenged.hypogean.vanilla.items.chassis

import com.cerebrallychallenged.hypogean.model.Initializer
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.attributes.ActiveEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.activeEnergyConsumption
import com.cerebrallychallenged.hypogean.vanilla.attributes.cooldown
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.attributes.initiativeCost
import com.cerebrallychallenged.hypogean.vanilla.refs.Images

abstract class Chassis(initializer: Initializer) : Equipment(initializer) {
    init {
        name = "Chassis"
        icon = Images.Chassis
        cooldown = 1
        initiativeCost = 2
        activeEnergyConsumption = ActiveEnergyConsumption.PerDistance(1)
    }

    /**
     * How far the actor can move in a regular move.
     */
    var moveRange: Int by attribute(10)

    /**
     * How far the actor can move in a quick move.
     * Quick moves allow for another action as the active actor keeps its turn.
     */
    var quickMoveRange: Int by attribute(5)

    /**
     * An actor can only move with this chassis
     * if the total weight of itself and carried items does not exceed `maxTonnage`.
     */
    var maxTonnage: Int by attribute(100)
}
