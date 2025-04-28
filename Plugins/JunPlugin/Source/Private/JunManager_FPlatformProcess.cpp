#include "JunManager.h"

void FJunManager::RegisterNatives_FPlatformProcess()
{
	RegisterNative<+[]() -> jstring {
		return U2J(FPlatformProcess::BaseDir());
	}>(
		"com.cerebrallychallenged.jun.unreal.FPlatformProcessKt",
		"getBaseDir",
		"()Ljava/lang/String;"
	);
}