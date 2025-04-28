package com.cerebrallychallenged.jun.unreal.rmc

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

interface FRuntimeMeshRenderableMeshData {
    companion object {
        fun makeShared(
                wantsHighPrecisionTangents: Boolean = false,
                wantsHighPrecisionTexCoords: Boolean = false,
                numTexCoords: Int = 1,
                wants32BitIndices: Boolean = false
        ): TSharedRef<FRuntimeMeshRenderableMeshData> = makeSharedImpl(
                wantsHighPrecisionTangents,
                wantsHighPrecisionTexCoords,
                numTexCoords.toByte(),
                wants32BitIndices
        ).wrapSharedRef()

        fun makeShared(
                sectionProps: TSharedRef<FRuntimeMeshSectionProperties>
        ): TSharedRef<FRuntimeMeshRenderableMeshData> = makeSharedImpl(sectionProps.directPtr).wrapSharedRef()
    }
}

context(Arena)
internal fun AnyRef<FRuntimeMeshRenderableMeshData>.memorySegment(kind: AttributeKind): MemorySegment =
    MemorySegment.ofAddress(getData(directPtr, kind.ordinal))
        .reinterpret(getByteSize(directPtr, kind.ordinal), this@Arena, null)

internal fun AnyRef<FRuntimeMeshRenderableMeshData>.setNum(kind: AttributeKind, newNum: Int) {
    setNum(directPtr, kind.ordinal, newNum)
}

internal enum class AttributeKind {
    Positions,
    Tangents,
    TexCoords,
    Colors,
    Triangles,
    AdjacencyTriangles
}

private external fun makeSharedImpl(sectionPropsPtr: CPointer): CPointer

private external fun makeSharedImpl(
        wantsHighPrecisionTangents: Boolean,
        wantsHighPrecisionTexCoords: Boolean,
        numTexCoords: Byte,
        wants32BitIndices: Boolean
): CPointer

@Convenience
private external fun getByteSize(ptr: CPointer, attributeKindMagic: Int): Long

@Convenience
private external fun getData(ptr: CPointer, attributeKindMagic: Int): Long

@Convenience
private external fun setNum(ptr: CPointer, attributeKindMagic: Int, newNum: Int)
