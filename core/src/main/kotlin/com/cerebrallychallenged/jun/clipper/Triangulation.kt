package com.cerebrallychallenged.jun.clipper

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.curve.Polyline
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshData
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionProperties
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.buffers.*
import com.cerebrallychallenged.jun.util.confinedArena
import com.cerebrallychallenged.jun.wrapSharedRef
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer

fun AnyRef<PolyTree>.triangulate(
    scale: Float,
    steinerPoints: Collection<Vec2f>
): Pair<Memory<LayoutVec3f>, Memory<LayoutVec3i>> {
    val (vertices, indices) = confinedArena {
        val memory = steinerPoints.toMemory(LayoutVec2i64, Memory<LayoutVec2i64>::set) { (it * scale).round() }
        triangulate(
            directPtr,
            scale,
            memory.segment.asByteBuffer()
        )
    }
    return Pair(
        MemorySegment.ofBuffer(vertices).withLayout(LayoutVec3f),
        MemorySegment.ofBuffer(indices).withLayout(LayoutVec3i)
    )
}

fun Polyline<Vec3f>.triangulate(
    distance: Float,
    meshSectionProperties: TSharedRef<FRuntimeMeshSectionProperties>
): TSharedRef<FRuntimeMeshRenderableMeshData> = confinedArena {
    val memory = vertices.toMemory(LayoutVec3f, Memory<LayoutVec3f>::set)
    triangulatePolyline(
        memory.segment.asByteBuffer(),
        meshSectionProperties.directPtr,
        distance
    ).wrapSharedRef()
}

private external fun triangulate(
    directPtr: CPointer,
    scale: Float,
    steinerPointBuffer: ByteBuffer
): Pair<ByteBuffer, ByteBuffer>

private external fun triangulatePolyline(
    polyLineBuffer: ByteBuffer,
    sectionPropsPtr: CPointer,
    distance: Float
): CPointer
