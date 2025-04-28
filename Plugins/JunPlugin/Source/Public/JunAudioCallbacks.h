#pragma once

#include <EngineMinimal.h>
#include "IJunPlugin.h"

#include "JunAudioCallbacks.generated.h"

UCLASS()
class JUNPLUGIN_API UJunAudioCallbacks : public UObject
{
	GENERATED_BODY()
public:
	UJunAudioCallbacks();

	void SetAudioComponent(UAudioComponent* NewAudioComponent);

	UFUNCTION()
	void OnAudioFinished();
private:
	UPROPERTY()
	UAudioComponent* AudioComponent;
};

class FJunAudioCallbacksJNI
{
public:
	FJunAudioCallbacksJNI(FJunManager& Manager);
	~FJunAudioCallbacksJNI();

	void OnAudioFinished(UAudioComponent* Component);
private:
	FJunManager& Manager;
	jclass AudioComponentKtClass;
	jmethodID OnAudioFinishedID;
};
