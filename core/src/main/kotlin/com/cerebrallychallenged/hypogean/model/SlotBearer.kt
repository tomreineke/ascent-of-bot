package com.cerebrallychallenged.hypogean.model

abstract class SlotBearer(initializer: Initializer) : NonWorldEntity(initializer) {
    private val _slots: MutableMap<String, Slot> = linkedMapOf()

    val slots: Collection<Slot>
        get() = _slots.values

    override fun remove() {
        for (item in _slots.values.toList()) {
            if (item.isAlive) {
                item.remove()
            }
        }
        super.remove()
    }

    /**
     * Adds the specified slot to this anchor.
     * Must only be called from [WorldUpdater.onSlotAdded] (for dependent worlds)
     * or [SlotBearerInitializer] (for primary world).
     */
    internal fun addSlot(slotId: String, slot: Slot) {
        _slots[slotId] = slot
        slot.internalSetAnchor(slotId, this)
        world.notify(WorldChange.SlotAdded(this, slotId, slot))
    }

    fun slotOrNull(slotName: String): Slot? = _slots[slotName]

    fun slot(slotName: String): Slot =
        slotOrNull(slotName) ?: modelError("No slot found with name $slotName in $this")

    override fun collectInitialChanges(collector: (WorldChange) -> Unit) {
        super.collectInitialChanges(collector)
        for ((slotName, slot) in _slots) {
            collector(WorldChange.SlotAdded(this, slotName, slot))
        }
    }

    /**
     * Called by constructors of this slot bearer to define its slots.
     */
    protected inline fun <reified T : Slot> Initializer.defineSlot(slotId: String, noinline slotInitializer: T.() -> Unit) {
        this@defineSlot as SlotBearerInitializer
        val slotType = entityTypeOf<T>()
        addSlotDefinition(slotType, slotId, slotInitializer)
    }
}
