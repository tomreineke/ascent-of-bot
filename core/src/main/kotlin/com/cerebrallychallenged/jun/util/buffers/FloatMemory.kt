package com.cerebrallychallenged.jun.util.buffers

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.math.geo.vec
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout.JAVA_FLOAT

object LayoutFloat : Memory.Layout(JAVA_FLOAT) {
    @JvmField
    internal val x = handle()
}

operator fun Memory<LayoutFloat>.get(index: Long): Float = with(LayoutFloat) {
    x.get(segment, index) as Float
}

operator fun Memory<LayoutFloat>.get(index: Int): Float = get(index.toLong())

operator fun Memory<LayoutFloat>.set(index: Long, value: Float): Unit = with(LayoutFloat) {
    x.set(segment, index, value)
}

operator fun Memory<LayoutFloat>.set(index: Int, value: Float): Unit = set(index.toLong(), value)

fun Memory<LayoutFloat>.view() = object : Memory.View<Float>(this) {
    override fun get(index: Long): Float = this@view[index]

    override fun set(index: Long, value: Float) {
        this@view[index] = value
    }
}

object LayoutVec2f : Memory.Layout(sequenceLayout(2, JAVA_FLOAT)) {
    @JvmStatic
    internal val x = handle(sequenceElement(0))

    @JvmStatic
    internal val y = handle(sequenceElement(1))
}

operator fun Memory<LayoutVec2f>.get(index: Long): Vec2f = with(LayoutVec2f) {
    vec(
        x.get(segment, index) as Float,
        y.get(segment, index) as Float
    )
}

operator fun Memory<LayoutVec2f>.get(index: Int): Vec2f = get(index.toLong())

operator fun Memory<LayoutVec2f>.set(index: Long, value: Vec2f): Unit = with(LayoutVec2f) {
    x.set(segment, index, value.x)
    y.set(segment, index, value.y)
}

operator fun Memory<LayoutVec2f>.set(index: Int, value: Vec2f): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec2f")
fun Memory<LayoutVec2f>.view(): Memory.View<Vec2f> = object : Memory.View<Vec2f>(this) {
    override fun get(index: Long): Vec2f = this@view[index]

    override fun set(index: Long, value: Vec2f) {
        this@view[index] = value
    }
}

object LayoutVec3f : Memory.Layout(sequenceLayout(3, JAVA_FLOAT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))
}

operator fun Memory<LayoutVec3f>.get(index: Long): Vec3f = with(LayoutVec3f) {
    vec(
        x.get(segment, index) as Float,
        y.get(segment, index) as Float,
        z.get(segment, index) as Float
    )
}

operator fun Memory<LayoutVec3f>.get(index: Int): Vec3f = get(index.toLong())

operator fun Memory<LayoutVec3f>.set(index: Long, value: Vec3f): Unit = with(LayoutVec3f) {
    x.set(segment, index, value.x)
    y.set(segment, index, value.y)
    z.set(segment, index, value.z)
}

operator fun Memory<LayoutVec3f>.set(index: Int, value: Vec3f): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec3f")
fun Memory<LayoutVec3f>.view(): Memory.View<Vec3f> = object : Memory.View<Vec3f>(this) {
    override fun get(index: Long): Vec3f = this@view[index]

    override fun set(index: Long, value: Vec3f) {
        this@view[index] = value
    }
}

object LayoutVec4f : Memory.Layout(sequenceLayout(4, JAVA_FLOAT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))

    @JvmField
    internal val w = handle(sequenceElement(3))
}

operator fun Memory<LayoutVec4f>.get(index: Long): Vec4f = with(LayoutVec4f) {
    vec(
        x.get(segment, index) as Float,
        y.get(segment, index) as Float,
        z.get(segment, index) as Float,
        w.get(segment, index) as Float
    )
}

operator fun Memory<LayoutVec4f>.get(index: Int): Vec4f = get(index.toLong())

operator fun Memory<LayoutVec4f>.set(index: Long, value: Vec4f): Unit = with(LayoutVec4f) {
    x.set(segment, index, value.x)
    y.set(segment, index, value.y)
    z.set(segment, index, value.z)
    w.set(segment, index, value.w)
}

operator fun Memory<LayoutVec4f>.set(index: Int, value: Vec4f): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec4f")
fun Memory<LayoutVec4f>.view(): Memory.View<Vec4f> = object : Memory.View<Vec4f>(this) {
    override fun get(index: Long): Vec4f = this@view[index]

    override fun set(index: Long, value: Vec4f) {
        this@view[index] = value
    }
}
