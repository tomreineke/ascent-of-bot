package com.cerebrallychallenged.jun.unreal.rmc.provider

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.math.FBoxSphereBounds
import com.cerebrallychallenged.jun.unreal.rmc.*
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.buffers.*
import java.nio.ByteBuffer

open class URuntimeMeshProviderStatic(ptr: CPointer) : URuntimeMeshProvider(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun clearSection(lodIndex: Int, sectionIndex: Int) = clearSection(ptr, lodIndex, sectionIndex)

    fun createSection(
            lodIndex: Int,
            sectionId: Int,
            sectionProperties: TSharedRef<FRuntimeMeshSectionProperties>,
            createCollision: Boolean = true
    ) {
        createSection(ptr, lodIndex, sectionId, sectionProperties.sharedPtrPtr, createCollision)
    }

    fun createSection(
            lodIndex: Int,
            sectionId: Int,
            sectionProperties: TSharedRef<FRuntimeMeshSectionProperties>,
            sectionData: TSharedRef<FRuntimeMeshRenderableMeshData>,
            knownBounds: FBoxSphereBounds? = null,
            createCollision: Boolean = true
    ) {
        createSection(
                ptr,
                lodIndex,
                sectionId,
                sectionProperties.sharedPtrPtr,
                sectionData.sharedPtrPtr,
                knownBounds,
                createCollision
        )
    }

    @JvmName("createSectionFromComponentsWithFColor")
    fun createSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            materialSlot: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv1: Memory<LayoutVec2f>,
            uv2: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv3: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFColor>,
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory(),
            updateFrequency: ERuntimeMeshUpdateFrequency = ERuntimeMeshUpdateFrequency.Infrequent,
            createCollision: Boolean = true
    ) = createSectionFromComponentsWithFColor(
            ptr,
            lodIndex,
            sectionIndex,
            materialSlot,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            uv1.segment.asByteBuffer(),
            uv2.segment.asByteBuffer(),
            uv3.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer(),
            updateFrequency.ordinal.toByte(),
            createCollision
    )


    @JvmName("createSectionFromComponentsWithFColorSingleUV")
    fun createSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            materialSlot: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFColor>,
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory(),
            updateFrequency: ERuntimeMeshUpdateFrequency = ERuntimeMeshUpdateFrequency.Infrequent,
            createCollision: Boolean = true
    ): Unit = createSectionFromComponentsWithFColor(
            ptr,
            lodIndex,
            sectionIndex,
            materialSlot,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer(),
            updateFrequency.ordinal.toByte(),
            createCollision
    )

    fun createSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            materialSlot: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv1: Memory<LayoutVec2f>,
            uv2: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv3: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFLinearColor> = LayoutFLinearColor.emptyMemory(),
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory(),
            updateFrequency: ERuntimeMeshUpdateFrequency = ERuntimeMeshUpdateFrequency.Infrequent,
            createCollision: Boolean = true
    ) = createSectionFromComponentsWithFLinearColor(
            ptr,
            lodIndex,
            sectionIndex,
            materialSlot,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            uv1.segment.asByteBuffer(),
            uv2.segment.asByteBuffer(),
            uv3.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer(),
            updateFrequency.ordinal.toByte(),
            createCollision
    )

    @JvmName("createSectionFromComponentsWithFLinearColorSingleUV")
    fun createSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            materialSlot: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFLinearColor> = LayoutFLinearColor.emptyMemory(),
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory(),
            updateFrequency: ERuntimeMeshUpdateFrequency = ERuntimeMeshUpdateFrequency.Infrequent,
            createCollision: Boolean = true
    ) = createSectionFromComponentsWithFLinearColor(
            ptr,
            lodIndex,
            sectionIndex,
            materialSlot,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer(),
            updateFrequency.ordinal.toByte(),
            createCollision
    )

    fun updateSection(
            lodIndex: Int,
            sectionId: Int,
            sectionData: TSharedRef<FRuntimeMeshRenderableMeshData>,
            knownBounds: FBoxSphereBounds? = null
    ) {
        updateSection(ptr, lodIndex, sectionId, sectionData.sharedPtrPtr, knownBounds)
    }

    @JvmName("updateSectionFromComponentsWithFColor")
    fun updateSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv1: Memory<LayoutVec2f>,
            uv2: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv3: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFColor>,
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory()
    ): Unit = updateSectionFromComponentsWithFColor(
            ptr,
            lodIndex,
            sectionIndex,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            uv1.segment.asByteBuffer(),
            uv2.segment.asByteBuffer(),
            uv3.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer()
    )

    @JvmName("updateSectionFromComponentsWithFColorSingleUV")
    fun updateSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFColor>,
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory()
    ): Unit = updateSectionFromComponentsWithFColor(
            ptr,
            lodIndex,
            sectionIndex,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer()
    )

    fun updateSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv1: Memory<LayoutVec2f>,
            uv2: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            uv3: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFLinearColor> = LayoutFLinearColor.emptyMemory(),
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory()
    ) = updateSectionFromComponentsWithFLinearColor(
            ptr,
            lodIndex,
            sectionIndex,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            uv1.segment.asByteBuffer(),
            uv2.segment.asByteBuffer(),
            uv3.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer()
    )

    @JvmName("updateSectionFromComponentsWithFLinearColorSingleUV")
    fun updateSectionFromComponents(
            lodIndex: Int,
            sectionIndex: Int,
            vertices: Memory<LayoutVec3f>,
            triangles: Memory<LayoutVec3i>,
            normals: Memory<LayoutVec3f> = LayoutVec3f.emptyMemory(),
            uv0: Memory<LayoutVec2f> = LayoutVec2f.emptyMemory(),
            vertexColors: Memory<LayoutFLinearColor> = LayoutFLinearColor.emptyMemory(),
            tangents: Memory<LayoutFRuntimeMeshTangent> = LayoutFRuntimeMeshTangent.emptyMemory()
    ) = updateSectionFromComponentsWithFLinearColor(
            ptr,
            lodIndex,
            sectionIndex,
            vertices.segment.asByteBuffer(),
            triangles.segment.asByteBuffer(),
            normals.segment.asByteBuffer(),
            uv0.segment.asByteBuffer(),
            vertexColors.segment.asByteBuffer(),
            tangents.segment.asByteBuffer()
    )
}

private external fun clearSection(ptr: CPointer, lodIndex: Int, sectionIndex: Int)

private external fun createSection(
        ptr: CPointer,
        lodIndex: Int,
        sectionId: Int,
        sectionPropertiesPtr: CPointer,
        createCollision: Boolean
)

private external fun createSection(
        ptr: CPointer,
        lodIndex: Int,
        sectionId: Int,
        sectionPropertiesPtr: CPointer,
        sectionDataPtr: CPointer,
        knownBounds: FBoxSphereBounds?,
        createCollision: Boolean
)

private external fun createSectionFromComponentsWithFColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        materialSlot: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        uv1: ByteBuffer,
        uv2: ByteBuffer,
        uv3: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer,
        updateFrequency: Byte,
        createCollision: Boolean
)

private external fun createSectionFromComponentsWithFColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        materialSlot: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer,
        updateFrequency: Byte,
        createCollision: Boolean
)

private external fun createSectionFromComponentsWithFLinearColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        materialSlot: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        uv1: ByteBuffer,
        uv2: ByteBuffer,
        uv3: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer,
        updateFrequency: Byte,
        createCollision: Boolean
)



private external fun createSectionFromComponentsWithFLinearColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        materialSlot: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer,
        updateFrequency: Byte,
        createCollision: Boolean
)

private external fun updateSection(
        ptr: CPointer,
        lodIndex: Int,
        sectionId: Int,
        sectionDataPtr: CPointer,
        knownBounds: FBoxSphereBounds?
)

private external fun updateSectionFromComponentsWithFColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        uv1: ByteBuffer,
        uv2: ByteBuffer,
        uv3: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer
)

private external fun updateSectionFromComponentsWithFColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer
)

private external fun updateSectionFromComponentsWithFLinearColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        uv1: ByteBuffer,
        uv2: ByteBuffer,
        uv3: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer
)

private external fun updateSectionFromComponentsWithFLinearColor(
        ptr: CPointer,
        lodIndex: Int,
        sectionIndex: Int,
        vertices: ByteBuffer,
        triangles: ByteBuffer,
        normals: ByteBuffer,
        uv0: ByteBuffer,
        vertexColors: ByteBuffer,
        tangents: ByteBuffer
)
