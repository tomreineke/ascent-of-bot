package com.cerebrallychallenged.hypogean.model.action

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import com.cerebrallychallenged.hypogean.model.attribute.attribute

var Actor.actionHistory: List<ActionHistoryEntry> by attribute(listOf())

/**
 * Each entry corresponds to an action executed in the past.
 */
data class ActionHistoryEntry(val iniTime: Int, val action: Action, val tool: Item, val target: Entity)

internal object ActionHistoryEntryAttributeCodec : AttributeCodec<ActionHistoryEntry>
