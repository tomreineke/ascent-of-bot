package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.model.containment.providedBoxes
import com.cerebrallychallenged.hypogean.util.toSimpleClassString
import com.cerebrallychallenged.jun.math.geo.vec

open class Slot(initializer: Initializer) : Item(initializer) {
    init {
        name = toString()
        providedBoxes = vec(1, 1)
    }

    var slotName: String? = null
        private set

    private var _anchor: SlotBearer? = null

    /**
     * Must only be called from [SlotBearer.addSlot].
     */
    internal fun internalSetAnchor(slotName: String?, newAnchor: SlotBearer?) {
        this.slotName = slotName
        _anchor = newAnchor
    }

    override val anchor: SlotBearer?
        get() = _anchor

    final override fun toString(): String {
        return this.toSimpleClassString()
    }
}
