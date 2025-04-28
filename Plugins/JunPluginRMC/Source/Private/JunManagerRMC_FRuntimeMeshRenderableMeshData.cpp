#include "JunManagerRMC.h"
#include "RuntimeMeshRenderable.h"

void FJunManagerRMC::RegisterNatives_FRuntimeMeshRenderableMeshData()
{
	RegisterNative<+[](jboolean WantsHighPrecisionTangents, jboolean WantsHighPrecisionTexCoords, jbyte NumTexCoords, jboolean Wants32BitIndices) -> FJunSharedRef* {
		return U2J(MakeShared<FRuntimeMeshRenderableMeshData>(
			J2U<bool>(WantsHighPrecisionTangents),
			J2U<bool>(WantsHighPrecisionTexCoords),
			NumTexCoords,
			J2U<bool>(Wants32BitIndices)
		));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshDataKt",
		"makeSharedImpl",
		"(ZZBZ)J"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* SectionProps) -> FJunSharedRef* {
		return U2J(MakeShared<FRuntimeMeshRenderableMeshData>(*SectionProps));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshDataKt",
		"makeSharedImpl",
		"(J)J"
	);

	RegisterNative<+[](FRuntimeMeshRenderableMeshData* MeshData, jint AttributeKind) -> jlong {
		switch (AttributeKind)
		{
		case 0:
			return MeshData->Positions.Num() * sizeof(FVector);
		case 1:
			return MeshData->Tangents.Num() * 2 * (MeshData->Tangents.IsHighPrecision() ? sizeof(FPackedRGBA16N) : sizeof(FPackedNormal));
		case 2:
			return MeshData->TexCoords.Num() * (MeshData->TexCoords.IsHighPrecision() ? sizeof(FVector2D) : sizeof(FVector2DHalf));
		case 3:
			return MeshData->Colors.Num() * sizeof(FColor);
		case 4:
			return MeshData->Triangles.Num() * 3 * (MeshData->Triangles.IsHighPrecision() ? sizeof(uint32) : sizeof(uint16));
		case 5:
			return MeshData->AdjacencyTriangles.Num() * 3 * (MeshData->Triangles.IsHighPrecision() ? sizeof(uint32) : sizeof(uint16));
		default:
			return 0;
		}
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshDataKt",
		"getByteSize",
		"(JI)J"
	);

	RegisterNative<+[](FRuntimeMeshRenderableMeshData* MeshData, jint AttributeKind) -> uint8* {
		switch (AttributeKind)
		{
		case 0:
			return const_cast<uint8*>(MeshData->Positions.GetData());
		case 1:
			return const_cast<uint8*>(MeshData->Tangents.GetData());
		case 2:
			return const_cast<uint8*>(MeshData->TexCoords.GetData());
		case 3:
			return const_cast<uint8*>(MeshData->Colors.GetData());
		case 4:
			return const_cast<uint8*>(MeshData->Triangles.GetData());
		case 5:
			return const_cast<uint8*>(MeshData->AdjacencyTriangles.GetData());
		default:
			return nullptr;
		}
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshDataKt",
		"getData",
		"(JI)J"
	);

	RegisterNative<+[](FRuntimeMeshRenderableMeshData* MeshData, jint AttributeKind, jint NewNum) -> void {
		switch (AttributeKind)
		{
		case 0:
			MeshData->Positions.SetNum(NewNum);
			break;
		case 1:
			MeshData->Tangents.SetNum(NewNum);
			break;
		case 2:
			MeshData->TexCoords.SetNum(NewNum);
			break;
		case 3:
			MeshData->Colors.SetNum(NewNum);
			break;
		case 4:
			MeshData->Triangles.SetNum(3 * NewNum);
			break;
		case 5:
			MeshData->AdjacencyTriangles.SetNum(3 * NewNum);
			break;
		}
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshRenderableMeshDataKt",
		"setNum",
		"(JII)V"
	);
}