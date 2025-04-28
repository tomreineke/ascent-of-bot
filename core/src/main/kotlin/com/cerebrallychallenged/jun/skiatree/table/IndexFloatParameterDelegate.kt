package com.cerebrallychallenged.jun.skiatree.table

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.guarded
import com.cerebrallychallenged.jun.skiatree.guardedUnit
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

internal open class ReadableIndexedFloatParameterDelegate<T: TableIndexed>(
    getterName: String
) : IndexedParameterDelegate(JAVA_FLOAT) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: T, property: KProperty<*>): Float = guarded(Float.NaN) {
        getter(libraryPointer, thisRef.table.resource.key, thisRef.index) as Float
    }
}

internal class IndexedFloatParameterDelegate<T: TableIndexed>(
    getterName: String,
    setterName: String
) : ReadableIndexedFloatParameterDelegate<T>(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: T, property: KProperty<*>, value: Float) {
        guardedUnit {
            setter(libraryPointer, thisRef.table.resource.key, thisRef.index, value) as Byte
        }
    }
}
