#include "JunManager.h"

void FJunManager::RegisterNatives_UGameplayStatics()
{
	RegisterNative<+[](UObject* WorldContextObject, jint PlayerIndex) -> ACharacter* {
		return UGameplayStatics::GetPlayerCharacter(WorldContextObject, PlayerIndex);
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameplayStaticsKt",
		"getPlayerCharacter",
		"(JI)J"
	);

	RegisterNative<+[](UObject* WorldContextObject, jint PlayerIndex) -> APlayerController* {
		return UGameplayStatics::GetPlayerController(WorldContextObject, PlayerIndex);
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameplayStaticsKt",
		"getPlayerController",
		"(JI)J"
	);

	RegisterNative<+[](UObject* WorldContextObject, USoundBase* Sound, jfloat VolumeMultiplier, jfloat PitchMultiplier, jfloat StartTime, USoundConcurrency* ConcurrencySettings, AActor* OwningActor, jboolean IsUISound) -> void {
		UGameplayStatics::PlaySound2D(
			WorldContextObject,
			Sound,
			VolumeMultiplier,
			PitchMultiplier,
			StartTime,
			ConcurrencySettings,
			OwningActor,
			J2U<bool>(IsUISound)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameplayStaticsKt",
		"playSound2D",
		"(JJFFFJJZ)V"
	);

	RegisterNative<+[](UObject* WorldContextObject, USoundBase* Sound, jobject Location, jfloat VolumeMultiplier, jfloat PitchMultiplier, jfloat StartTime, USoundAttenuation* AttenuationSettings, USoundConcurrency* ConcurrencySettings, UInitialActiveSoundParams* InitialParams) -> void {
		UGameplayStatics::PlaySoundAtLocation(
			WorldContextObject,
			Sound,
			J2U<FVector>(Location),
			VolumeMultiplier,
			PitchMultiplier,
			StartTime,
			AttenuationSettings,
			ConcurrencySettings,
			InitialParams
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameplayStaticsKt",
		"playSoundAtLocation",
		"(JJLcom/cerebrallychallenged/jun/math/geo/Vec3f;FFFJJJ)V"
	);

	RegisterNative<+[](UObject* WorldContextObject, UParticleSystem* EmitterTemplate, jobject Location, jobject Rotation, jobject Scale, jboolean AutoDestroy, EPSCPoolMethod PoolingMethod) -> UParticleSystemComponent* {
		return UGameplayStatics::SpawnEmitterAtLocation(
			WorldContextObject,
			EmitterTemplate,
			J2U<FVector>(Location),
			J2U<FQuat>(Rotation).Rotator(),
			J2U<FVector>(Scale),
			J2U<bool>(AutoDestroy),
			PoolingMethod
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.UGameplayStaticsKt",
		"spawnEmitterAtLocation",
		"(JJLcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Quaternion;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;ZB)J"
	);
}