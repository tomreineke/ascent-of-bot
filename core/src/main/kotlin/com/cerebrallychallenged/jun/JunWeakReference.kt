package com.cerebrallychallenged.jun

import java.lang.ref.WeakReference

private var head: JunWeakReference? = null

class JunWeakReference(
        obj: Any,
        private val closer: () -> Unit
) : WeakReference<Any>(obj, JunManager.garbageCollectedObjects) {
    private var prev: JunWeakReference? = null

    private var next: JunWeakReference?

    init {
        val oldHead = head
        next = oldHead
        oldHead?.prev = this
        head = this
    }

    override fun clear() {
        super.clear()
        prev?.next = next
        next?.prev = prev
        prev = null
        next = null
        if (this == head) {
            head = null
        }
    }

    fun close() {
        clear()
        closer()
    }
}
