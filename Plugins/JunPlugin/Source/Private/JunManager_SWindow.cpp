#include "JunManager.h"
#include "Widgets/SWindow.h"

void FJunManager::RegisterNatives_SWindow()
{
	RegisterNative<+[](SWindow* Window) -> FJunSharedRef* {
		return U2J(Window->GetNativeWindow());
	}>(
		"com.cerebrallychallenged.jun.unreal.SWindowKt",
		"getNativeWindow",
		"(J)J"
	);
}