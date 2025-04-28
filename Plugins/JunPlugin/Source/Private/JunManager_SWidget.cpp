#include "JunManager.h"

void FJunManager::RegisterNatives_SWidget()
{
	RegisterNative<+[](SWidget* Widget) -> jint {
		return U2J(Widget->GetVisibility());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SWidgetKt",
		"getVisibility",
		"(J)I"
	);

	RegisterNative<+[](SWidget* Widget, jint VisibilityMagic) -> void {
		Widget->SetVisibility(J2UEVisibility(VisibilityMagic));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SWidgetKt",
		"setVisibility",
		"(JI)V"
	);

	RegisterNative<+[](SWidget* Widget) -> jboolean {
		return U2J(Widget->SupportsKeyboardFocus());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SWidgetKt",
		"supportsKeyBoardFocus",
		"(J)Z"
	);
}