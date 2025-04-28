#include "JunManager.h"

void FJunManager::RegisterNatives_SButton()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(SNew(SButton));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SButtonKt",
		"createBySNewImpl",
		"()J"
	);
}