package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.model.action.nearCells
import com.cerebrallychallenged.hypogean.model.attribute.attribute
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.containment.transitivelyContainedItems
import com.cerebrallychallenged.hypogean.model.effect.AreaEffect
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.areaEffect
import com.cerebrallychallenged.hypogean.model.effect.directEffect
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.jun.math.geo.Vec2f

open class StatusEffect(initializer: Initializer) : NonWorldEntity(initializer) {
    val bearer: Entity = (initializer as StatusEffectInitializer).bearer

    /**
     * Time when this StatusEffect has been created.
     */
    val creationTime: Int = world.currentIniTime

    /**
     * Ini time duration after which that status effect will be removed by StatusEffectPeriodic.
     * A value of `null` indicates that the effect has unlimited duration.
     */
    var duration: Int? by attribute(null)

    var triggerRange: Float? by attribute(null)

    override fun remove() {
        (bearer as AbstractEntity).internalRemoveStatusEffect(this)
        unregisterAtCells()
        super.remove()
    }

    override fun onAttributeChanged(change: WorldChange.AttributeChanged<*>) {
        super.onAttributeChanged(change)
        change.ifOf(StatusEffect::triggerRange) { (_, _, value, prevValue) ->
            prevValue?.let { unregisterAtCells(rangeOverride = it) }
            value?.let { registerAtCells(rangeOverride = it) }
        }
    }

    open fun isTriggeredBy(triggeringActor: Actor): Boolean = false

    context(CascadeBlock)
    open suspend fun executeTrigger(triggeringActor: Actor) {}

    private fun nearCells(rangeOverride: Float?, positionOverride: Vec2f?): Sequence<Cell> {
        val actualRangeOverride = rangeOverride ?: triggerRange ?: return sequenceOf()
        return (bearer as? LocatedEntity)?.nearCells(actualRangeOverride, positionOverride) ?: sequenceOf()
    }

    internal fun registerAtCells(rangeOverride: Float? = null, positionOverride: Vec2f? = null) {
        for (cell in nearCells(rangeOverride, positionOverride)) {
            cell._nearStatusEffects.add(this)
        }
    }

    internal fun unregisterAtCells(rangeOverride: Float? = null, positionOverride: Vec2f? = null) {
        for (cell in nearCells(rangeOverride, positionOverride)) {
            cell._nearStatusEffects.remove(this)
        }
    }
}

class StatusEffectInitializationHelper {
    var name: String? = null

    var icon: ImageResource? = null

    var directEffect: Effect? = null

    var areaEffect: AreaEffect? = null

    internal fun applyOn(statusEffect: StatusEffect) {
        name?.let { statusEffect.name = it }
        icon?.let { statusEffect.icon = it }
        directEffect?.let { statusEffect.directEffect = it }
        areaEffect?.let { statusEffect.areaEffect = it }
    }
}

abstract class StatusEffectCompanion(
    private val factory: (Initializer) -> StatusEffect,
    private val initialize: StatusEffectInitializationHelper.(Int) -> Unit
) {
    internal fun createFor(bearer: Entity): StatusEffect = bearer.createStatusEffect(factory)

    internal fun createEffectContainer(intensity: Int): StatusEffectInitializationHelper =
        StatusEffectInitializationHelper().apply { initialize(intensity) }
}

val Entity.transitivelyCarriedStatusEffects: Sequence<StatusEffect>
    get() = sequence {
        if (this@transitivelyCarriedStatusEffects is StatusEffect) {
            yield(this@transitivelyCarriedStatusEffects)
        }
        for (statusEffect in statusEffects) {
            yieldAll(statusEffect.transitivelyCarriedStatusEffects)
        }
        if (this@transitivelyCarriedStatusEffects is SlotBearer) {
            for (item in transitivelyContainedItems) {
                yieldAll(item.transitivelyCarriedStatusEffects)
            }
        }
    }
