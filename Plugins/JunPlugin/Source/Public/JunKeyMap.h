#pragma once

#include "CoreMinimal.h"
#include "InputCoreTypes.h"

class JUNPLUGIN_API FJunKeyMap
{
public:
	static FJunKeyMap& Get();

	FJunKeyMap();

	uint32 IndexOf(const FKey& Key) const;

	uint32 KeyCount() const;

	const FKey& operator[](uint32 Index) const;
private:
	TArray<FKey> AllKeys;

	TMap<NAME_INDEX, uint32> Map;

	uint32 IndexOfInvalidKey;
};
