#include "JunPlayerController.h"

AJunPlayerController::AJunPlayerController(const FObjectInitializer& ObjectInitializer) : APlayerController(ObjectInitializer)
{
	ClickEventKeys.Add(EKeys::RightMouseButton);
}