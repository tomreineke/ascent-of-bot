#pragma once

#include "CoreMinimal.h"
#include "Engine/GameViewportClient.h"
#include "HypogeanViewportClient.generated.h"

/**
* 
*/
UCLASS()
class HYPOGEAN_API UHypogeanViewportClient : public UGameViewportClient
{
	GENERATED_BODY()
public:
	void Init(struct FWorldContext& WorldContext, UGameInstance* OwningGameInstance, bool bCreateNewAudioDevice = true) override;
};
