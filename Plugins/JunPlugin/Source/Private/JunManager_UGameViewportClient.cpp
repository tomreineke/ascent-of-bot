#include "JunManager.h"

void FJunManager::RegisterNatives_UGameViewportClient()
{
	RegisterNative<+[](UGameViewportClient* GameViewport, FJunSharedRef* ViewportContent, jint ZOrder) -> void {
		GameViewport->AddViewportWidgetContent(J2U<TSharedRef<SWidget>>(ViewportContent), ZOrder);
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameViewportClientKt",
		"addViewportWidgetContent",
		"(JJI)V"
	);

	RegisterNative<+[](UGameViewportClient* GameViewport) -> FJunSharedRef* {
		return U2J(GameViewport->GetWindow());
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameViewportClientKt",
		"getWindow",
		"(J)J"
	);

	RegisterNative<+[](UGameViewportClient* GameViewport) -> void {
		GameViewport->RebuildCursors();
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameViewportClientKt",
		"rebuildCursors",
		"(J)V"
	);

	RegisterNative<+[](UGameViewportClient* GameViewport, FJunSharedRef* ViewportContent) -> void {
		GameViewport->RemoveViewportWidgetContent(J2U<TSharedRef<SWidget>>(ViewportContent));
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameViewportClientKt",
		"removeViewportWidgetContent",
		"(JJ)V"
	);

	RegisterNative<+[](UGameViewportClient* GameViewport, EMouseCursor::Type CursorShape, jstring CursorNameJ, jobject HotSpot) -> void {
		FString CursorName = J2U<FString>(CursorNameJ);
		GameViewport->SetHardwareCursor(CursorShape, *CursorName, J2U<FVector2D>(HotSpot));
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameViewportClientKt",
		"setHardwareCursor",
		"(JILjava/lang/String;Lcom/cerebrallychallenged/jun/math/geo/Vec2f;)V"
	);
}