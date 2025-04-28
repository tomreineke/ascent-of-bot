package com.cerebrallychallenged.hypogean.model

import java.util.Collections

class IniQueue(private val world: World, initialIniTime: Int) {
    val slots = ArrayDeque<ArrayDeque<IniHolder>>()

    init {
        slots.addLast(ArrayDeque())
    }

    var currentIniTime: Int = initialIniTime
        private set

    private var iniHolderCount: Int = 0

    internal fun clear(initialIniTime: Int) {
        slots.clear()
        slots.add(ArrayDeque())
        this.currentIniTime = initialIniTime
        iniHolderCount = 0
    }

    private fun internalGetSlot(iniDelta: Int): ArrayDeque<IniHolder> {
        require(iniDelta >= 0) { "Cannot obtain ini slot from the past." }
        val missingCount = iniDelta - slots.size + 1
        for (i in 0 until missingCount) {
            slots.add(ArrayDeque())
        }
        return slots[iniDelta]
    }

    val slotCount: Int
        get() = slots.size

    fun slot(iniDelta: Int): Collection<IniHolder> {
        return Collections.unmodifiableCollection(internalGetSlot(iniDelta))
    }

    fun absoluteSlot(iniTime: Int): Collection<IniHolder> = slot(iniTime - currentIniTime)

    val isCurrentSlotEmpty: Boolean
        get() = slots.first().isEmpty()

    fun enqueueRelative(iniDelta: Int, holder: IniHolder) {
        enqueueAbsolute(currentIniTime + iniDelta, holder)
    }

    fun enqueueAbsolute(iniTime: Int, holder: IniHolder) {
        if (holder.isIniScheduled) {
            modelError("Ini holder $holder is already scheduled in ini queue")
        }
        internalGetSlot(iniTime - currentIniTime).add(holder)
        holder.setIniScheduled(iniTime)
        ++iniHolderCount
        world.notify(WorldChange.IniEnqueue(iniTime, holder))
    }

    fun dequeue(): IniHolder {
        val iniHolder = slots.first().removeFirst()
        iniHolder.resetIniScheduled()
        --iniHolderCount
        world.notify(WorldChange.IniDequeue(iniHolder))
        return iniHolder
    }

    fun remove(iniHolder: IniHolder) {
        val iniTime = iniHolder.scheduledIniTime
        if (iniTime == MAGIC_UNSCHEDULED) {
            return
        }
        val slot = internalGetSlot(iniTime - currentIniTime)
        val wasRemoved = slot.remove(iniHolder)
        if (!wasRemoved) {
            modelError(
                    "Tried to remove $iniHolder from ini queue at ini time $iniTime but it is not present"
            )
        }
        iniHolder.resetIniScheduled()
        --iniHolderCount
        world.notify(WorldChange.IniRemove(iniHolder))
    }

    fun incTime(): Int {
        val slot = slots.removeFirst()
        if (!slot.isEmpty()) {
            modelError("Cannot advance ini time because the current slot is not empty")
        }
        if (slots.isEmpty()) {
            slots.add(ArrayDeque())
        }
        ++currentIniTime
        world.notify(WorldChange.IniIncTime(currentIniTime))
        return currentIniTime
    }

    internal fun collectInitialChanges(listener: (WorldChange) -> Unit) {
        for (iniDelta in 0 until slots.size) {
            val iniTime = currentIniTime + iniDelta
            for (holder in slots[iniDelta]) {
                listener(WorldChange.IniEnqueue(iniTime, holder))
            }
        }
    }

    fun isEmpty(): Boolean = iniHolderCount == 0

    val actors: Sequence<Actor>
        get() = slots.asSequence().flatMap { it.asSequence() }.filterIsInstance<Actor>()
}
