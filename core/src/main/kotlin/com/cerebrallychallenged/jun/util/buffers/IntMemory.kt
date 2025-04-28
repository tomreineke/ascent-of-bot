package com.cerebrallychallenged.jun.util.buffers

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.Vec3i
import com.cerebrallychallenged.jun.math.geo.Vec4i
import com.cerebrallychallenged.jun.math.geo.vec
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG
import java.lang.foreign.ValueLayout.JAVA_SHORT

object LayoutInt : Memory.Layout(JAVA_INT) {
    @JvmField
    internal val x = handle()
}

operator fun Memory<LayoutInt>.get(index: Long): Int = with(LayoutInt) {
    x.get(segment, index) as Int
}

operator fun Memory<LayoutInt>.get(index: Int): Int = get(index.toLong())

operator fun Memory<LayoutInt>.set(index: Long, value: Int): Unit = with(LayoutInt) {
    x.set(segment, index, value)
}

operator fun Memory<LayoutInt>.set(index: Int, value: Int): Unit = set(index.toLong(), value)

fun Memory<LayoutInt>.view() = object : Memory.View<Int>(this) {
    override fun get(index: Long): Int = this@view[index]

    override fun set(index: Long, value: Int) {
        this@view[index] = value
    }
}

object LayoutVec2i : Memory.Layout(sequenceLayout(2, JAVA_INT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))
}

operator fun Memory<LayoutVec2i>.get(index: Long): Vec2i = with(LayoutVec2i) {
    vec(
        x.get(segment, index) as Int,
        y.get(segment, index) as Int
    )
}

operator fun Memory<LayoutVec2i>.get(index: Int): Vec2i = get(index.toLong())

operator fun Memory<LayoutVec2i>.set(index: Long, value: Vec2i): Unit = with(LayoutVec2i) {
    x.set(segment, index, value.x)
    y.set(segment, index, value.y)
}

operator fun Memory<LayoutVec2i>.set(index: Int, value: Vec2i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec2i")
fun Memory<LayoutVec2i>.view() = object : Memory.View<Vec2i>(this) {
    override fun get(index: Long): Vec2i = this@view[index]

    override fun set(index: Long, value: Vec2i) {
        this@view[index] = value
    }
}

object LayoutVec3i : Memory.Layout(sequenceLayout(3, JAVA_INT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))
}

operator fun Memory<LayoutVec3i>.get(index: Long): Vec3i = with(LayoutVec3i) {
    vec(
        x.get(segment, index) as Int,
        y.get(segment, index) as Int,
        z.get(segment, index) as Int
    )
}

operator fun Memory<LayoutVec3i>.get(index: Int): Vec3i = get(index.toLong())

operator fun Memory<LayoutVec3i>.set(index: Long, value: Vec3i): Unit = with(LayoutVec3i) {
    x.set(segment, index, value.x)
    y.set(segment, index, value.y)
    z.set(segment, index, value.z)
}

operator fun Memory<LayoutVec3i>.set(index: Int, value: Vec3i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec3i")
fun Memory<LayoutVec3i>.view() = object : Memory.View<Vec3i>(this) {
    override fun get(index: Long): Vec3i = this@view[index]

    override fun set(index: Long, value: Vec3i) {
        this@view[index] = value
    }
}

object LayoutVec4i : Memory.Layout(sequenceLayout(4, JAVA_INT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))

    @JvmField
    internal val w = handle(sequenceElement(3))
}

operator fun Memory<LayoutVec4i>.get(index: Long): Vec4i = with(LayoutVec4i) {
    vec(
        x.get(segment, index) as Int,
        y.get(segment, index) as Int,
        z.get(segment, index) as Int,
        w.get(segment, index) as Int
    )
}

operator fun Memory<LayoutVec4i>.get(index: Int): Vec4i = get(index.toLong())

operator fun Memory<LayoutVec4i>.set(index: Long, value: Vec4i): Unit = with(LayoutVec4i) {
    x.set(segment, index, value.x)
    y.set(segment, index, value.y)
    z.set(segment, index, value.z)
    w.set(segment, index, value.w)
}

operator fun Memory<LayoutVec4i>.set(index: Int, value: Vec4i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec4i")
fun Memory<LayoutVec4i>.view() = object : Memory.View<Vec4i>(this) {
    override fun get(index: Long): Vec4i = this@view[index]

    override fun set(index: Long, value: Vec4i) {
        this@view[index] = value
    }
}

object LayoutVec2i16 : Memory.Layout(sequenceLayout(2, JAVA_SHORT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))
}

@JvmName("getLayoutVec2i16")
operator fun Memory<LayoutVec2i16>.get(index: Long): Vec2i = with(LayoutVec2i16) {
    vec(
        (x.get(segment, index) as Short).toInt(),
        (y.get(segment, index) as Short).toInt()
    )
}

@JvmName("setLayoutVec2i16")
operator fun Memory<LayoutVec2i16>.set(index: Long, value: Vec2i): Unit = with(LayoutVec2i16) {
    x.set(segment, index, value.x.toShort())
    y.set(segment, index, value.y.toShort())
}

@JvmName("setLayoutVec2i16")
operator fun Memory<LayoutVec2i16>.set(index: Int, value: Vec2i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec2i16")
fun Memory<LayoutVec2i16>.view() = object : Memory.View<Vec2i>(this) {
    override fun get(index: Long): Vec2i = this@view[index]

    override fun set(index: Long, value: Vec2i) {
        this@view[index] = value
    }
}

object LayoutVec3i16 : Memory.Layout(sequenceLayout(3, JAVA_SHORT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))
}

@JvmName("getLayoutVec3i16")
operator fun Memory<LayoutVec3i16>.get(index: Long): Vec3i = with(LayoutVec3i16) {
    vec(
        (x.get(segment, index) as Short).toInt(),
        (y.get(segment, index) as Short).toInt(),
        (z.get(segment, index) as Short).toInt()
    )
}

@JvmName("setLayoutVec3i16")
operator fun Memory<LayoutVec3i16>.set(index: Long, value: Vec3i): Unit = with(LayoutVec3i16) {
    x.set(segment, index, value.x.toShort())
    y.set(segment, index, value.y.toShort())
    z.set(segment, index, value.z.toShort())
}

@JvmName("setLayoutVec3i16")
operator fun Memory<LayoutVec3i16>.set(index: Int, value: Vec3i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec3i16")
fun Memory<LayoutVec3i16>.view() = object : Memory.View<Vec3i>(this) {
    override fun get(index: Long): Vec3i = this@view[index]

    override fun set(index: Long, value: Vec3i) {
        this@view[index] = value
    }
}

object LayoutVec4i16 : Memory.Layout(sequenceLayout(4, JAVA_SHORT)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))

    @JvmField
    internal val w = handle(sequenceElement(3))
}

@JvmName("getLayoutVec4i16")
operator fun Memory<LayoutVec4i16>.get(index: Long): Vec4i = with(LayoutVec4i16) {
    vec(
        (x.get(segment, index) as Short).toInt(),
        (y.get(segment, index) as Short).toInt(),
        (z.get(segment, index) as Short).toInt(),
        (w.get(segment, index) as Short).toInt(),
    )
}

@JvmName("setLayoutVec4i16")
operator fun Memory<LayoutVec4i16>.set(index: Long, value: Vec4i): Unit = with(LayoutVec4i16) {
    x.set(segment, index, value.x.toShort())
    y.set(segment, index, value.y.toShort())
    z.set(segment, index, value.z.toShort())
    w.set(segment, index, value.w.toShort())
}

@JvmName("setLayoutVec4i16")
operator fun Memory<LayoutVec4i16>.set(index: Int, value: Vec4i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec4i16")
fun Memory<LayoutVec4i16>.view() = object : Memory.View<Vec4i>(this) {
    override fun get(index: Long): Vec4i = this@view[index]

    override fun set(index: Long, value: Vec4i) {
        this@view[index] = value
    }
}

object LayoutVec2i64 : Memory.Layout(sequenceLayout(2, JAVA_LONG)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))
}

@JvmName("getLayoutVec2i64")
operator fun Memory<LayoutVec2i64>.get(index: Long): Vec2i = with(LayoutVec2i64) {
    vec(
        (x.get(segment, index) as Long).toInt(),
        (y.get(segment, index) as Long).toInt()
    )
}

@JvmName("setLayoutVec2i64")
operator fun Memory<LayoutVec2i64>.set(index: Long, value: Vec2i): Unit = with(LayoutVec2i64) {
    x.set(segment, index, value.x.toLong())
    y.set(segment, index, value.y.toLong())
}

@JvmName("setLayoutVec2i64")
operator fun Memory<LayoutVec2i64>.set(index: Int, value: Vec2i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec2i64")
fun Memory<LayoutVec2i64>.view() = object : Memory.View<Vec2i>(this) {
    override fun get(index: Long): Vec2i = this@view[index]

    override fun set(index: Long, value: Vec2i) {
        this@view[index] = value
    }
}

object LayoutVec3i64 : Memory.Layout(sequenceLayout(3, JAVA_LONG)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))
}

@JvmName("getLayoutVec3i64")
operator fun Memory<LayoutVec3i64>.get(index: Long): Vec3i = with(LayoutVec3i64) {
    vec(
        (x.get(segment, index) as Long).toInt(),
        (y.get(segment, index) as Long).toInt(),
        (z.get(segment, index) as Long).toInt()
    )
}

@JvmName("setLayoutVec3i64")
operator fun Memory<LayoutVec3i64>.set(index: Long, value: Vec3i): Unit = with(LayoutVec3i64) {
    x.set(segment, index, value.x.toLong())
    y.set(segment, index, value.y.toLong())
    z.set(segment, index, value.z.toLong())
}

@JvmName("setLayoutVec3i64")
operator fun Memory<LayoutVec3i64>.set(index: Int, value: Vec3i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec3i64")
fun Memory<LayoutVec3i64>.view() = object : Memory.View<Vec3i>(this) {
    override fun get(index: Long): Vec3i = this@view[index]

    override fun set(index: Long, value: Vec3i) {
        this@view[index] = value
    }
}

object LayoutVec4i64 : Memory.Layout(sequenceLayout(4, JAVA_LONG)) {
    @JvmField
    internal val x = handle(sequenceElement(0))

    @JvmField
    internal val y = handle(sequenceElement(1))

    @JvmField
    internal val z = handle(sequenceElement(2))

    @JvmField
    internal val w = handle(sequenceElement(3))
}

@JvmName("getLayoutVec4i64")
operator fun Memory<LayoutVec4i64>.get(index: Long): Vec4i = with(LayoutVec4i64) {
    vec(
        (x.get(segment, index) as Long).toInt(),
        (y.get(segment, index) as Long).toInt(),
        (z.get(segment, index) as Long).toInt(),
        (w.get(segment, index) as Long).toInt()
    )
}

@JvmName("setLayoutVec4i64")
operator fun Memory<LayoutVec4i64>.set(index: Long, value: Vec4i): Unit = with(LayoutVec4i64) {
    x.set(segment, index, value.x.toLong())
    y.set(segment, index, value.y.toLong())
    z.set(segment, index, value.z.toLong())
    w.set(segment, index, value.w.toLong())
}

@JvmName("setLayoutVec4i64")
operator fun Memory<LayoutVec4i64>.set(index: Int, value: Vec4i): Unit = set(index.toLong(), value)

@JvmName("viewLayoutVec4i64")
fun Memory<LayoutVec4i64>.view() = object : Memory.View<Vec4i>(this) {
    override fun get(index: Long): Vec4i = this@view[index]

    override fun set(index: Long, value: Vec4i) {
        this@view[index] = value
    }
}
