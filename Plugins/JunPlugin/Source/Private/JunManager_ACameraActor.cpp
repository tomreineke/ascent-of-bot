#include "JunManager.h"
#include "Camera/CameraActor.h"

void FJunManager::RegisterNatives_ACameraActor()
{
	RegisterNative<+[](ACameraActor* Actor) -> UCameraComponent* {
		return Actor->GetCameraComponent();
	}>(
		"com.cerebrallychallenged.jun.unreal.camera.ACameraActorKt",
		"getCameraComponent",
		"(J)J"
	);
}