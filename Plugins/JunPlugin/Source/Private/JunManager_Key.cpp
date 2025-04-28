#include "JunManager.h"

void FJunManager::RegisterNatives_Key()
{
	RegisterNative<+[](jint KeyIndex) -> jstring {
		return U2J(FJunKeyMap::Get()[KeyIndex].GetFName().ToString());
	}>(
		"com.cerebrallychallenged.jun.input.KeyKt",
		"getName",
		"(I)Ljava/lang/String;"
	);

	RegisterNative<+[](jint KeyIndex) -> jstring {
		return U2J(FJunKeyMap::Get()[KeyIndex].GetDisplayName().ToString());
	}>(
		"com.cerebrallychallenged.jun.input.KeyKt",
		"getDisplayName",
		"(I)Ljava/lang/String;"
	);

	RegisterNative<+[](jint KeyIndex) -> jlong {
		const uint32* KeyCodePtr;
		const uint32* CharCodePtr;
		FInputKeyManager::Get().GetCodesFromKey(FJunKeyMap::Get()[KeyIndex], KeyCodePtr, CharCodePtr);
		uint64 KeyCode = KeyCodePtr != nullptr ? *KeyCodePtr : 0;
		uint64 CharCode = CharCodePtr != nullptr ? *CharCodePtr : 0;
		return (KeyCode << 32) | CharCode;
	}>(
		"com.cerebrallychallenged.jun.input.KeyKt",
		"getCodes",
		"(I)J"
	);

	RegisterNative<+[]() -> jint {
		return FJunKeyMap::Get().KeyCount();
	}>(
		"com.cerebrallychallenged.jun.input.KeyKt",
		"keyCount",
		"()I"
	);

	RegisterNative<+[](jint SpecialIndex) -> jint {
		switch (SpecialIndex)
		{
		case 0:
			return FJunKeyMap::Get().IndexOf(EKeys::AnyKey);
		case 1:
			return FJunKeyMap::Get().IndexOf(EKeys::LeftMouseButton);
		case 2:
			return FJunKeyMap::Get().IndexOf(EKeys::MiddleMouseButton);
		case 3:
			return FJunKeyMap::Get().IndexOf(EKeys::RightMouseButton);
		case 4:
			return FJunKeyMap::Get().IndexOf(EKeys::ThumbMouseButton);
		case 5:
			return FJunKeyMap::Get().IndexOf(EKeys::ThumbMouseButton2);
		case 6:
			return FJunKeyMap::Get().IndexOf(EKeys::MouseScrollUp);
		case 7:
			return FJunKeyMap::Get().IndexOf(EKeys::MouseScrollDown);
		default:
			return -1;
		}
	}>(
		"com.cerebrallychallenged.jun.input.KeyKt",
		"specialKeyIndex",
		"(I)I"
	);
}