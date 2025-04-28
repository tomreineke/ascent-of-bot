package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

internal open class ReadableNodeFloatParameterDelegate(getterName: String) : NodeParameterDelegate(JAVA_FLOAT) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: Node, property: KProperty<*>): Float = guarded(Float.NaN) {
        getter(libraryPointer, thisRef.resource.key) as Float
    }
}

internal class NodeFloatParameterDelegate(
    getterName: String,
    setterName: String
) : ReadableNodeFloatParameterDelegate(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: Float) {
        guardedUnit {
            setter(libraryPointer, thisRef.resource.key, value) as Byte
        }
    }
}
