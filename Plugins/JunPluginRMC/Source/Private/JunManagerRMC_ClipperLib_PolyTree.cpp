#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_PolyTree()
{
	RegisterNative<+[](ClipperLib::PolyTree* Tree) -> void {
		Tree->Clear();
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyTreeKt",
		"clear",
		"(J)V"
	);

	RegisterNative<+[](ClipperLib::PolyTree* Tree) -> ClipperLib::PolyNode* {
		return Tree->GetFirst();
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyTreeKt",
		"getFirst",
		"(J)J"
	);

	RegisterNative<+[](ClipperLib::PolyTree* Tree) -> jint {
		return Tree->Total();
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyTreeKt",
		"getTotal",
		"(J)I"
	);

	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(MakeShared<ClipperLib::PolyTree>());
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyTreeKt",
		"makeSharedOfPolyTree",
		"()J"
	);
}