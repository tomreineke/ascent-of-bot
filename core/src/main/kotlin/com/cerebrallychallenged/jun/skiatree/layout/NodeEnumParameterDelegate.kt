package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

abstract class NodeEnumParameterDelegate<E: Enum<E>>(
    private val constants: Array<out E>
) : NodeParameterDelegate(JAVA_INT) {
    abstract val getter: MethodHandle

    abstract val setter: MethodHandle

    operator fun getValue(thisRef: Node, property: KProperty<*>): E {
        return constants[guarded(-1) {
            getter(
                libraryPointer,
                thisRef.resource.key
            ) as Int
        }]
    }

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: E) {
        guardedUnit {
            setter(
                libraryPointer,
                thisRef.resource.key,
                value.ordinal
            ) as Byte
        }
    }
}
