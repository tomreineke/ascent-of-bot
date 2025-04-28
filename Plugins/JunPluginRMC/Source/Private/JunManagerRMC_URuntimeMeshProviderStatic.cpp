#include "JunManagerRMC.h"
#include "Providers/RuntimeMeshProviderStatic.h"

void FJunManagerRMC::RegisterNatives_URuntimeMeshProviderStatic()
{
	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex) -> void {
		Provider->ClearSection(LODIndex, SectionIndex);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"clearSection",
		"(JII)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionId, FJunSharedRef* SectionProperties, jboolean CreateCollision) -> void {
		Provider->CreateSection(LODIndex, SectionId, *J2U<TSharedRef<FRuntimeMeshSectionProperties>>(SectionProperties), J2U<bool>(CreateCollision));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"createSection",
		"(JIIJZ)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionId, FJunSharedRef* SectionProperties, FJunSharedRef* SectionData, jobject KnownBounds, jboolean CreateCollision) -> void {
		if (KnownBounds == nullptr)
		{
			Provider->CreateSection(
				LODIndex,
				SectionId,
				*J2U<TSharedRef<FRuntimeMeshSectionProperties>>(SectionProperties),
				*J2U<TSharedRef<FRuntimeMeshRenderableMeshData>>(SectionData),
				J2U<bool>(CreateCollision)
			);
		}
		else {
			Provider->CreateSection(
				LODIndex,
				SectionId,
				*J2U<TSharedRef<FRuntimeMeshSectionProperties>>(SectionProperties),
				*J2U<TSharedRef<FRuntimeMeshRenderableMeshData>>(SectionData),
				J2U<FBoxSphereBounds>(KnownBounds),
				J2U<bool>(CreateCollision)
			);
		}
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"createSection",
		"(JIIJJLcom/cerebrallychallenged/jun/unreal/math/FBoxSphereBounds;Z)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jint MaterialSlot, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject UV1, jobject UV2, jobject UV3, jobject VertexColors, jobject Tangents, ERuntimeMeshUpdateFrequency UpdateFrequency, jboolean CreateCollision) -> void {
		Provider->CreateSectionFromComponents(
			LODIndex,
			SectionIndex,
			MaterialSlot,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FVector2D>>(UV1),
			J2U<TArray<FVector2D>>(UV2),
			J2U<TArray<FVector2D>>(UV3),
			J2U<TArray<FColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents),
			UpdateFrequency,
			J2U<bool>(CreateCollision)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"createSectionFromComponentsWithFColor",
		"(JIIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;BZ)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jint MaterialSlot, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject VertexColors, jobject Tangents, ERuntimeMeshUpdateFrequency UpdateFrequency, jboolean CreateCollision) -> void {
		Provider->CreateSectionFromComponents(
			LODIndex,
			SectionIndex,
			MaterialSlot,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents),
			UpdateFrequency,
			J2U<bool>(CreateCollision)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"createSectionFromComponentsWithFColor",
		"(JIIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;BZ)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jint MaterialSlot, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject UV1, jobject UV2, jobject UV3, jobject VertexColors, jobject Tangents, ERuntimeMeshUpdateFrequency UpdateFrequency, jboolean CreateCollision) -> void {
		Provider->CreateSectionFromComponents(
			LODIndex,
			SectionIndex,
			MaterialSlot,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FVector2D>>(UV1),
			J2U<TArray<FVector2D>>(UV2),
			J2U<TArray<FVector2D>>(UV3),
			J2U<TArray<FLinearColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents),
			UpdateFrequency,
			J2U<bool>(CreateCollision)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"createSectionFromComponentsWithFLinearColor",
		"(JIIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;BZ)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jint MaterialSlot, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject VertexColors, jobject Tangents, ERuntimeMeshUpdateFrequency UpdateFrequency, jboolean CreateCollision) -> void {
		Provider->CreateSectionFromComponents(
			LODIndex,
			SectionIndex,
			MaterialSlot,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FLinearColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents),
			UpdateFrequency,
			J2U<bool>(CreateCollision)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"createSectionFromComponentsWithFLinearColor",
		"(JIIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;BZ)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject UV1, jobject UV2, jobject UV3, jobject VertexColors, jobject Tangents) -> void {
		Provider->UpdateSectionFromComponents(
			LODIndex,
			SectionIndex,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FVector2D>>(UV1),
			J2U<TArray<FVector2D>>(UV2),
			J2U<TArray<FVector2D>>(UV3),
			J2U<TArray<FColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"updateSectionFromComponentsWithFColor",
		"(JIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject VertexColors, jobject Tangents) -> void {
		Provider->UpdateSectionFromComponents(
			LODIndex,
			SectionIndex,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"updateSectionFromComponentsWithFColor",
		"(JIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject UV1, jobject UV2, jobject UV3, jobject VertexColors, jobject Tangents) -> void {
		Provider->UpdateSectionFromComponents(
			LODIndex,
			SectionIndex,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FVector2D>>(UV1),
			J2U<TArray<FVector2D>>(UV2),
			J2U<TArray<FVector2D>>(UV3),
			J2U<TArray<FLinearColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"updateSectionFromComponentsWithFLinearColor",
		"(JIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)V"
	);

	RegisterNative<+[](URuntimeMeshProviderStatic* Provider, jint LODIndex, jint SectionIndex, jobject Vertices, jobject Triangles, jobject Normals, jobject UV0, jobject VertexColors, jobject Tangents) -> void {
		Provider->UpdateSectionFromComponents(
			LODIndex,
			SectionIndex,
			J2U<TArray<FVector>>(Vertices),
			J2U<TArray<int>>(Triangles),
			J2U<TArray<FVector>>(Normals),
			J2U<TArray<FVector2D>>(UV0),
			J2U<TArray<FLinearColor>>(VertexColors),
			J2U<TArray<FRuntimeMeshTangent>>(Tangents)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderStaticKt",
		"updateSectionFromComponentsWithFLinearColor",
		"(JIILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)V"
	);
}