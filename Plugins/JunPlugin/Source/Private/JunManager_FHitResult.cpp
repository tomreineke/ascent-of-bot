#include "JunManager.h"

void FJunManager::RegisterNatives_FHitResult()
{
	RegisterNative<+[](FHitResult* HitResult) -> AActor* {
		return HitResult->GetActor();
	}>(
		"com.cerebrallychallenged.jun.unreal.FHitResultKt",
		"getActor",
		"(J)J"
	);
	
	RegisterNative<+[](FHitResult* HitResult) -> UPrimitiveComponent* {
		return HitResult->GetComponent();
	}>(
		"com.cerebrallychallenged.jun.unreal.FHitResultKt",
		"getComponent",
		"(J)J"
	);

	RegisterNative<+[](FHitResult* HitResult) -> jobject {
		return U2J(HitResult->ImpactPoint);
	}>(
		"com.cerebrallychallenged.jun.unreal.FHitResultKt",
		"getImpactPoint",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);
}
