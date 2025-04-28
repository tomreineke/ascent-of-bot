package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.invoke.MethodHandle

class CloseableKey(key: Long, private val closingHandle: MethodHandle) : AutoCloseable {
    companion object {
        const val ERROR_KEY: Long = 0x1FFFFFFFFL

        // We need to use the upper and lower bits of the key to create an index with almost no collisions.
        // There still can be collisions, leading to bugs when hovering UI elements from Skia.
        fun indexFor(key: Long): Int = key.and(0xFFFFFFFF).toInt() + key.shr(32).toInt()
    }

    val key: Long = key
        get() {
            if (isClosed) {
                throw IllegalStateException("Resource $field already closed")
            }
            return field
        }


    val index: Int = indexFor(key)

    private var isClosed = false
        private set

    override fun close() {
        if (!isClosed) {
            guardedUnit {
                closingHandle(libraryPointer, key) as Byte
            }
            isClosed = true
        }
    }
}
