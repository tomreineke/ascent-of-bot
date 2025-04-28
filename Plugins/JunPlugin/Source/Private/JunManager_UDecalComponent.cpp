#include "JunManager.h"
#include "Components/DecalComponent.h"

void FJunManager::RegisterNatives_UDecalComponent()
{
	RegisterNative<+[](UDecalComponent* Component) -> UMaterialInterface* {
		return Component->GetDecalMaterial();
	}>(
		"com.cerebrallychallenged.jun.unreal.decal.UDecalComponentKt",
		"getDecalMaterial",
		"(J)J"
	);

	RegisterNative<+[](UDecalComponent* Component, UMaterialInterface* Material) -> void {
		Component->SetDecalMaterial(Material);
	}>(
		"com.cerebrallychallenged.jun.unreal.decal.UDecalComponentKt",
		"setDecalMaterial",
		"(JJ)V"
	);
}