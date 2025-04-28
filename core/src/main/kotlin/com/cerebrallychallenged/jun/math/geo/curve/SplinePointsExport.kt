package com.cerebrallychallenged.jun.math.geo.curve

import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.times
import com.cerebrallychallenged.jun.unreal.newObject
import com.cerebrallychallenged.jun.unreal.spline.ESplinePointType
import com.cerebrallychallenged.jun.unreal.spline.USplineComponent
import com.cerebrallychallenged.jun.util.buffers.LayoutFSplinePoint
import com.cerebrallychallenged.jun.util.buffers.Memory
import com.cerebrallychallenged.jun.util.buffers.allocate
import com.cerebrallychallenged.jun.util.buffers.arriveTangent
import com.cerebrallychallenged.jun.util.buffers.inputKey
import com.cerebrallychallenged.jun.util.buffers.leaveTangent
import com.cerebrallychallenged.jun.util.buffers.position
import com.cerebrallychallenged.jun.util.buffers.scale
import com.cerebrallychallenged.jun.util.buffers.type
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.SegmentAllocator

context(SegmentAllocator)
fun BezierCurve<Vec3f>.toFSplinePoints(): Memory<LayoutFSplinePoint> {
    val memory = LayoutFSplinePoint.allocate(segments.size + 1)
    val inputKey = memory.inputKey
    val position = memory.position
    val leaveTangent = memory.leaveTangent
    val arriveTangent = memory.arriveTangent
    val scale = memory.scale
    val type = memory.type
    inputKey[0] = 0.0f
    position[0] = startPoint
    scale[0] = Vec3f.ONE
    type[0] = ESplinePointType.CurveCustomTangent
    var index = 0L
    for (segment in segments.values) {
        ++index
        inputKey[index] = index.toFloat()
        position[index] = segment.target
        scale[index] = Vec3f.ONE
        type[index] = ESplinePointType.CurveCustomTangent
        when (segment) {
            is BezierCurve.QuadSegment -> {
                val source = segment.source
                val control = segment.control
                val target = segment.target
                // Unorthodox factor of 3.0 compared to "real" Bezier formula, see CubicInterp in UnrealMathUtility.h
                leaveTangent[index - 1] = 1.5f * (control - source)
                arriveTangent[index] = 1.5f * (target - control)
            }
            is BezierCurve.CubicSegment -> {
                val source = segment.source
                val firstControl = segment.firstControl
                val secondControl = segment.secondControl
                val target = segment.target
                // Unorthodox factor of 3.0 compared to "real" Bezier formula, see CubicInterp in UnrealMathUtility.h
                leaveTangent[index - 1] = 3.0f * (firstControl - source)
                arriveTangent[index] = 3.0f * (target - secondControl)
            }
            else -> {}
        }
    }
    return memory
}

fun BezierCurve<Vec3f>.toUSplineComponent(): USplineComponent = newObject<USplineComponent>().apply {
    clearSplinePoints(false)
    confinedArena {
        addPoints(toFSplinePoints(), true)
    }
    registerComponent()
}
