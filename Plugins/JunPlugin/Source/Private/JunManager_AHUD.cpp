#include "JunManager.h"
#include "GameFramework/HUD.h"

void FJunManager::RegisterNatives_AHUD()
{
	RegisterNative<+[](AHUD* HUD, jstring TextJ, jobject TextColor, jfloat ScreenX, jfloat ScreenY, UFont* Font, jfloat Scale, jboolean ScalePosition) -> void {
		FString Text = J2U<FString>(TextJ);
		HUD->DrawText(Text, J2U<FLinearColor>(TextColor), ScreenX, ScreenY, Font, Scale, ScalePosition != 0);
	}>(
		"com.cerebrallychallenged.jun.unreal.AHUDKt",
		"drawText",
		"(JLjava/lang/String;Lcom/cerebrallychallenged/jun/math/geo/Vec4f;FFJFZ)V"
	);
}