#include "JunManager.h"

void FJunManager::RegisterNatives_UClass()
{
	RegisterNative<+[](jstring ClassName) -> UClass* {
		FSoftClassPath ClassPath(J2U<FString>(ClassName));
		return ClassPath.TryLoadClass<UObject>();
	}>(
		"com.cerebrallychallenged.jun.unreal.UClassKt",
		"loadClassImpl",
		"(Ljava/lang/String;)J"
	);

	RegisterNative<+[](UClass* Class) -> UClass* {
		return Class->GetSuperClass();
	}>(
		"com.cerebrallychallenged.jun.unreal.UClassKt",
		"getSuperClass",
		"(J)J"
	);
}