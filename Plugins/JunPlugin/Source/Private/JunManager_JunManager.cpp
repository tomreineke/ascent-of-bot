#include "JunManager.h"
#include "Engine/Engine.h"

void FJunManager::RegisterNatives_JunManager()
{
	RegisterNative<+[](UObject* Object) -> FGCObjectScopeGuard* {
		return new FGCObjectScopeGuard(Object);
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"newScopeGuard",
		"(J)J"
	);
	
	RegisterNative<+[](FGCObjectScopeGuard* ScopeGuard) -> void {
		delete ScopeGuard;
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"deleteScopeGuard",
		"(J)V"
	);

	RegisterNative<+[](FJunSharedRef* SharedRef) -> void {
		delete SharedRef;
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"deleteSharedRef",
		"(J)V"
	);

	RegisterNativeWithEnv<+[](JNIEnv* Env, jobject WrapperIncubator) -> void {
		jclass WrapperIncubatorClass = Env->GetObjectClass(WrapperIncubator);
		jmethodID RegisterClassID = Env->GetMethodID(WrapperIncubatorClass, "registerClass", "(JLjava/lang/String;J)V");
		for (TObjectIterator<UClass> ClassIter; ClassIter; ++ClassIter)
		{
			UClass* Class = *ClassIter;

			if (!Class->IsNative()) continue;

			UClass* SuperClass = Class->GetSuperClass();
			jstring Name = U2J(Class->GetName());
			Env->CallVoidMethod(WrapperIncubator, RegisterClassID, Class, Name, SuperClass);
			GJunManager->JunCheckException();
			Env->DeleteLocalRef(Name);
		}
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"registerClasses",
		"(Lcom/cerebrallychallenged/jun/WrapperIncubator;)V"
	);

	RegisterNative<+[]() -> UWorld* {
		return GJunManager->World;
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"getWorld",
		"()J"
	);
	
	RegisterNative<+[]() -> AJunDefaultActor* {
		return GJunManager->DefaultActor;
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"getDefaultActor",
		"()J"
	);
	
	RegisterNative<+[]() -> UEngine* {
		return GEngine;
	}>(
		"com.cerebrallychallenged.jun.JunManagerKt",
		"getEngine",
		"()J"
	);

	RegisterNative<+[]() -> void {
		//UKismetSystemLibrary::QuitGame(GWorld, nullptr, EQuitPreference::Quit, true);
		FPlatformMisc::RequestExit(false);
	}>(
		"com.cerebrallychallenged.jun.JunManager",
		"quitGame",
		"()V"
	);
}