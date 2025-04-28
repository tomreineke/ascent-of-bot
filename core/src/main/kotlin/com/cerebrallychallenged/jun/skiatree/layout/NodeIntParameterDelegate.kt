package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

internal open class ReadableNodeIntParameterDelegate(getterName: String) : NodeParameterDelegate(JAVA_INT) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: Node, property: KProperty<*>): Int = guarded(Int.MIN_VALUE) {
        getter(libraryPointer, thisRef.resource.key) as Int
    }
}

internal class NodeIntParameterDelegate(
    getterName: String,
    setterName: String
) : ReadableNodeIntParameterDelegate(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: Int) {
        guardedUnit {
            setter(libraryPointer, thisRef.resource.key, value) as Byte
        }
    }
}
