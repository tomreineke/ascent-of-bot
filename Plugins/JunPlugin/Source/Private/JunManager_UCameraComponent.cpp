#include "JunManager.h"

void FJunManager::RegisterNatives_UCameraComponent()
{
	RegisterNative<+[](UCameraComponent* CameraComponent) -> jfloat {
		return CameraComponent->AspectRatio;
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"getAspectRatio",
		"(J)F"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent, jfloat DeltaTime) -> FJunSharedRef* {
		TSharedRef<FMinimalViewInfo> ViewInfo = MakeShared<FMinimalViewInfo>();
		CameraComponent->GetCameraView(DeltaTime, *ViewInfo);
		return U2J(ViewInfo);
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"getCameraView",
		"(JF)J"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent) -> jboolean {
		return U2J(CameraComponent->bConstrainAspectRatio);
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"getConstraintAspectRatio",
		"(J)Z"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent) -> jfloat {
		return CameraComponent->OrthoWidth;
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"getOrthoWidth",
		"(J)F"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent) -> ECameraProjectionMode::Type {
		return CameraComponent->ProjectionMode;
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"getProjectionMode",
		"(J)I"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent, jfloat AspectRatio) -> void {
		CameraComponent->SetAspectRatio(AspectRatio);
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"setAspectRatio",
		"(JF)V"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent, jboolean ConstraintAspectRatio) -> void {
		CameraComponent->SetConstraintAspectRatio(J2U<bool>(ConstraintAspectRatio));
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"setConstraintAspectRatio",
		"(JZ)V"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent, jfloat OrthoWidth) -> void {
		CameraComponent->SetOrthoWidth(OrthoWidth);
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"setOrthoWidth",
		"(JF)V"
	);

	RegisterNative<+[](UCameraComponent* CameraComponent, ECameraProjectionMode::Type ProjectionMode) -> void {
		CameraComponent->SetProjectionMode(ProjectionMode);
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.UCameraComponentKt",
		"setProjectionMode",
		"(JI)V"
	);
}