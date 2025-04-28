package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guardedBool
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

internal open class ReadableNodeBooleanParameterDelegate(getterName: String) : NodeParameterDelegate(JAVA_BYTE) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: Node, property: KProperty<*>): Boolean = guardedBool {
        getter(libraryPointer, thisRef.resource.key) as Byte
    }
}

internal class NodeBooleanParameterDelegate(
    getterName: String,
    setterName: String
) : ReadableNodeBooleanParameterDelegate(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: Boolean) {
        guardedUnit {
            val byteValue: Byte = if (value) 1 else 0
            setter(libraryPointer, thisRef.resource.key, byteValue) as Byte
        }
    }
}
