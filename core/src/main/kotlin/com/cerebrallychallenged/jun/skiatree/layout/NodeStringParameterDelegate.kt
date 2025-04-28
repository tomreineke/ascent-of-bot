package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.consumeNullableFfiString
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnitArena
import com.cerebrallychallenged.jun.skiatree.node.Node
import com.cerebrallychallenged.jun.skiatree.toSegment
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

open class ReadableNodeStringParameterDelegate(getterName: String) {
    val getter: MethodHandle = function(getterName, ADDRESS, ADDRESS, JAVA_LONG)

    operator fun getValue(thisRef: Node, property: KProperty<*>): String {
        return requireNotNull(guarded(null) {
            (getter(libraryPointer, thisRef.resource.key) as MemorySegment).consumeNullableFfiString()
        })
    }
}


class NodeStringParameterDelegate(
    getterName: String,
    setterName: String
) : ReadableNodeStringParameterDelegate(getterName) {
    val setter: MethodHandle = function(setterName, JAVA_BYTE, ADDRESS, JAVA_LONG, ADDRESS)

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: String) {
        guardedUnitArena {
            setter(
                libraryPointer,
                thisRef.resource.key,
                value.toSegment()
            ) as Byte
        }
    }
}
