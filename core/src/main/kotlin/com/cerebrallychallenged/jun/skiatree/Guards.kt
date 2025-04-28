package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableKey
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

internal inline fun <T> guarded(errorValue: T, block: () -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val result = block()
    if (result == errorValue) {
        throw SkiaException(SkiaTreeApi.lastError() ?: "Unknown last error despite return value indicating error")
    } else {
        return result
    }
}

internal inline fun <T> guardedArena(errorValue: T, block: Arena.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val result = confinedArena(block)
    if (result == errorValue) {
        throw SkiaException(SkiaTreeApi.lastError() ?: "Unknown last error despite return value indicating error")
    } else {
        return result
    }
}

internal inline fun guardedPointer(block: () -> MemorySegment): MemorySegment {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return guarded(MemorySegment.NULL, block)
}

internal inline fun guardedPointerArena(block: Arena.() -> MemorySegment): MemorySegment {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return guardedArena(MemorySegment.NULL, block)
}

internal inline fun guardedResource(closer: MethodHandle, block: () -> MemorySegment): CloseableResource {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return CloseableResource(guardedPointer(block), closer)
}

internal inline fun guardedResourceArena(closer: MethodHandle, block: Arena.() -> MemorySegment): CloseableResource {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return CloseableResource(guardedPointerArena(block), closer)
}

internal inline fun guardedKey(closer: MethodHandle, block: () -> Long): CloseableKey {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return CloseableKey(guarded(CloseableKey.ERROR_KEY, block), closer)
}

internal inline fun guardedKeyArena(closer: MethodHandle, block: Arena.() -> Long): CloseableKey {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return CloseableKey(guardedArena(CloseableKey.ERROR_KEY, block), closer)
}

internal inline fun guardedUnit(block: () -> Byte) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    guarded(-1 , block)
}

internal inline fun guardedUnitArena(block: Arena.() -> Byte) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    guardedArena(-1, block)
}

internal fun guardedBool(block: () -> Byte): Boolean {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return guarded(-1, block) != 0.toByte()
}

internal fun guardedBoolArena(block: Arena.() -> Byte): Boolean {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return guardedArena(-1, block) != 0.toByte()
}

internal fun guardedIndex(block: () -> Long): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return guarded(-1, block)
}

internal fun guardedIndexArena(block: Arena.() -> Long): Long {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return guardedArena(-1, block)
}
