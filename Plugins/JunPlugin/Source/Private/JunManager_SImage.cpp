#include "JunManager.h"

void FJunManager::RegisterNatives_SImage()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(SNew(SImage));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SImageKt",
		"createBySNewImpl",
		"()J"
	);

	RegisterNative<+[](SImage* Image, FSlateBrush* Brush) -> void {
		Image->SetImage(Brush);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.SImageKt",
		"setImage",
		"(JJ)V"
	);
}