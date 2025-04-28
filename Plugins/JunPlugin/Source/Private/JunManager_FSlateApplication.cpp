#include "JunManager.h"
#include "Framework/Application/SlateApplication.h"

void FJunManager::RegisterNatives_FSlateApplication()
{
	RegisterNative<+[](jint UserIndex, FJunSharedRef* WidgetToFocus, EFocusCause ReasonFocusIsChanging) -> jboolean {
		return U2J(FSlateApplication::Get().SetUserFocus(UserIndex, J2U<TSharedRef<SWidget>>(WidgetToFocus), ReasonFocusIsChanging));
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateApplicationKt",
		"setUserFocus",
		"(IJB)Z"
	);

	RegisterNative<+[](jint UserIndex, EFocusCause ReasonFocusIsChanging) -> void {
		return FSlateApplication::Get().SetUserFocusToGameViewport(UserIndex, ReasonFocusIsChanging);
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateApplicationKt",
		"setUserFocusToGameViewport",
		"(IB)V"
	);
}
