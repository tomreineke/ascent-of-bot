#include "JunManager.h"

void FJunManager::RegisterNatives_UEngine()
{
	RegisterNative<+[](UEngine* Engine) -> UGameViewportClient* {
		return Engine->GameViewport;
	}>(
		"com.cerebrallychallenged.jun.unreal.UEngineKt",
		"getGameViewport",
		"(J)J"
	);
}