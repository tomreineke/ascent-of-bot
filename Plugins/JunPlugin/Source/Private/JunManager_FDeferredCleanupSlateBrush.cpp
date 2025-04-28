#include "JunManager.h"

void FJunManager::RegisterNatives_FDeferredCleanupSlateBrush()
{
	RegisterNative<+[](UTexture2D* Texture) -> FJunSharedRef* {
		return U2J(FDeferredCleanupSlateBrush::CreateBrush(Texture));
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FDeferredCleanupSlateBrushKt",
		"createBrush",
		"(J)J"
	);
}