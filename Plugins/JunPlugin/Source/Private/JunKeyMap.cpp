#include "JunKeyMap.h"
#include "JunManager.h"

FJunKeyMap& FJunKeyMap::Get()
{
	static FJunKeyMap JunKeyMap;
	return JunKeyMap;
}

FJunKeyMap::FJunKeyMap()
{
	EKeys::GetAllKeys(AllKeys);
	uint32 Num = AllKeys.Num();
	for (uint32 I = 0; I < Num; ++I)
	{
		Map.Add(AllKeys[I].GetFName().GetComparisonIndex(), I);
		if (AllKeys[I] == EKeys::Invalid)
		{
			IndexOfInvalidKey = I;
		}
	}
}

uint32 FJunKeyMap::IndexOf(const FKey& Key) const
{
	const uint32* Result = Map.Find(Key.GetFName().GetComparisonIndex());
	if (Result == nullptr)
	{
		UE_LOG(
			LogJun,
			Warning,
			TEXT("No matching index for key '%s' (comparison index: %d)"),
			*Key.GetDisplayName().ToString(),
			Key.GetFName().GetComparisonIndex()
		);
		return IndexOfInvalidKey;
	}
	else
	{
		return *Result;
	}
}

uint32 FJunKeyMap::KeyCount() const
{
	return AllKeys.Num();
}

const FKey& FJunKeyMap::operator[](uint32 Index) const
{
	return AllKeys[Index];
}
