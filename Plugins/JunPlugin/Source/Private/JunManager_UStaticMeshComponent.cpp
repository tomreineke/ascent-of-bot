#include "JunManager.h"

void FJunManager::RegisterNatives_UStaticMeshComponent()

{
	RegisterNative<+[](UStaticMeshComponent* Component) -> UStaticMesh* {
		return Component->GetStaticMesh();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.UStaticMeshComponentKt",
		"getStaticMesh",
		"(J)J"
	);

	RegisterNative<+[](UStaticMeshComponent* Component, UStaticMesh* NewMesh) -> void {
		Component->SetStaticMesh(NewMesh);
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.UStaticMeshComponentKt",
		"setStaticMesh",
		"(JJ)V"
	);
}