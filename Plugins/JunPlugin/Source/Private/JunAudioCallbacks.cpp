#include "JunAudioCallbacks.h"
#include "JunManager.h"

UJunAudioCallbacks::UJunAudioCallbacks() : AudioComponent(nullptr)
{
}

void UJunAudioCallbacks::SetAudioComponent(UAudioComponent* NewAudioComponent)
{
	AudioComponent = NewAudioComponent;
}

void UJunAudioCallbacks::OnAudioFinished()
{
	GJunLifeGuard->ExecuteIfOpen([this](FJunManager* Manager)
	{
		Manager->JunAudioCallbacksJNI->OnAudioFinished(AudioComponent);
	});
}



FJunAudioCallbacksJNI::FJunAudioCallbacksJNI(FJunManager& Manager) : Manager(Manager)
{
	AudioComponentKtClass = Manager.GetClassLoader()->LoadClassGlobalRef("com.cerebrallychallenged.jun.unreal.sound.UAudioComponent");
	Manager.JunCheckException(true);
	OnAudioFinishedID = GEnv->GetStaticMethodID(AudioComponentKtClass, "onAudioFinished", "(J)V");
	Manager.JunCheckException(true);
}

FJunAudioCallbacksJNI::~FJunAudioCallbacksJNI()
{
	GEnv->DeleteGlobalRef(AudioComponentKtClass);
}

void FJunAudioCallbacksJNI::OnAudioFinished(UAudioComponent* Component)
{
	GEnv->CallStaticVoidMethod(AudioComponentKtClass, OnAudioFinishedID, Component);
	Manager.JunCheckException();
}
