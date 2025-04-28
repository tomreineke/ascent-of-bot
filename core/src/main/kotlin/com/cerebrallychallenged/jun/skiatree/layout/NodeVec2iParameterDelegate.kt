package com.cerebrallychallenged.jun.skiatree.layout

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import com.cerebrallychallenged.jun.skiatree.geo.IPointLayout
import com.cerebrallychallenged.jun.skiatree.geo.decodeFromSegment
import com.cerebrallychallenged.jun.skiatree.geo.isErrorSegment
import com.cerebrallychallenged.jun.skiatree.geo.toSegment
import com.cerebrallychallenged.jun.skiatree.guardedArena
import com.cerebrallychallenged.jun.skiatree.guardedUnitArena
import com.cerebrallychallenged.jun.skiatree.node.Node
import java.lang.foreign.MemorySegment
import java.lang.invoke.MethodHandle
import kotlin.reflect.KProperty

open class ReadableNodeVec2iParameterDelegate(getterName: String) : NodeParameterDelegate(IPointLayout) {
    private val getter: MethodHandle = createGetter(getterName)

    operator fun getValue(thisRef: Node, property: KProperty<*>): Vec2i {
        return requireNotNull(guardedArena(null) {
            val dimensionSegment = getter(this, libraryPointer, thisRef.resource.key) as MemorySegment
            if (Vec2i.isErrorSegment(dimensionSegment)) {
                null
            } else {
                Vec2i.decodeFromSegment(dimensionSegment)
            }
        })
    }
}

class NodeVec2iParameterDelegate(
    getterName: String,
    setterName: String
) : ReadableNodeVec2iParameterDelegate(getterName) {
    private val setter: MethodHandle = createSetter(setterName)

    operator fun setValue(thisRef: Node, property: KProperty<*>, value: Vec2i) {
        guardedUnitArena {
            setter(
                libraryPointer,
                thisRef.resource.key,
                value.toSegment()
            ) as Byte
        }
    }
}
