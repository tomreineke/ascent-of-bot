#include "JunManager.h"

void FJunManager::RegisterNatives_SBox()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(SNew(SBox));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SBoxKt",
		"createBySNewImpl",
		"()J"
	);

	RegisterNative<+[](SBox* Box, FJunSharedRef* Content) -> void {
		Box->SetContent(J2U<TSharedRef<SWidget>>(Content));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SBoxKt",
		"setContent",
		"(JJ)V"
	);
}