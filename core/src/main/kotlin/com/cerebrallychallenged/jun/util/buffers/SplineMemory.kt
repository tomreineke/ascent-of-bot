package com.cerebrallychallenged.jun.util.buffers

import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.spline.ESplinePointType
import com.cerebrallychallenged.jun.util.buffers.Memory.Layout
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_DOUBLE
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.invoke.VarHandle

object LayoutFSplinePoint : Layout(MemoryLayout.structLayout(
    JAVA_FLOAT.withName("InputKey"),
    MemoryLayout.paddingLayout(4),
    sequenceLayout(3, JAVA_DOUBLE).withName("Position"),
    sequenceLayout(3, JAVA_DOUBLE).withName("ArriveTangent"),
    sequenceLayout(3, JAVA_DOUBLE).withName("LeaveTangent"),
    sequenceLayout(3, JAVA_DOUBLE).withName("Rotation"),
    sequenceLayout(3, JAVA_DOUBLE).withName("Scale"),
    JAVA_BYTE.withName("Type"),
    MemoryLayout.paddingLayout(7)
)) {
    private fun componentHandle(name: String, componentIndex: Long): VarHandle =
        handle(groupElement(name), sequenceElement(componentIndex))

    @JvmField
    internal val inputKey = handle(groupElement("InputKey"))

    class InputKey(memory: Memory<LayoutFSplinePoint>): Memory.View<Float>(memory) {
        override fun get(index: Long): Float = inputKey.get(segment, index) as Float

        override fun set(index: Long, value: Float) {
            inputKey.set(segment, index, value)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    abstract class Channel(memory: Memory<LayoutFSplinePoint>): Memory.View<Vec3f>(memory) {
        internal inline fun get(handleX: VarHandle, handleY: VarHandle, handleZ: VarHandle, index: Long): Vec3f = vec(
            (handleX.get(segment, index) as Double).toFloat(),
            (handleY.get(segment, index) as Double).toFloat(),
            (handleZ.get(segment, index) as Double).toFloat()
        )

        internal inline fun set(handleX: VarHandle, handleY: VarHandle, handleZ: VarHandle, index: Long, value: Vec3f) {
            handleX.set(segment, index, value.x.toDouble())
            handleY.set(segment, index, value.y.toDouble())
            handleZ.set(segment, index, value.z.toDouble())
        }
    }

    @JvmField
    internal val positionX = componentHandle("Position", 0)
    @JvmField
    internal val positionY = componentHandle("Position", 1)
    @JvmField
    internal val positionZ = componentHandle("Position", 2)

    class Position(memory: Memory<LayoutFSplinePoint>): Channel(memory) {
        override fun get(index: Long): Vec3f = get(positionX, positionY, positionZ, index)

        override fun set(index: Long, value: Vec3f) {
            set(positionX, positionY, positionZ, index, value)
        }
    }

    @JvmField
    internal val arriveTangentX = componentHandle("ArriveTangent", 0)
    @JvmField
    internal val arriveTangentY = componentHandle("ArriveTangent", 1)
    @JvmField
    internal val arriveTangentZ = componentHandle("ArriveTangent", 2)

    class ArriveTangent(memory: Memory<LayoutFSplinePoint>): Channel(memory) {
        override fun get(index: Long): Vec3f = get(arriveTangentX, arriveTangentY, arriveTangentZ, index)

        override fun set(index: Long, value: Vec3f) {
            set(arriveTangentX, arriveTangentY, arriveTangentZ, index, value)
        }
    }

    @JvmField
    internal val leaveTangentX = componentHandle("LeaveTangent", 0)
    @JvmField
    internal val leaveTangentY = componentHandle("LeaveTangent", 1)
    @JvmField
    internal val leaveTangentZ = componentHandle("LeaveTangent", 2)

    class LeaveTangent(memory: Memory<LayoutFSplinePoint>): Channel(memory) {
        override fun get(index: Long): Vec3f = get(leaveTangentX, leaveTangentY, leaveTangentZ, index)

        override fun set(index: Long, value: Vec3f) {
            set(leaveTangentX, leaveTangentY, leaveTangentZ, index, value)
        }
    }

    @JvmField
    internal val rotationX = componentHandle("Rotation", 0)
    @JvmField
    internal val rotationY = componentHandle("Rotation", 1)
    @JvmField
    internal val rotationZ = componentHandle("Rotation", 2)

    class Rotation(memory: Memory<LayoutFSplinePoint>): Channel(memory) {
        override fun get(index: Long): Vec3f = get(rotationX, rotationY, rotationZ, index)

        override fun set(index: Long, value: Vec3f) {
            set(rotationX, rotationY, rotationZ, index, value)
        }
    }

    @JvmField
    internal val scaleX = componentHandle("Scale", 0)
    @JvmField
    internal val scaleY = componentHandle("Scale", 1)
    @JvmField
    internal val scaleZ = componentHandle("Scale", 2)

    class Scale(memory: Memory<LayoutFSplinePoint>): Channel(memory) {
        override fun get(index: Long): Vec3f = get(scaleX, scaleY, scaleZ, index)

        override fun set(index: Long, value: Vec3f) {
            set(scaleX, scaleY, scaleZ, index, value)
        }
    }

    @JvmField
    internal val type = handle(groupElement("Type"))

    class Type(memory: Memory<LayoutFSplinePoint>): Memory.View<ESplinePointType>(memory) {
        override fun get(index: Long): ESplinePointType =
            ESplinePointType.entries[(type.get(segment, index) as Byte).toInt()]

        override fun set(index: Long, value: ESplinePointType) {
            type.set(segment, index, value.ordinal.toByte())
        }
    }
}

val Memory<LayoutFSplinePoint>.inputKey: LayoutFSplinePoint.InputKey
    get() = LayoutFSplinePoint.InputKey(this)

val Memory<LayoutFSplinePoint>.position: LayoutFSplinePoint.Position
    get() = LayoutFSplinePoint.Position(this)

val Memory<LayoutFSplinePoint>.arriveTangent: LayoutFSplinePoint.ArriveTangent
    get() = LayoutFSplinePoint.ArriveTangent(this)

val Memory<LayoutFSplinePoint>.leaveTangent: LayoutFSplinePoint.LeaveTangent
    get() = LayoutFSplinePoint.LeaveTangent(this)

val Memory<LayoutFSplinePoint>.rotation: LayoutFSplinePoint.Rotation
    get() = LayoutFSplinePoint.Rotation(this)

val Memory<LayoutFSplinePoint>.scale: LayoutFSplinePoint.Scale
    get() = LayoutFSplinePoint.Scale(this)

val Memory<LayoutFSplinePoint>.type: LayoutFSplinePoint.Type
    get() = LayoutFSplinePoint.Type(this)
