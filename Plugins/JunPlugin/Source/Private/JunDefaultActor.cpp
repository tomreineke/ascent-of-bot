#include "JunDefaultActor.h"
#include "IJunPlugin.h"
#include "JunKeyMap.h"
#include "JunManager.h"
#include "JunWrapperManager.h"

AJunDefaultActor::AJunDefaultActor(const FObjectInitializer& FObjectInitializer) : AActor(FObjectInitializer)
{
}

void AJunDefaultActor::Init(FJunLifeGuard* NewLifeGuard, JNIEnv* NewEnv, TSharedPtr<FJunClassLoader> ClassLoader)
{
	this->LifeGuard = NewLifeGuard;
	this->Env = NewEnv;
}

void AJunDefaultActor::BeginPlay()
{
	Super::BeginPlay();
	LifeGuard->ExecuteIfOpen([](FJunManager* Manager)
	{
		Manager->OnBeginPlay();
	});
}

void AJunDefaultActor::EndPlay(const EEndPlayReason::Type Reason)
{
	LifeGuard->ExecuteIfOpen([Reason](FJunManager* Manager)
	{
		Manager->OnEndPlay(Reason);
	});
	Super::EndPlay(Reason);
}
