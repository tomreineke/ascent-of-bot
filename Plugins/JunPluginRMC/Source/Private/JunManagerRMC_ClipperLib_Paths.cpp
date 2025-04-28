#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_Paths()
{
	RegisterNative<+[](ClipperLib::Paths* Paths, size_t Index) -> ClipperLib::Path* {
		return &(*Paths)[Index];
	}>(
		"com.cerebrallychallenged.jun.clipper.PathsKt",
		"get",
		"(JJ)J"
	);

	RegisterNative<+[](ClipperLib::Paths* Paths) -> size_t {
		return Paths->size();
	}>(
		"com.cerebrallychallenged.jun.clipper.PathsKt",
		"getSize",
		"(J)J"
	);

	RegisterNative<+[](jint Count) -> FJunSharedRef* {
		return U2J(MakeShared<ClipperLib::Paths>(Count));
	}>(
		"com.cerebrallychallenged.jun.clipper.PathsKt",
		"makeSharedOfPaths",
		"(I)J"
	);

	RegisterNative<+[](ClipperLib::Paths* Paths, ClipperLib::Path* Path) -> void {
		Paths->push_back(*Path);
	}>(
		"com.cerebrallychallenged.jun.clipper.PathsKt",
		"pushBack",
		"(JJ)V"
	);
}