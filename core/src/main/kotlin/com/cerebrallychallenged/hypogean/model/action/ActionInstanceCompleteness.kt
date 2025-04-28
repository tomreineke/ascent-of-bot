package com.cerebrallychallenged.hypogean.model.action

enum class ActionInstanceCompleteness {
    Complete,
    Partial
}

fun ActionTable.compatibleInstances(completeness: ActionInstanceCompleteness): Sequence<ActionInstance> =
        instances.asSequence().filter { it.isCompatible(completeness) }

val ActionTable.completeInstances: Sequence<ActionInstance>
    get() = instances.asSequence().filter { it.canBeComplete }

val ActionTable.partialInstances: Sequence<ActionInstance>
    get() = instances.asSequence().filter { it.canBePartial }
