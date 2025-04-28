#include "JunManager.h"

void FJunManager::RegisterNatives_FChildren()
{
	RegisterNative<+[](FChildren* Children) -> jint {
		return Children->Num();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.FChildrenKt",
		"getNum",
		"(J)I"
	);
}