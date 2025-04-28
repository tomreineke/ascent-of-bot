package com.cerebrallychallenged.jun.rmc

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.math.geo.Vec3i
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.math.FBoxSphereBounds
import com.cerebrallychallenged.jun.unreal.rmc.AttributeKind
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshData
import com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionProperties
import com.cerebrallychallenged.jun.unreal.rmc.memorySegment
import com.cerebrallychallenged.jun.unreal.rmc.numTexCoords
import com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStatic
import com.cerebrallychallenged.jun.unreal.rmc.setNum
import com.cerebrallychallenged.jun.unreal.rmc.useHighPrecisionTangents
import com.cerebrallychallenged.jun.unreal.rmc.useHighPrecisionTexCoords
import com.cerebrallychallenged.jun.unreal.rmc.wants32BitIndices
import com.cerebrallychallenged.jun.util.buffers.LayoutFColor
import com.cerebrallychallenged.jun.util.buffers.LayoutVec3f
import com.cerebrallychallenged.jun.util.buffers.LayoutVec3i
import com.cerebrallychallenged.jun.util.buffers.LayoutVec3i16
import com.cerebrallychallenged.jun.util.buffers.Memory
import com.cerebrallychallenged.jun.util.buffers.view
import com.cerebrallychallenged.jun.util.buffers.withLayout
import com.cerebrallychallenged.jun.util.confinedArena

/**
 * High-level wrapper around [FRuntimeMeshRenderableMeshData].
 */
class MeshData internal constructor(
    val properties: TSharedRef<FRuntimeMeshSectionProperties>,
    val vertexCount: Int
) {
    internal val data = FRuntimeMeshRenderableMeshData.makeShared(properties)

    private val useHighPrecisionTangents = properties.useHighPrecisionTangents

    private val useHighPrecisionTexCoords = properties.useHighPrecisionTexCoords

    private val useHighPrecisionIndices = properties.wants32BitIndices

    private val channelCount = properties.numTexCoords

    init {
        data.setNum(AttributeKind.Positions, vertexCount)
        data.setNum(AttributeKind.Tangents, vertexCount)
        data.setNum(AttributeKind.TexCoords, vertexCount)
        data.setNum(AttributeKind.Colors, vertexCount)
    }

    fun updatePositions(update: (position: Memory<LayoutVec3f>) -> Unit) {
        confinedArena {
            update(data.memorySegment(AttributeKind.Positions).withLayout(LayoutVec3f))
        }
    }

    fun updateTangents(update: (tangent: Memory.View<Vec3f>, normal: Memory.View<Vec3f>) -> Unit) {
        confinedArena {
            val segment = data.memorySegment(AttributeKind.Tangents)
            if (useHighPrecisionTangents) {
                val memory = segment.withLayout(LayoutFPackedRGBA16N)
                update(memory.tangent, memory.normal)
            } else {
                val memory = segment.withLayout(LayoutFPackedNormal)
                update(memory.tangent, memory.normal)
            }
        }
    }

    fun updateTexCoords(update: (channel: List<Memory.View<Vec2f>>) -> Unit) {
        if (channelCount == 0) return
        confinedArena {
            val segment = data.memorySegment(AttributeKind.TexCoords)
            if (useHighPrecisionTexCoords) {
                val memory = segment.withLayout(LayoutTexCoordF32List[channelCount - 1])
                update((0 until channelCount).map { channel -> memory.channel(channel) })
            } else {
                val memory = segment.withLayout(LayoutTexCoordF16List[channelCount - 1])
                update((0 until channelCount).map { channel -> memory.channel(channel) })
            }
        }
    }

    fun updateColors(update: (color: Memory<LayoutFColor>) -> Unit) {
        confinedArena {
            update(data.memorySegment(AttributeKind.Colors).withLayout(LayoutFColor))
        }
    }

    fun updateTriangles(triangleCount: Int?, update: ((triangle: Memory.View<Vec3i>) -> Unit)? = null) {
        if (triangleCount != null) {
            data.setNum(AttributeKind.Triangles, triangleCount)
        }
        if (update != null) {
            confinedArena {
                val segment = data.memorySegment(AttributeKind.Triangles)
                update(
                    if (useHighPrecisionIndices) {
                        segment.withLayout(LayoutVec3i).view()
                    } else {
                        segment.withLayout(LayoutVec3i16).view()
                    }
                )
            }
        }
    }

    fun updateAdjacencyTriangles(triangleCount: Int?, update: ((triangle: Memory.View<Vec3i>) -> Unit)? = null) {
        if (triangleCount != null) {
            data.setNum(AttributeKind.AdjacencyTriangles, triangleCount)
        }
        if (update != null) {
            confinedArena {
                val segment = data.memorySegment(AttributeKind.AdjacencyTriangles)
                update(
                    if (useHighPrecisionIndices) {
                        segment.withLayout(LayoutVec3i).view()
                    } else {
                        segment.withLayout(LayoutVec3i16).view()
                    }
                )
            }
        }
    }
}

fun TSharedRef<FRuntimeMeshSectionProperties>.createMeshData(vertexCount: Int): MeshData =
        MeshData(this, vertexCount)

fun URuntimeMeshProviderStatic.updateSection(
        lodIndex: Int,
        sectionId: Int,
        sectionData: MeshData,
        knownBounds: FBoxSphereBounds? = null
) {
    updateSection(lodIndex, sectionId, sectionData.data, knownBounds)
}

fun URuntimeMeshProviderStatic.createSection(
        lodIndex: Int,
        sectionId: Int,
        sectionProperties: TSharedRef<FRuntimeMeshSectionProperties>,
        sectionData: MeshData,
        knownBounds: FBoxSphereBounds? = null,
        createCollision: Boolean = true
) {
    createSection(lodIndex, sectionId, sectionProperties, sectionData.data, knownBounds, createCollision)
}
