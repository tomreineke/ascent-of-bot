package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.JunWeakReference
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle

class CloseableResource(address: MemorySegment, private val closingHandle: MethodHandle) : AutoCloseable {
    val address: MemorySegment = address
        get() {
            if (isClosed) {
                throw IllegalStateException("Resource ${field.address()} already closed")
            }
            return field
        }

    var isClosed = false
        private set

    override fun close() {
        if (!isClosed) {
//            JunManager.LOGGER.warn { "CLOSING RESOURCE!" }
            closingHandle(address)
            isClosed = true
        }
    }
}

fun Any.createWeakReference(resource: CloseableResource) {
    JunWeakReference(this) {
        resource.close()
    }
}
