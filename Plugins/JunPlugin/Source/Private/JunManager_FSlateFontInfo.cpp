#include "JunManager.h"

void FJunManager::RegisterNatives_FSlateFontInfo()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(MakeShared<FSlateFontInfo>());
	}>(
		"com.cerebrallychallenged.jun.unreal.font.FSlateFontInfoKt",
		"makeSharedImpl",
		"()J"
	);

	RegisterNative<+[](UObject* FontObject, jint Size, jstring TypefaceFontNameJ, FJunSharedRef* OutlineSettingsPtr) -> FJunSharedRef* {
		FName TypefaceFontName;
		if (TypefaceFontNameJ != nullptr)
		{
			FString Name = J2U<FString>(TypefaceFontNameJ);
			TypefaceFontName = FName(*Name);
		}
		return U2J(MakeShared<FSlateFontInfo>(
			FontObject,
			Size,
			TypefaceFontName,
			*J2U<TSharedRef<FFontOutlineSettings>>(OutlineSettingsPtr)
		));
	}>(
		"com.cerebrallychallenged.jun.unreal.font.FSlateFontInfoKt",
		"makeSharedImpl",
		"(JILjava/lang/String;J)J"
	);
}