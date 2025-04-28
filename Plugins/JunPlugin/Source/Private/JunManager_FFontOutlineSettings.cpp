#include "JunManager.h"

void FJunManager::RegisterNatives_FFontOutlineSettings()
{
	RegisterNative<+[](jint OutlineSize, jobject OutlineColor) -> FJunSharedRef* {
		return U2J(MakeShared<FFontOutlineSettings>(OutlineSize, J2U<FLinearColor>(OutlineColor)));
	}>(
		"com.cerebrallychallenged.jun.unreal.font.FFontOutlineSettingsKt",
		"makeShared",
		"(ILcom/cerebrallychallenged/jun/math/geo/Vec4f;)J"
	);
}