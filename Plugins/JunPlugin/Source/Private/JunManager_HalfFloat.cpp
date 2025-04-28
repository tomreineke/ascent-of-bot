#include "JunManager.h"

void FJunManager::RegisterNatives_HalfFloat()
{
	RegisterNative<+[](jshort Bits) -> jfloat {
		return *reinterpret_cast<FFloat16*>(&Bits);
	}>(
		"com.cerebrallychallenged.jun.util.HalfFloatKt",
		"nativeHalfFloatToFloat-xj2QHRw",
		"(S)F"
	);

	RegisterNative<+[](jfloat Value) -> jshort {
		FFloat16 Float = Value;
		return Float.Encoded;
	}>(
		"com.cerebrallychallenged.jun.util.HalfFloatKt",
		"nativeFloatToHalfFloat",
		"(F)S"
	);
}