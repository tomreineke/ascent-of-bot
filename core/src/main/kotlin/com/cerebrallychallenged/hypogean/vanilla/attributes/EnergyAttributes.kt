package com.cerebrallychallenged.hypogean.vanilla.attributes

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.effect.MutableEffectModifiers
import com.cerebrallychallenged.jun.unreal.color.FLinearColor

/**
 * The current amount of stored energy.
 */
var Entity.energy: Int by attribute(0)

/**
 * The maximum amount of storable energy.
 */
var Entity.maxEnergy: Int by attribute(0)

object Energy : SimpleIntAttribute<Entity>(
    Entity::class,
    Entity::energy,
    Entity::maxEnergy,
    ImageResource("Images/gage/icon_energy.png"),
    FLinearColor.rgb(0.086f, 0.996f, 0.98f),
    "Energy",
    "⚡"
)

/**
 * Entity produced for actors having this entity equipped.
 */
var Entity.energyProduction: Int by attribute(0)

/**
 * Entity produced for actors standing at the location of this prop.
 */
var Entity.energyCharging: Int by attribute(0)

/**
 * Energy used by an actor or equipped item each round,
 * independent of whether or not it is a tool partaking in an action, and independent of power setting.
 */
var Entity.passiveEnergyConsumption: Int by attribute(0)

sealed class ActiveEnergyConsumption {
    data class PerAction(val value: Int) : ActiveEnergyConsumption() {
        override val isZero: Boolean
            get() = value == 0
    }

    data class PerDistance(val perMeter: Int) : ActiveEnergyConsumption() {
        override val isZero: Boolean
            get() = perMeter == 0
    }

    abstract val isZero: Boolean
}

var Entity.activeEnergyConsumption: ActiveEnergyConsumption by attribute(ActiveEnergyConsumption.PerAction(0))

interface EnergyConsumptionModifyingActionInstance {
    fun contributeModifiersTo(modifiers: MutableEffectModifiers)
}


/**
 * Computes the amount of energy consumed each round by this actor and its equipped items.
 */
fun Actor.computePassiveEnergyConsumption(): Int {
    var consumption = this.passiveEnergyConsumption
    for (item in equippedItems) {
        consumption += item.passiveEnergyConsumption
    }
    return consumption
}
