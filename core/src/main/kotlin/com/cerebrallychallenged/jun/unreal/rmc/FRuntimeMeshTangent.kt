package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.buffers.Memory
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

data class FRuntimeMeshTangent(val tangentX: Vec3f = Vec3f.UNIT_X, val flipTangentX: Boolean = false)

object LayoutFRuntimeMeshTangent : Memory.Layout(
    MemoryLayout.structLayout(
        sequenceLayout(3, JAVA_FLOAT).withName("tangentX"),
        JAVA_INT.withName("flipTangentX"),
    )
) {
    internal val x = handle(groupElement("tangentX"), sequenceElement(0))

    internal val y = handle(groupElement("tangentX"), sequenceElement(1))

    internal val z = handle(groupElement("tangentX"), sequenceElement(2))

    internal val flip = handle(groupElement("flipTangentX"))
}

operator fun Memory<LayoutFRuntimeMeshTangent>.get(index: Long): FRuntimeMeshTangent = with(LayoutFRuntimeMeshTangent) {
    FRuntimeMeshTangent(
        vec(
            x.get(segment, index) as Float,
            y.get(segment, index) as Float,
            z.get(segment, index) as Float,
        ),
        (flip.get(segment, index) as Int) != 0
    )
}

operator fun Memory<LayoutFRuntimeMeshTangent>.get(index: Int): FRuntimeMeshTangent = get(index.toLong())

operator fun Memory<LayoutFRuntimeMeshTangent>.set(
    index: Long,
    value: FRuntimeMeshTangent
): Unit = with(LayoutFRuntimeMeshTangent) {
    val (tangentX, flipTangentX) = value
    x.set(segment, index, tangentX.x)
    y.set(segment, index, tangentX.y)
    z.set(segment, index, tangentX.z)
    flip.set(segment, index, if (flipTangentX) 1 else 0)
}

operator fun Memory<LayoutFRuntimeMeshTangent>.set(index: Int, value: FRuntimeMeshTangent): Unit =
    set(index.toLong(), value)
