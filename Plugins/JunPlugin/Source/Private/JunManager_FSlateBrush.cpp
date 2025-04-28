#include "JunManager.h"

void FJunManager::RegisterNatives_FSlateBrush()
{
	RegisterNative<+[](FSlateBrush* Brush) -> jobject {
		return U2J(Brush->GetImageSize());
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateBrushKt",
		"getImageSize",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](FSlateBrush* Brush) -> jobject {
		return U2J(Brush->GetUVRegion());
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateBrushKt",
		"getUVRegion",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Bounds;"
	);

	RegisterNative<+[](FSlateBrush* Brush, jobject ImageSize) -> void {
		Brush->SetImageSize(J2U<FVector2D>(ImageSize));
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateBrushKt",
		"setImageSize",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;)V"
	);

	RegisterNative<+[](FSlateBrush* Brush, jobject UVRegion) -> void {
		Brush->SetUVRegion(J2U<FBox2D>(UVRegion));
	}>(
		"com.cerebrallychallenged.jun.unreal.slate.FSlateBrushKt",
		"setUVRegion",
		"(JLcom/cerebrallychallenged/jun/math/geo/Bounds;)V"
	);
}