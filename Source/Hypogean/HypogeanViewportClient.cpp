#include "HypogeanViewportClient.h"

void UHypogeanViewportClient::Init(struct FWorldContext& WorldContext, UGameInstance* OwningGameInstance, bool bCreateNewAudioDevice)
{
	Super::Init(WorldContext, OwningGameInstance, bCreateNewAudioDevice);
	MouseEnter(Viewport, 0, 0);
}