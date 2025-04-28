#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_Clipper()
{
	RegisterNative<+[](ClipperLib::Clipper* Clipper, ClipperLib::ClipType ClipType, ClipperLib::Paths* Solution, ClipperLib::PolyFillType SubjFillType, ClipperLib::PolyFillType ClipFillType) -> jboolean {
		const char* exception = nullptr;
		return U2J(Clipper->Execute(ClipType, *Solution, SubjFillType, ClipFillType, exception));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"executeForPaths",
		"(JIJII)Z"
	);

	RegisterNative<+[](ClipperLib::Clipper* Clipper, ClipperLib::ClipType ClipType, ClipperLib::PolyTree* Solution, ClipperLib::PolyFillType SubjFillType, ClipperLib::PolyFillType ClipFillType) -> jboolean {
		return U2J(Clipper->Execute(ClipType, *Solution, SubjFillType, ClipFillType));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"executeForPolyTree",
		"(JIJII)Z"
	);

	RegisterNative<+[](ClipperLib::Clipper* Clipper) -> jboolean {
		return U2J(Clipper->ReverseSolution());
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"getReverseSolution",
		"(J)Z"
	);

	RegisterNative<+[](ClipperLib::Clipper* Clipper) -> jboolean {
		return U2J(Clipper->StrictlySimple());
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"getStrictlySimple",
		"(J)Z"
	);

	RegisterNative<+[](ClipperLib::InitOptions InitOptions) -> FJunSharedRef* {
		return U2J(MakeShared<ClipperLib::Clipper>(InitOptions));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"makeSharedOfClipper",
		"(I)J"
	);

	RegisterNative<+[](ClipperLib::Clipper* Clipper, jboolean Value) -> void {
		Clipper->ReverseSolution(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"setReverseSolution",
		"(JZ)V"
	);

	RegisterNative<+[](ClipperLib::Clipper* Clipper, jboolean Value) -> void {
		Clipper->StrictlySimple(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperKt",
		"setStrictlySimple",
		"(JZ)V"
	);
}