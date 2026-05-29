package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.invoke.MethodHandle

class CloseableKey(key: Long, private val closingHandle: MethodHandle) : AutoCloseable {
    companion object {
        const val ERROR_KEY: Long = 0x1FFFFFFFFL
    }

    val key: Long = key
        get() {
            if (isClosed) {
                throw IllegalStateException("Resource $field already closed")
            }
            return field
        }


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
