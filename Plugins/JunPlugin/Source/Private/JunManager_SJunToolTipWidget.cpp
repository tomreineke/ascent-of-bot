#include "JunManager.h"

void FJunManager::RegisterNatives_SJunToolTipWidget()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(SNew(SJunToolTipWidget).LifeGuard(GJunManager->LifeGuard));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SJunToolTipWidgetKt",
		"createBySNewImpl",
		"()J"
	);

	RegisterNative<+[](SJunToolTipWidget* Widget) -> double {
		return Widget->GetDelay();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SJunToolTipWidgetKt",
		"getDelay",
		"(J)D"
	);

	RegisterNative<+[](SJunToolTipWidget* Widget, jdouble Delay) -> void {
		Widget->SetDelay(Delay);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SJunToolTipWidgetKt",
		"setDelay",
		"(JD)V"
	);
}