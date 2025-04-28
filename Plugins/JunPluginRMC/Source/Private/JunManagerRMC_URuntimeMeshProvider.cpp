#include "JunManagerRMC.h"
#include "RuntimeMeshProvider.h"

void FJunManagerRMC::RegisterNatives_URuntimeMeshProvider()
{
	RegisterNative<+[](URuntimeMeshProvider* Provider) -> void {
		Provider->Initialize();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"initialize",
		"(J)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint LODIndex, jint SectionId, FJunSharedRef* SectionProperties) -> void {
		Provider->CreateSection(LODIndex, SectionId, *J2U<TSharedRef<FRuntimeMeshSectionProperties>>(SectionProperties));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"createSection",
		"(JIIJ)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jstring SlotNameJ) -> jint {
		FJunString SlotName = SlotNameJ;
		return Provider->GetMaterialIndex(SlotName);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"getMaterialIndex",
		"(JLjava/lang/String;)I"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider) -> void {
		Provider->MarkAllLODsDirty();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"markAllLODsDirty",
		"(J)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider) -> void {
		Provider->MarkCollisionDirty();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"markCollisionDirty",
		"(J)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint LODIndex) -> void {
		Provider->MarkLODDirty(LODIndex);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"markLODDirty",
		"(JI)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint LODIndex, jint SectionId) -> void {
		Provider->MarkSectionDirty(LODIndex, SectionId);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"markSectionDirty",
		"(JII)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint LODIndex, jint SectionId) -> void {
		Provider->RemoveSection(LODIndex, SectionId);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"removeSection",
		"(JII)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint LODIndex, jint SectionId, jboolean CastsShadow) -> void {
		Provider->SetSectionCastsShadow(LODIndex, SectionId, J2U<bool>(CastsShadow));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"setSectionCastsShadow",
		"(JIIZ)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint LODIndex, jint SectionId, jboolean IsVisible) -> void {
		Provider->SetSectionVisibility(LODIndex, SectionId, J2U<bool>(IsVisible));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"setSectionVisibility",
		"(JIIZ)V"
	);

	RegisterNative<+[](URuntimeMeshProvider* Provider, jint MaterialSlot, jstring SlotNameJ, UMaterialInterface* Material) -> void {
		FJunString SlotName = SlotNameJ;
		Provider->SetupMaterialSlot(MaterialSlot, SlotName, Material);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderKt",
		"setupMaterialSlot",
		"(JILjava/lang/String;J)V"
	);
}