#include "JunManager.h"
#include "Blueprint/UserWidget.h"

void FJunManager::RegisterNatives_UUserWidget()
{
	RegisterNative<+[](APlayerController* OwningPlayer, UClass* UserWidgetClass) -> UUserWidget* {
		return CreateWidget<UUserWidget>(OwningPlayer, UserWidgetClass);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UUserWidgetKt",
		"createWidget",
		"(JJ)J"
	);

	RegisterNative<+[](UUserWidget* UserWidget, jint ZOrder) -> void {
		UserWidget->AddToViewport(ZOrder);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UUserWidgetKt",
		"addToViewport",
		"(JI)V"
	);
}
