#include "JunManager.h"

void FJunManager::RegisterNatives_UTexture()
{
	RegisterNative<+[](UTexture* Texture) -> void {
		Texture->UpdateResource();
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextureKt",
		"updateResource",
		"(J)V"
	);
}