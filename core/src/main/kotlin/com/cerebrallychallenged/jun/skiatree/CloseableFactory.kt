package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableKey
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.invoke.MethodHandle

sealed class CloseableFactory<M, R, T>(private val create: (R) -> T) {
    operator fun invoke(block: () -> M): T = create(guarding(block))

    protected abstract fun guarding(block: () -> M): R
}

abstract class CloseableResourceFactory<T>(
    create: (CloseableResource) -> T,
    deleteMethodName: String
) : CloseableFactory<MemorySegment, CloseableResource, T>(create) {
    private val deleteHandle: MethodHandle = function(deleteMethodName, VOID, ADDRESS)

    final override fun guarding(block: () -> MemorySegment): CloseableResource = guardedResource(deleteHandle, block)
}

abstract class CloseableKeyFactory<T>(
    create: (CloseableKey) -> T,
    deleteMethodName: String
) : CloseableFactory<Long, CloseableKey, T>(create) {
    private val deleteHandle: MethodHandle = function(deleteMethodName, JAVA_BYTE, ADDRESS, JAVA_LONG)

    final override fun guarding(block: () -> Long): CloseableKey = guardedKey(deleteHandle, block)
}
