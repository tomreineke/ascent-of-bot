#pragma once

#include "Runtime/Engine/Classes/GameFramework/PlayerController.h"

#include "JunPlayerController.generated.h"

UCLASS()
class JUNPLUGIN_API AJunPlayerController : public APlayerController
{
	GENERATED_BODY()
public:
	AJunPlayerController(const FObjectInitializer&);
};