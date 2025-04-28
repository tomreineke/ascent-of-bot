package com.cerebrallychallenged.hypogean.view.util

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.vanilla.attributes.numberWeaponSlots
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec

inline fun <T: Any> layoutGrid(
    rowCount: Int,
    columnCount: Int,
    rowOffset: Int,
    columnOffset: Int,
    values: Array<T?>,
    block: (position: Vec2i, value: T) -> Unit
) {
    require(values.size == rowCount * columnCount)
    var index = 0
    for (row in 0 until rowCount) {
        for (column in 0 until columnCount) {
            values[index]?.let { block(vec(column * columnOffset, row * rowOffset), it) }
            ++index
        }
    }
}

fun layoutInventorySlots(actor: Actor): Array<String?> {
    val slots = actor.numberWeaponSlots

    /**
     * @param slots number of weapon slots of the actor
     * @param requiredSlots number of required slots to actually display slot with slotId
     */
    fun addSlot(slots: Int?, requiredSlots: Int, slotId: String): String? {
        return if(slots != null && slots >= requiredSlots) slotId else null
    }

    return arrayOf(
        addSlot(slots, 5, "left_shoulder"), "head", addSlot(slots, 6, "right_shoulder"),
        addSlot(slots, 1, "left_arm"), "torso", addSlot(slots, 2, "right_arm"),
        addSlot(slots, 3, "left_foot"), "chassis", addSlot(slots, 4, "right_foot")
    )
}
