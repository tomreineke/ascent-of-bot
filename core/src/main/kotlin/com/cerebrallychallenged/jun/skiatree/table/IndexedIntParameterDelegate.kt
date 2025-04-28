package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

internal open class ReadableIndexedIntParameterDelegate<T: TableIndexed>(
    getterName: String
) : IndexedParameterDelegate(JAVA_INT) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: T, property: KProperty<*>): Int = guarded(Int.MIN_VALUE) {
        getter(libraryPointer, thisRef.table.resource.key, thisRef.index) as Int
    }
}

internal class IndexedIntParameterDelegate<T: TableIndexed>(
    getterName: String,
    setterName: String
) : ReadableIndexedIntParameterDelegate<T>(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: T, property: KProperty<*>, value: Int) {
        guardedUnit {
            setter(libraryPointer, thisRef.table.resource.key, thisRef.index, value) as Byte
        }
    }
}
