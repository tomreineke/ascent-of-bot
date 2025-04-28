#include "JunManager.h"
#include "Engine/BlueprintCore.h"

void FJunManager::RegisterNatives_UBlueprint()
{
	RegisterNative<+[](UBlueprint* Blueprint) -> UClass* {
		return Blueprint->GeneratedClass;
	}>(
		"com.cerebrallychallenged.jun.unreal.blueprint.UBlueprintKt",
		"getGeneratedClass",
		"(J)J"
	);
}
