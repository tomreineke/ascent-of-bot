#include "JunManager.h"

void FJunManager::RegisterNatives_APlayerController()
{
	RegisterNative<+[](APlayerController* PlayerController, AActor* Target) -> void {
		PlayerController->ClientSetViewTarget(Target);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"clientSetViewTarget",
		"(JJ)V"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jobject {
		FVector WorldLocation;
		FVector WorldDirection;
		bool bSuccess = PlayerController->DeprojectMousePositionToWorld(WorldLocation, WorldDirection);
		return bSuccess ? U2J(U2J(WorldLocation), U2J(WorldDirection)) : nullptr;
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"deprojectMousePositionToWorld",
		"(J)Lkotlin/Pair;"
	);

	RegisterNative<+[](APlayerController* PlayerController, jfloat ScreenX, jfloat ScreenY) -> jobject {
		FVector WorldLocation;
		FVector WorldDirection;
		bool bSuccess = PlayerController->DeprojectScreenPositionToWorld(ScreenX, ScreenY, WorldLocation, WorldDirection);
		return bSuccess ? U2J(U2J(WorldLocation), U2J(WorldDirection)) : nullptr;
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"deprojectScreenPositionToWorld",
		"(JFF)Lkotlin/Pair;"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> EMouseCursor::Type {
		return PlayerController->CurrentMouseCursor;
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getCurrentMouseCursor",
		"(J)I"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jboolean {
		return U2J(PlayerController->bEnableClickEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getEnableClickEvents",
		"(J)Z"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jboolean {
		return U2J(PlayerController->bEnableMouseOverEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getEnableMouseOverEvents",
		"(J)Z"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jboolean {
		return U2J(PlayerController->bEnableTouchEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getEnableTouchEvents",
		"(J)Z"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jboolean {
		return U2J(PlayerController->bEnableTouchOverEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getEnableTouchOverEvents",
		"(J)Z"
	);

	RegisterNative<+[](APlayerController* PlayerController, jobject ScreenPosition, ECollisionChannel TraceChannel, jboolean TraceComplex) -> FJunSharedRef* {
		TSharedRef<FHitResult> HitResult = MakeShared<FHitResult>();
		PlayerController->GetHitResultAtScreenPosition(J2U<FVector2D>(ScreenPosition), TraceChannel, J2U<bool>(TraceComplex), *HitResult);
		return U2J(std::move(HitResult));
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getHitResultAtScreenPosition",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;IZ)J"
		);

	RegisterNative<+[](APlayerController* PlayerController, ECollisionChannel TraceChannel, jboolean TraceComplex) -> FJunSharedRef* {
		TSharedRef<FHitResult> HitResult = MakeShared<FHitResult>();
		PlayerController->GetHitResultUnderCursor(TraceChannel, J2U<bool>(TraceComplex), *HitResult);
		return U2J(std::move(HitResult));
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getHitResultUnderCursor",
		"(JIZ)J"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> AHUD* {
		return PlayerController->GetHUD();
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getHUD",
		"(J)J"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> ULocalPlayer* {
		return PlayerController->GetLocalPlayer();
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getLocalPlayer",
		"(J)J"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jboolean {
		return U2J(PlayerController->bShowMouseCursor);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getShowMouseCursor",
		"(J)Z"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> jobject {
		int32 X;
		int32 Y;
		PlayerController->GetViewportSize(X, Y);
		return U2J(X, Y);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"getViewportSize",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2i;"
	);

	RegisterNative<+[](APlayerController* PlayerController, jobject WorldLocation, jboolean bPlayerViewportRelative) -> jobject {
		FVector2D Result;
		bool bSuccess = PlayerController->ProjectWorldLocationToScreen(J2U<FVector>(WorldLocation), Result, J2U<bool>(bPlayerViewportRelative));
		if (!bSuccess) {
			Result.X = std::numeric_limits<float>::quiet_NaN();
		}
		return U2J(Result);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"projectWorldLocationToScreen",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](APlayerController* PlayerController, EMouseCursor::Type MouseCursor) -> void {
		PlayerController->CurrentMouseCursor = MouseCursor;
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setCurrentMouseCursor",
		"(JI)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, jboolean bEnableClickEvents) -> void {
		PlayerController->bEnableClickEvents = J2U<bool>(bEnableClickEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setEnableClickEvents",
		"(JZ)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, jboolean bEnableMouseOverEvents) -> void {
		PlayerController->bEnableMouseOverEvents = J2U<bool>(bEnableMouseOverEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setEnableMouseOverEvents",
		"(JZ)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, jboolean bEnableTouchEvents) -> void {
		PlayerController->bEnableTouchEvents = J2U<bool>(bEnableTouchEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setEnableTouchEvents",
		"(JZ)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, jboolean bEnableTouchOverEvents) -> void {
		PlayerController->bEnableTouchOverEvents = J2U<bool>(bEnableTouchOverEvents);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setEnableTouchOverEvents",
		"(JZ)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, FJunSharedRef* WidgetToFocus, EMouseLockMode MouseLockMode, jboolean bHideCursorDuringCapture) -> void {
		FInputModeGameAndUI InputMode;
		InputMode.SetLockMouseToViewportBehavior(MouseLockMode);
		InputMode.SetHideCursorDuringCapture(J2U<bool>(bHideCursorDuringCapture));
		InputMode.SetWidgetToFocus(J2U<TSharedPtr<SWidget>>(WidgetToFocus));
		PlayerController->SetInputMode(InputMode);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setInputModeGameAndUi",
		"(JJBZ)V"
	);

	RegisterNative<+[](APlayerController* PlayerController) -> void {
		FInputModeGameOnly InputMode;
		PlayerController->SetInputMode(InputMode);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setInputModeGameOnly",
		"(J)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, FJunSharedRef* WidgetToFocus, EMouseLockMode MouseLockMode) -> void {
		FInputModeUIOnly InputMode;
		InputMode.SetLockMouseToViewportBehavior(MouseLockMode);
		InputMode.SetWidgetToFocus(J2U<TSharedPtr<SWidget>>(WidgetToFocus));
		PlayerController->SetInputMode(InputMode);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setInputModeUiOnly",
		"(JJB)V"
	);

	RegisterNative<+[](APlayerController* PlayerController, jboolean bShowMouseCursor) -> void {
		PlayerController->bShowMouseCursor = J2U<bool>(bShowMouseCursor);
	}>(
		"com.cerebrallychallenged.jun.unreal.APlayerControllerKt",
		"setShowMouseCursor",
		"(JZ)V"
	);
}