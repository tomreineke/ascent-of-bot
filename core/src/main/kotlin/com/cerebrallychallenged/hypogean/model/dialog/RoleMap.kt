package com.cerebrallychallenged.hypogean.model.dialog

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionMember

/**
 * A map from roles to entities playing that roles in a [Dialog].
 */
abstract class RoleMap {
    data class Entry<T : Entity>(val role: Dialog.Role<T>, val entity: T)

    internal abstract val entries: List<Entry<*>>

    operator fun <T : Entity> get(role: Dialog.Role<T>): T {
        val entity =
                entries.firstOrNull { it.role == role }?.entity
                        ?: throw NoSuchElementException("No entity assigned for role $role")
        @Suppress("UNCHECKED_CAST") // Safe by generic parameter of Entry.
        return entity as T
    }

    val participatingEntities: List<Entity> by lazy { entries.map { it.entity } }

    val participatingFactions: Collection<Faction> by lazy {
        participatingEntities
            .asSequence()
            .filterIsInstance<FactionMember>()
            .mapNotNullTo(mutableSetOf()) { it.faction }
    }
}

fun roleMapOf(vararg entries: RoleMap.Entry<*>): RoleMap = MutableRoleMap(entries.toMutableList())

class MutableRoleMap(override val entries: MutableList<Entry<*>> = mutableListOf()) : RoleMap() {
    fun add(entry: Entry<*>) {
        entries.removeIf { it.role == entry.role }
        entries.add(entry)
    }

    operator fun <T : Entity> set(role: Dialog.Role<T>, entity: T) {
        add(role playedBy entity)
    }
}
