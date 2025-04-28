#include "JunManager.h"

void FJunManager::RegisterNatives_Int()
{
	RegisterNative<+[](jint* Ptr) -> jint {
		return *Ptr;
	}>(
		"com.cerebrallychallenged.jun.unreal.AnyRefKt",
		"getInt",
		"(J)I"
	);
}