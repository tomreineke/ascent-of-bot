package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import com.cerebrallychallenged.jun.skiatree.guardedArena
import com.cerebrallychallenged.jun.skiatree.guardedUnitArena
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

open class ReadableNodeIRectParameterDelegate(getterName: String) : NodeParameterDelegate(IRect.Layout) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: Node, property: KProperty<*>): IRect {
        return requireNotNull(guardedArena(null) {
            val rectSegment = getter(this, libraryPointer, thisRef.resource.key) as MemorySegment
            if (IRect.isErrorSegment(rectSegment)) {
                null
            } else {
                IRect.decodeFromSegment(rectSegment)
            }
        })
    }
}

class NodeIRectParameterDelegate(
    getterName: String,
    setterName: String
) : ReadableNodeIRectParameterDelegate(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: IRect) {
        guardedUnitArena {
            setter(
                libraryPointer,
                thisRef.resource.key,
                value.toSegment()
            ) as Byte
        }
    }
}
