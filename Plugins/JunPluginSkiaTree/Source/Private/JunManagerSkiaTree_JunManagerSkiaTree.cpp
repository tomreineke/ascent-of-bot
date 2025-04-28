#include "JunManagerSkiaTree.h"

void FJunManagerSkiaTree::RegisterNatives_FJunManagerSkiaTree()
{
	RegisterNative<+[]() -> jstring {
		return U2J(*GJunManagerSkiaTree->SkiaTreePath);
	}>(
		"com.cerebrallychallenged.jun.skiatree.SkiaTreeApiKt",
		"getSkiaTreePath",
		"()Ljava/lang/String;"
	);
}
