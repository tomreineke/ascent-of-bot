#include "JunManager.h"
#include "Components/StaticMeshComponent.h"
#include "Engine/StaticMeshActor.h"

void FJunManager::RegisterNatives_AStaticMeshActor()
{
	RegisterNative<+[](AStaticMeshActor* Actor) -> UStaticMeshComponent* {
		return Actor->GetStaticMeshComponent();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.AStaticMeshActorKt",
		"getStaticMeshComponent",
		"(J)J"
	);
}