#include "JunManager.h"

void FJunManager::RegisterNatives_FSoftObjectPtr()
{
	RegisterNative<+[](jstring PathJ) -> FJunSharedRef* {
		return U2J(MakeShared<TSoftObjectPtr<UObject>>(FSoftObjectPath(J2U<FString>(PathJ))));
	}>(
		"com.cerebrallychallenged.jun.unreal.FSoftObjectPtrKt",
		"makeSharedOfFSoftObjectPtr",
		"(Ljava/lang/String;)J"
	);

	RegisterNativeWithEnv<+[](JNIEnv* Env, TSoftObjectPtr<UObject>* SoftObjectPtr, jobject LocalRunnable) -> void {
		jobject Runnable = Env->NewGlobalRef(LocalRunnable);
		GJunManager->StreamableManager.RequestAsyncLoad(
			SoftObjectPtr->ToSoftObjectPath(),
			[LifeGuard = GJunManager->LifeGuard, Runnable]()
			{
				ExecuteInMainThread(LifeGuard, Runnable);
			}
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.FSoftObjectPtrKt",
		"requestAsyncLoad",
		"(JLjava/lang/Runnable;)V"
	);

	RegisterNative<+[](TSoftObjectPtr<UObject>* SoftObjectPtr) -> UObject* {
		return SoftObjectPtr->Get();
	}>(
		"com.cerebrallychallenged.jun.unreal.FSoftObjectPtrKt",
		"get",
		"(J)J"
	);
}