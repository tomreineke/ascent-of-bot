#include "JunManager.h"

void FJunManager::RegisterNatives_FViewport()
{
	RegisterNative<+[](FViewport* Viewport) -> jobject {
		return U2J(Viewport->GetInitialPositionXY());
	}>(
		"com.cerebrallychallenged.jun.unreal.FViewportKt",
		"getInitialPositionXY",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2i;"
	);
}