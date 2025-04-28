package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.model.cascade.CascadeContext
import com.cerebrallychallenged.hypogean.util.collections.WorldStatistic

abstract class ActionInstance(val action: Action, val activeActor: Actor) : WorldContext {
    internal lateinit var id: ActionInstanceId

    internal var children: ActionTable? = null

    abstract val equipment: Item

    abstract val target: Entity

    final override val world: World
        get() = activeActor.world

    open val mode: ActionMode
        get() = DefaultMode

    abstract val initiativeCost: InitiativeCost

    open val canBeComplete: Boolean
        get() = true

    open val canBePartial: Boolean
        get() = false

    val estimatedConsequences: WorldStatistic<IntProperty>? by lazy { estimateConsequences() }

    open fun estimateConsequences(): WorldStatistic<IntProperty>? = null

    context(CascadeContext)
    open suspend fun execute() {}

    internal fun expand(): ActionTable = children ?: MutableActionTable(this).also {
        it.createChildren(activeActor)
        children = it
    }

    protected open fun MutableActionTable.createChildren(activeActor: Actor) {}

    override fun toString(): String = "ActionInstance[$action, equipment=$equipment, target=$target, mode=$mode]"
}

fun ActionInstance.isCompatible(completeness: ActionInstanceCompleteness): Boolean = when (completeness) {
    ActionInstanceCompleteness.Complete -> canBeComplete
    ActionInstanceCompleteness.Partial -> canBePartial
}
