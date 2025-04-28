#include "JunManager.h"
#include "Engine/Canvas.h"

void FJunManager::RegisterNatives_UCanvas()
{
	RegisterNative<+[](UCanvas* Canvas, UFont* Font, jstring TextJ, float X, float Y, float XScale, float YScale) -> void {
		Canvas->DrawText(Font, J2U<FString>(TextJ), X, Y, XScale, YScale);
	}>(
		"com.cerebrallychallenged.jun.unreal.UCanvasKt",
		"drawText",
		"(JJLjava/lang/String;FFFF)V"
	);

	RegisterNative<+[](UCanvas* Canvas) -> jfloat {
		return Canvas->SizeX;
	}>(
		"com.cerebrallychallenged.jun.unreal.UCanvasKt",
		"getSizeX",
		"(J)F"
	);

	RegisterNative<+[](UCanvas* Canvas) -> jfloat {
		return Canvas->SizeY;
	}>(
		"com.cerebrallychallenged.jun.unreal.UCanvasKt",
		"getSizeY",
		"(J)F"
	);
}