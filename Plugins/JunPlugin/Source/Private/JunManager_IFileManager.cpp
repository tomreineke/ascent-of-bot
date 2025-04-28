#include "JunManager.h"

void FJunManager::RegisterNatives_IFileManager()
{
	RegisterNative<+[](jstring RelativePath) -> jstring {
		return U2J(IFileManager::Get().ConvertToAbsolutePathForExternalAppForRead(*J2U<FString>(RelativePath)));
	}>(
		"com.cerebrallychallenged.jun.unreal.IFileManager",
		"convertToAbsolutePathForExternalAppForRead",
		"(Ljava/lang/String;)Ljava/lang/String;"
	);
}