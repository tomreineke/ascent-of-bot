package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import java.lang.foreign.MemoryLayout
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.invoke.MethodHandle

abstract class IndexedParameterDelegate(private val signatureElement: MemoryLayout) {
    protected fun createGetter(functionName: String): MethodHandle = function(
        functionName,
        signatureElement,
        ADDRESS,
        JAVA_LONG,
        JAVA_INT
    )

    protected fun createSetter(functionName: String): MethodHandle = function(
        functionName,
        JAVA_BYTE,
        ADDRESS,
        JAVA_LONG,
        JAVA_INT,
        signatureElement
    )
}
