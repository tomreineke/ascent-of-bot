package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.input.Key
import com.cerebrallychallenged.jun.skiatree.input.InputUpCalls
import java.lang.foreign.Arena
import java.lang.foreign.FunctionDescriptor
import java.lang.foreign.Linker
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemorySegment
import java.lang.foreign.SymbolLookup
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BOOLEAN
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import kotlin.io.path.Path
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaMethod

val VOID: MemoryLayout? = null

object SkiaTreeApi {
    private val linker: Linker = Linker.nativeLinker()

    private val lookup = SymbolLookup.libraryLookup(Path(getSkiaTreePath()).resolve("skiatree.dll"), Arena.global())

    private val methodLookup = MethodHandles.lookup()

    private fun functionDescriptor(resLayout: MemoryLayout?, vararg argLayouts: MemoryLayout): FunctionDescriptor =
        if (resLayout != null) {
            FunctionDescriptor.of(resLayout, *argLayouts)
        } else {
            FunctionDescriptor.ofVoid(*argLayouts)
        }

    fun function(
        name: String,
        resLayout: MemoryLayout?,
        vararg argLayouts: MemoryLayout
    ): MethodHandle = linker.downcallHandle(
        lookup.find(name).orElseThrow { Exception("""Cannot find function "$name"""") },
        functionDescriptor(resLayout, *argLayouts)
    )

    fun staticUpcall(
        function: KFunction<*>,
        resLayout: MemoryLayout?,
        vararg argLayouts: MemoryLayout
    ): MemorySegment = upcall(null, function, resLayout, argLayouts, Arena.global())

    fun instanceUpcall(
        instance: Any,
        function: KFunction<*>,
        resLayout: MemoryLayout?,
        vararg argLayouts: MemoryLayout
    ): MemorySegment = upcall(instance, function, resLayout, argLayouts, Arena.ofAuto())

    private fun upcall(
        instance: Any?,
        function: KFunction<*>,
        resLayout: MemoryLayout?,
        argLayouts: Array<out MemoryLayout>,
        arena: Arena
    ): MemorySegment = linker.upcallStub(
        methodLookup.unreflect(requireNotNull(function.javaMethod).also { it.isAccessible = true }).run {
            instance?.let(::bindTo) ?: this
        },
        functionDescriptor(resLayout, *argLayouts),
        arena
    )

//    internal fun upcall(
//        instance: Any,
//        function: KFunction<*>,
//        block: Signature.Builder.() -> Signature.Element
//    ): Pair<MethodHandle, MemorySegment> {
//        val signature = buildSignature(block)
//        val method = function.javaMethod ?: throw Exception("Cannot get reflective access for $function")
//        method.isAccessible = true
//        val methodHandle = methodHandles.unreflect(method).bindTo(instance)
//        val upcallStub = linker.upcallStub(methodHandle, signature.functionDescriptor)
//        return Pair(methodHandle, upcallStub)
//    }

    private val lastErrorHandle: MethodHandle = function("skiatree_last_error", ADDRESS)

    @JvmStatic
    internal val libraryPointer: MemorySegment = guardedPointerArena {
        val newLibrary = function(
            "skiatree_library_new",
            ADDRESS,
            JAVA_BOOLEAN,
            InputUpCalls.layout,
            JAVA_INT
        )
        newLibrary(false, InputUpCalls.toSegment(), Key.LEFT_MOUSE_BUTTON.index) as MemorySegment
    }

    @JvmStatic
    private val libraryFlushAndSubmit = function("skiatree_library_flush_and_submit", VOID, ADDRESS)

    @JvmStatic
    internal val stringFree = function("skiatree_string_free", VOID, ADDRESS)

    internal fun lastError(): String? {
        val errorMessage = lastErrorHandle() as MemorySegment
        return if (errorMessage == MemorySegment.NULL) {
            null
        } else {
            errorMessage.getUtf8String(0)
        }
    }

    fun flushAndSubmit() {
        libraryFlushAndSubmit(libraryPointer)
    }
}

private external fun getSkiaTreePath(): String
