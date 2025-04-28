#pragma once

#include "CoreMinimal.h"

#include "JunStatics.generated.h"

UCLASS()
class UJunStatics : public UBlueprintFunctionLibrary
{
	GENERATED_BODY()
public:
	UJunStatics(const FObjectInitializer&);

	UFUNCTION(BlueprintCallable, Category = Jun)
	static void OnKeyPressed(FKey Key);

	UFUNCTION(BlueprintCallable, Category = Jun)
	static void OnKeyReleased(FKey Key);
};