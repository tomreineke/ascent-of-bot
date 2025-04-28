#include "JunManager.h"

void FJunManager::RegisterNatives_TSharedRef()
{
	RegisterNative<+[](FJunSharedRef* Ptr) -> void* {
		return Ptr->GetDirect();
	}>(
		"com.cerebrallychallenged.jun.unreal.AnyRefKt",
		"getDirect",
		"(J)J"
	);
}