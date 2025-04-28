#include "JunManager.h"

void FJunManager::RegisterNatives_FSoundAttenuationSettings()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(MakeShared<FSoundAttenuationSettings>());
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.FSoundAttenuationSettingsKt",
		"createSoundAttenuationSettings",
		"()J"
	);

	RegisterNative<+[](FSoundAttenuationSettings* Settings) -> jboolean {
		return U2J(Settings->bSpatialize);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.FSoundAttenuationSettingsKt",
		"getSpatialize",
		"(J)Z"
	);

	RegisterNative<+[](FSoundAttenuationSettings* Settings, jboolean Spatialize) -> void {
		Settings->bSpatialize = J2U<bool>(Spatialize);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.FSoundAttenuationSettingsKt",
		"setSpatialize",
		"(JZ)V"
	);
}
