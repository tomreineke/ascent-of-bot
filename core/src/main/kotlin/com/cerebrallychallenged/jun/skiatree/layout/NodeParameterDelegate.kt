package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import java.lang.foreign.MemoryLayout
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.invoke.MethodHandle

abstract class NodeParameterDelegate(private val elementLayout: MemoryLayout) {
    protected fun createGetter(functionName: String): MethodHandle = function(
        functionName,
        elementLayout,
        ADDRESS,
        JAVA_LONG
    )

    protected fun createSetter(functionName: String): MethodHandle = function(
        functionName,
        JAVA_BYTE,
        ADDRESS,
        JAVA_LONG,
        elementLayout
    )
}
