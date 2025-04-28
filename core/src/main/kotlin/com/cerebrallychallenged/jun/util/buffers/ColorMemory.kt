package com.cerebrallychallenged.jun.util.buffers

import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.unreal.color.FColor
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import java.lang.foreign.MemoryLayout.PathElement.sequenceElement
import java.lang.foreign.MemoryLayout.sequenceLayout
import java.lang.foreign.ValueLayout.JAVA_FLOAT
import java.lang.foreign.ValueLayout.JAVA_INT

object LayoutFColor : Memory.Layout(JAVA_INT) {
    val color = handle()
}

operator fun Memory<LayoutFColor>.get(index: Long): FColor = with(LayoutFColor) {
    FColor(color.get(segment, index) as Int)
}

operator fun Memory<LayoutFColor>.get(index: Int): FColor = get(index.toLong())

operator fun Memory<LayoutFColor>.set(index: Long, value: FColor): Unit = with(LayoutFColor) {
    color.set(segment, index, value.packedARGB)
}

operator fun Memory<LayoutFColor>.set(index: Int, value: FColor): Unit = set(index.toLong(), value)

fun Memory.View<Int>.asColorView(): Memory.View<FColor> = derive(::FColor, FColor::packedARGB)

object LayoutFLinearColor : Memory.Layout(sequenceLayout(4, JAVA_FLOAT)) {
    @JvmField
    val r = handle(sequenceElement(0))

    @JvmField
    val g = handle(sequenceElement(1))

    @JvmField
    val b = handle(sequenceElement(2))

    @JvmField
    val a = handle(sequenceElement(3))
}

operator fun Memory<LayoutFLinearColor>.get(index: Long): FLinearColor = with(LayoutFLinearColor) {
    FLinearColor(vec(
        r.get(segment, index) as Float,
        g.get(segment, index) as Float,
        b.get(segment, index) as Float,
        a.get(segment, index) as Float
    ))
}

operator fun Memory<LayoutFLinearColor>.get(index: Int): FLinearColor = get(index.toLong())

operator fun Memory<LayoutFLinearColor>.set(index: Long, value: FLinearColor): Unit = with(LayoutFLinearColor) {
    val rgba = value.rgba
    r.set(segment, index, rgba.r)
    g.set(segment, index, rgba.g)
    b.set(segment, index, rgba.b)
    a.set(segment, index, rgba.a)
}

operator fun Memory<LayoutFLinearColor>.set(index: Int, value: FLinearColor): Unit = set(index.toLong(), value)
