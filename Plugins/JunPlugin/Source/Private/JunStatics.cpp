#include "JunStatics.h"
#include "JunKeyMap.h"
#include "JunManager.h"

UJunStatics::UJunStatics(const FObjectInitializer& ObjectInitializer) : UBlueprintFunctionLibrary(ObjectInitializer)
{

}

void UJunStatics::OnKeyPressed(FKey Key)
{
	if (GJunManager != nullptr)
	{
		GJunManager->OnKeyPressed(Key);
	}
}

void UJunStatics::OnKeyReleased(FKey Key)
{
	if (GJunManager != nullptr)
	{
		GJunManager->OnKeyReleased(Key);
	}
}