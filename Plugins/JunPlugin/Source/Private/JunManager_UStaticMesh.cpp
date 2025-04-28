#include "JunManager.h"

void FJunManager::RegisterNatives_UStaticMesh()
{
	RegisterNative<+[](UStaticMesh* Mesh, jint Index) -> UMaterialInterface* {
		return Mesh->GetMaterial(Index);
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.UStaticMeshKt",
		"getMaterial",
		"(JI)J"
	);

	RegisterNative<+[](UStaticMesh* Mesh) -> jint {
		return Mesh->GetStaticMaterials().Num();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.UStaticMeshKt",
		"getNumMaterials",
		"(J)I"
	);
}