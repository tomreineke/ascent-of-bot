#include "JunManager.h"
#include "JunAudioCallbacks.h"

void FJunManager::RegisterNatives_UAudioComponent()
{
	RegisterNative<+[](UAudioComponent* AudioComponent) -> void {
		UJunAudioCallbacks* Callbacks = NewObject<UJunAudioCallbacks>(GJunManager->DefaultActor);
		Callbacks->SetAudioComponent(AudioComponent);
		AudioComponent->OnAudioFinished.AddDynamic(Callbacks, &UJunAudioCallbacks::OnAudioFinished);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"activateCallbacks",
		"(J)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, FSoundAttenuationSettings* Settings) -> void {
		AudioComponent->AdjustAttenuation(*Settings);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"adjustAttenuation",
		"(JJ)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, float FadeOutDuration, float FadeVolumeLevel, EAudioFaderCurve FadeCurve) -> void {
		AudioComponent->FadeOut(FadeOutDuration, FadeVolumeLevel, FadeCurve);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"fadeOut",
		"(JFFB)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent) -> jboolean {
		return U2J(AudioComponent->bAllowSpatialization);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"getAllowSpatialization",
		"(J)Z"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent) -> jboolean {
		return AudioComponent->bIsPaused;
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"getPaused",
		"(J)Z"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent) -> jfloat {
		return AudioComponent->PitchMultiplier;
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"getPitchMultiplier",
		"(J)F"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent) -> USoundBase* {
		return AudioComponent->Sound;
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"getSound",
		"(J)J"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent) -> jfloat {
		return AudioComponent->VolumeMultiplier;
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"getVolumeMultiplier",
		"(J)F"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, jfloat StartTime) -> void {
		AudioComponent->Play(StartTime);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"play",
		"(JF)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, jboolean AllowSpatialization) -> void {
		AudioComponent->bAllowSpatialization = J2U<bool>(AllowSpatialization);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"setAllowSpatialization",
		"(JZ)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, jboolean Paused) -> void {
		AudioComponent->SetPaused(J2U<bool>(Paused));
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"setPaused",
		"(JZ)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, jfloat NewPitchMultiplier) -> void {
		AudioComponent->SetPitchMultiplier(NewPitchMultiplier);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"setPitchMultiplier",
		"(JF)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, USoundBase* NewSound) -> void {
		AudioComponent->SetSound(NewSound);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"setSound",
		"(JJ)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent, jfloat NewVolumeMultiplier) -> void {
		AudioComponent->SetVolumeMultiplier(NewVolumeMultiplier);
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"setVolumeMultiplier",
		"(JF)V"
	);

	RegisterNative<+[](UAudioComponent* AudioComponent) -> void {
		AudioComponent->Stop();
	}>(
		"com.cerebrallychallenged.jun.unreal.sound.UAudioComponentKt",
		"stop",
		"(J)V"
	);
}
