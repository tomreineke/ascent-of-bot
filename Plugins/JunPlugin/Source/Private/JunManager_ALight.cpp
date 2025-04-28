#include "JunManager.h"
#include "Engine/Light.h"

void FJunManager::RegisterNatives_ALight()
{
	RegisterNative<+[](ALight* Light) -> ULightComponent* {
		return Light->GetLightComponent();
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ALightKt",
		"getLightComponent",
		"(J)J"
	);
}