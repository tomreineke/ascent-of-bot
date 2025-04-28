#include "JunManager.h"

void FJunManager::RegisterNatives_FPaths()
{
	RegisterNative<+[]() -> jstring {
		return U2J(FPaths::ProjectConfigDir());
	}>(
		"com.cerebrallychallenged.jun.unreal.FPathsKt",
		"getProjectConfigDir",
		"()Ljava/lang/String;"
	);

	RegisterNative<+[]() -> jstring {
		return U2J(FPaths::ProjectContentDir());
	}>(
		"com.cerebrallychallenged.jun.unreal.FPathsKt",
		"getProjectContentDir",
		"()Ljava/lang/String;"
	);

	RegisterNative<+[]() -> jstring {
		return U2J(FPaths::ProjectSavedDir());
	}>(
		"com.cerebrallychallenged.jun.unreal.FPathsKt",
		"getProjectSavedDir",
		"()Ljava/lang/String;"
	);

	RegisterNative<+[]() -> jstring {
		return U2J(FPaths::ProjectUserDir());
	}>(
		"com.cerebrallychallenged.jun.unreal.FPathsKt",
		"getProjectUserDir",
		"()Ljava/lang/String;"
	);
}