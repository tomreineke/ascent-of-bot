#include "JunManager.h"

void FJunManager::RegisterNatives_UObject()
{
	RegisterNative<+[](UObject* Object) -> void {
		Object->AddToRoot();
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"addToRoot",
		"(J)V"
	);

	RegisterNative<+[](UObject* Object) -> UClass* {
		return Object->GetClass();
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"getClass",
		"(J)J"
	);

	RegisterNative<+[](UObject* Object) -> jstring {
		return U2J(Object->GetName());
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"getName",
		"(J)Ljava/lang/String;"
	);

	RegisterNative<+[](UObject* Object, UClass* Class) -> jboolean {
		return U2J(Object->IsA(Class));
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"isA",
		"(JJ)Z"
	);

	RegisterNative<+[](UObject* Object) -> jboolean {
		return U2J(Object->IsRooted());
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"isRooted",
		"(J)Z"
	);

	RegisterNative<+[](UObject* Outer, UClass* Class, jstring NameJ) -> UObject* {
		if (Outer == nullptr)
		{
			Outer = GJunManager->DefaultActor;
		}
		if (NameJ != nullptr)
		{
			FString Name = J2U<FString>(NameJ);
			return NewObject<UObject>(Outer, Class, *Name);
		}
		else
		{
			return NewObject<UObject>(Outer, Class);
		}
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"newObject",
		"(JJLjava/lang/String;)J"
	);

	RegisterNative<+[](UObject* Object) -> void {
		Object->RemoveFromRoot();
	}>(
		"com.cerebrallychallenged.jun.unreal.UObjectKt",
		"removeFromRoot",
		"(J)V"
	);
}