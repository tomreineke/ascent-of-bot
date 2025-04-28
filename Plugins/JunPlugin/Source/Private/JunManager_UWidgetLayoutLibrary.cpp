#include "JunManager.h"
#include "Blueprint/WidgetLayoutLibrary.h"

void FJunManager::RegisterNatives_UWidgetLayoutLibrary()
{
	RegisterNative<+[](UObject* WorldContextObject) -> jobject {
		return U2J(UWidgetLayoutLibrary::GetMousePositionOnViewport(WorldContextObject));
	}>(
		"com.cerebrallychallenged.jun.unreal.UWidgetLayoutLibraryKt",
		"getMousePositionOnViewport",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);
}