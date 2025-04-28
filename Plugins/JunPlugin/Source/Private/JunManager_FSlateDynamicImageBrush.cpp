#include "JunManager.h"

void FJunManager::RegisterNatives_FSlateDynamicImageBrush()
{
	RegisterNative<+[](UTexture2D* Texture, jobject Size, jstring NameJ) -> FJunSharedRef* {
		FName Name;
		FString NameString;
		if (NameJ != nullptr)
		{
			NameString = J2U<FString>(NameJ);
			Name = *NameString;
		}
		return U2J(MakeShared<FSlateDynamicImageBrush>(
			Texture,
			J2U<FVector2D>(Size),
			Name
		));
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateDynamicImageBrushKt",
		"makeShared",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;Ljava/lang/String;)J"
	);
}