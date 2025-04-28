#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_ClipperOffset()
{
	RegisterNative<+[](ClipperLib::ClipperOffset* Offset, ClipperLib::Path* Path, ClipperLib::JoinType JoinType, ClipperLib::EndType EndType) -> void {
		Offset->AddPath(*Path, JoinType, EndType);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"addPath",
		"(JJII)V"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset, ClipperLib::Paths* Paths, ClipperLib::JoinType JoinType, ClipperLib::EndType EndType) -> void {
		Offset->AddPaths(*Paths, JoinType, EndType);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"addPaths",
		"(JJII)V"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset) -> void {
		Offset->Clear();
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"clear",
		"(J)V"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset, ClipperLib::Paths* Solution, jdouble Delta) -> void {
		const char* exception = nullptr;
		Offset->Execute(*Solution, Delta, exception);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"executeForPaths",
		"(JJD)V"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset, ClipperLib::PolyTree* Solution, jdouble Delta) -> void {
		const char* exception = nullptr;
		Offset->Execute(*Solution, Delta, exception);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"executeForPolyTree",
		"(JJD)V"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset) -> jdouble {
		return Offset->ArcTolerance;
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"getArcTolerance",
		"(J)D"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset) -> jdouble {
		return Offset->MiterLimit;
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"getMiterLimit",
		"(J)D"
	);

	RegisterNative<+[](jdouble MiterLimit, jdouble RoundPrecision) -> FJunSharedRef* {
		return U2J(MakeShared<ClipperLib::ClipperOffset>(MiterLimit, RoundPrecision));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"makeSharedOfClipperOffset",
		"(DD)J"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset, jdouble Value) -> void {
		Offset->ArcTolerance = Value;
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"setArcTolerance",
		"(JD)V"
	);

	RegisterNative<+[](ClipperLib::ClipperOffset* Offset, jdouble Value) -> void {
		Offset->MiterLimit = Value;
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperOffsetKt",
		"setMiterLimit",
		"(JD)V"
	);
}