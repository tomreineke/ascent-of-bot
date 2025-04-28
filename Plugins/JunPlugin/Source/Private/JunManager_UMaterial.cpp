#include "JunManager.h"

void FJunManager::RegisterNatives_UMaterial()
{
	RegisterNative<+[](EMaterialDomain Domain) -> UMaterial* {
		return UMaterial::GetDefaultMaterial(Domain);
	}>(
		"com.cerebrallychallenged.jun.unreal.material.UMaterialKt",
		"getDefaultMaterial",
		"(I)J"
	);
}