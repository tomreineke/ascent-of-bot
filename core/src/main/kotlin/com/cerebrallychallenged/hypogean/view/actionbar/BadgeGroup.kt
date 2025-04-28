package com.cerebrallychallenged.hypogean.view.actionbar

class BadgeGroup<T : Any>(val id: String, val values: Map<T, String>) {
    companion object {
        operator fun invoke(id: String): BadgeGroup<Unit> =
            BadgeGroup(id, Unit to "")

        operator fun <T : Any> invoke(id: String, vararg values: T): BadgeGroup<T> =
            BadgeGroup(id, values.associateWith { it.toString() })

        operator fun <T : Any> invoke(id: String, vararg pairs: Pair<T, String>): BadgeGroup<T> =
            BadgeGroup(id, pairs.toMap())
    }

    inner class Entry(val value: T) {
        operator fun component1(): String = id
        operator fun component2(): String = values.getValue(value)
    }

    infix fun to(value: T): Entry = Entry(value)
}
