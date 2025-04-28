#include "JunManager.h"

void FJunManager::RegisterNatives_FSlateImageBrush()
{
	RegisterNative<+[](jstring NameJ, jobject ImageSize, jobject Color, ESlateBrushTileType::Type Tiling, ESlateBrushImageType::Type ImageType) -> FJunSharedRef* {
		return U2J(MakeShared<FSlateImageBrush>(
			J2U<FString>(NameJ),
			J2U<FVector2D>(ImageSize),
			J2U<FLinearColor>(Color),
			Tiling,
			ImageType
		));
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateImageBrushKt",
		"makeShared",
		"(Ljava/lang/String;Lcom/cerebrallychallenged/jun/math/geo/Vec2f;Lcom/cerebrallychallenged/jun/math/geo/Vec4f;II)J"
	);
}