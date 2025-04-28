#include "JunManager.h"

void FJunManager::RegisterNatives_ULightComponentBase()
{
	RegisterNative<+[](ULightComponentBase* Component) -> jboolean {
		return U2J(Component->CastShadows);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentBaseKt",
		"getCastShadows",
		"(J)Z"
	);

	RegisterNative<+[](ULightComponentBase* Component, jboolean NewValue) -> void {
		Component->SetCastShadows(J2U<bool>(NewValue));
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentBaseKt",
		"setCastShadows",
		"(JZ)V"
	);

	RegisterNative<+[](ULightComponentBase* Component) -> jfloat {
		return Component->Intensity;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentBaseKt",
		"getIntensity",
		"(J)F"
	);
}