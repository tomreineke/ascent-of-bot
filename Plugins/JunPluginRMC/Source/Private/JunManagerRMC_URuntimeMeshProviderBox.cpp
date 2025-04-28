#include "JunManagerRMC.h"
#include "Providers/RuntimeMeshProviderBox.h"

void FJunManagerRMC::RegisterNatives_URuntimeMeshProviderBox()
{
	RegisterNative<+[](URuntimeMeshProviderBox* Provider) -> jobject {
		return U2J(Provider->GetBoxRadius());
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderBoxKt",
		"getBoxRadius",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](URuntimeMeshProviderBox* Provider) -> UMaterialInterface* {
		return Provider->GetBoxMaterial();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderBoxKt",
		"getBoxMaterial",
		"(J)J"
	);

	RegisterNative<+[](URuntimeMeshProviderBox* Provider, jobject NewValue) -> void {
		Provider->SetBoxRadius(J2U<FVector>(NewValue));
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderBoxKt",
		"setBoxRadius",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](URuntimeMeshProviderBox* Provider, UMaterialInterface* Material) -> void {
		Provider->SetBoxMaterial(Material);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.provider.URuntimeMeshProviderBoxKt",
		"setBoxMaterial",
		"(JJ)V"
	);
}