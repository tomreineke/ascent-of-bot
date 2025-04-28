#pragma once

#include "EngineMinimal.h"

#include "JunClassLoader.h"
#include "JunLifeGuard.h"

#include "JunDefaultActor.generated.h"

UCLASS()
class JUNPLUGIN_API AJunDefaultActor : public AActor
{
	GENERATED_BODY()
public:
	AJunDefaultActor(const FObjectInitializer&);

	void Init(FJunLifeGuard* NewLifeGuard, JNIEnv* NewEnv, TSharedPtr<FJunClassLoader> ClassLoader);

	void BeginPlay() override;

	void EndPlay(const EEndPlayReason::Type Reason) override;
private:
	FJunLifeGuard* LifeGuard;
	JNIEnv* Env;
};