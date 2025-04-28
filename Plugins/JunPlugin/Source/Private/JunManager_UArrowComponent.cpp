#include "JunManager.h"
#include "Components/ArrowComponent.h"

void FJunManager::RegisterNatives_UArrowComponent()
{
	RegisterNative<+[](UArrowComponent* Component) -> FColor {
		return Component->ArrowColor;
	}>(
		"com.cerebrallychallenged.jun.unreal.UArrowComponentKt",
		"getArrowColor",
		"(J)I"
	);

	RegisterNative<+[](UArrowComponent* Component) -> jfloat {
		return Component->ArrowSize;
	}>(
		"com.cerebrallychallenged.jun.unreal.UArrowComponentKt",
		"getArrowSize",
		"(J)F"
	);

	RegisterNative<+[](UArrowComponent* Component, FColor Color) -> void {
		Component->SetArrowColor(Color);
	}>(
		"com.cerebrallychallenged.jun.unreal.UArrowComponentKt",
		"setArrowColor",
		"(JI)V"
	);

	RegisterNative<+[](UArrowComponent* Component, jfloat Size) -> void {
		Component->ArrowSize = Size;
	}>(
		"com.cerebrallychallenged.jun.unreal.UArrowComponentKt",
		"setArrowSize",
		"(JF)V"
	);
}