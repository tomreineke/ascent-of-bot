#include "JunManager.h"

void FJunManager::RegisterNatives_ULocalLightComponent()
{
	RegisterNative<+[](ULocalLightComponent* Component) -> ELightUnits {
		return Component->IntensityUnits;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULocalLightComponentKt",
		"getIntensityUnits",
		"(J)B"
	);

	RegisterNative<+[](ULocalLightComponent* Component, ELightUnits NewValue) -> void {
		Component->SetIntensityUnits(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULocalLightComponentKt",
		"setIntensityUnits",
		"(JB)V"
	);
}