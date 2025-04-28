#include "JunManager.h"

void FJunManager::RegisterNatives_log()
{
	RegisterNative<+[](jint Level, jstring MessageJ) -> void {
		FString Message = J2U<FString>(MessageJ);
		switch (Level)
		{
		case 0:
			UE_LOG(LogJun, Fatal, TEXT("%s"), *Message);
			break;
		case 1:
			UE_LOG(LogJun, Error, TEXT("%s"), *Message);
			break;
		case 2:
			UE_LOG(LogJun, Warning, TEXT("%s"), *Message);
			break;
		case 3:
			UE_LOG(LogJun, Display, TEXT("%s"), *Message);
			break;
		case 4:
			UE_LOG(LogJun, Log, TEXT("%s"), *Message);
			break;
		case 5:
			UE_LOG(LogJun, Verbose, TEXT("%s"), *Message);
			break;
		case 6:
			UE_LOG(LogJun, VeryVerbose, TEXT("%s"), *Message);
			break;
		default:
			break;
		}
	}>(
		"com.cerebrallychallenged.jun.log.LogKt",
		"log",
		"(ILjava/lang/String;)V"
	);
}