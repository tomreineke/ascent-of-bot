#include "JunManager.h"

void FJunManager::RegisterNatives_SBorder()
{
	RegisterNative<+[](SBorder* Border) -> FJunSharedRef* {
		return U2J(Border->GetContent());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SBorderKt",
		"getContent",
		"(J)J"
	);

	RegisterNative<+[](SBorder* Border, FJunSharedRef* Content) -> void {
		Border->SetContent(J2U<TSharedRef<SWidget>>(Content));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SBorderKt",
		"setContent",
		"(JJ)V"
	);
}