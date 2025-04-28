#include "JunManager.h"
#include "Engine/DecalActor.h"

void FJunManager::RegisterNatives_ADecalActor()
{
	RegisterNative<+[](ADecalActor* Actor) -> UDecalComponent* {
		return Actor->GetDecal();
	}>(
		"com.cerebrallychallenged.jun.unreal.decal.ADecalActorKt",
		"getDecal",
		"(J)J"
	);
}