#include "JunManager.h"

void FJunManager::RegisterNatives_FCommandLine()
{
	RegisterNative<+[]() -> jstring {
		return U2J(FCommandLine::Get());
	}>(
		"com.cerebrallychallenged.jun.unreal.FCommandLineKt",
		"getImpl",
		"()Ljava/lang/String;"
	);
}