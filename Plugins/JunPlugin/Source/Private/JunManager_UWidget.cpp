#include "JunManager.h"
#include "Components/Widget.h"

void FJunManager::RegisterNatives_UWidget()
{
	RegisterNative<+[](UWidget* Widget) -> void {
		Widget->RemoveFromParent();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetKt",
		"removeFromParent",
		"(J)V"
	);

	RegisterNative<+[](UWidget* Widget) -> void {
		Widget->SetKeyboardFocus();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetKt",
		"setKeyboardFocus",
		"(J)V"
	);
}
