#include "JunManagerRMC.h"
#include "RuntimeMesh.h"

void FJunManagerRMC::RegisterNatives_URuntimeMesh()
{
	RegisterNative<+[](URuntimeMesh* Mesh) -> UBodySetup* {
		return Mesh->GetBodySetup();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"getBodySetup",
		"(J)J"
	);

	RegisterNative<+[](URuntimeMesh* Mesh) -> jobject {
		return U2J(Mesh->GetLocalBounds());
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"getLocalBounds",
		"(J)Lcom/cerebrallychallenged/jun/unreal/math/FBoxSphereBounds;"
	);

	RegisterNative<+[](URuntimeMesh* Mesh, jint SlotIndex) -> UMaterialInterface* {
		return Mesh->GetMaterial(SlotIndex);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"getMaterial",
		"(JI)J"
	);

	RegisterNative<+[](URuntimeMesh* Mesh) -> jint {
		return Mesh->GetNumMaterials();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"getNumMaterials",
		"(J)I"
	);

	RegisterNative<+[](URuntimeMesh* Mesh, URuntimeMeshProvider* Provider) -> void {
		return Mesh->Initialize(Provider);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"initialize",
		"(JJ)V"
	);

	RegisterNative<+[](URuntimeMesh* Mesh, jstring SlotNameJ) -> jboolean {
		FJunString SlotName = SlotNameJ;
		return U2J(Mesh->IsMaterialSlotNameValid(SlotName));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"isMaterialSlotNameValid",
		"(JLjava/lang/String;)Z"
	);

	RegisterNative<+[](URuntimeMesh* Mesh) -> void {
		return Mesh->Reset();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"reset",
		"(J)V"
	);

	RegisterNative<+[](URuntimeMesh* Mesh, jint MaterialSlot, jstring SlotNameJ, UMaterialInterface* Material) -> void {
		FJunString SlotName = SlotNameJ;
		Mesh->SetupMaterialSlot(MaterialSlot, SlotName, Material);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshKt",
		"setupMaterialSlot",
		"(JILjava/lang/String;J)V"
	);
}