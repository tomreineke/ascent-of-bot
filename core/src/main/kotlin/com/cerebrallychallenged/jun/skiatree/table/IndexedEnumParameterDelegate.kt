package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

abstract class IndexedEnumParameterDelegate<E: Enum<E>, T: TableIndexed>(
    private val constants: Array<out E>
) : IndexedParameterDelegate(JAVA_INT) {
    abstract val getter: MethodHandle

    abstract val setter: MethodHandle

    operator fun getValue(thisRef: T, property: KProperty<*>): E {
        return constants[guarded(-1) {
            getter(
                libraryPointer,
                thisRef.table.resource.key,
                thisRef.index
            ) as Int
        }]
    }

    operator fun setValue(thisRef: T, property: KProperty<*>, value: E) {
        guardedUnit {
            setter(
                libraryPointer,
                thisRef.table.resource.key,
                thisRef.index,
                value.ordinal
            ) as Byte
        }
    }
}
