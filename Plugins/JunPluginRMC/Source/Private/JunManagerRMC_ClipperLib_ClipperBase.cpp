#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_ClipperBase()
{
	RegisterNative<+[](ClipperLib::ClipperBase* Base, ClipperLib::Path* Path, ClipperLib::PolyType PolyType, jboolean Closed) -> jboolean {
		const char* exception = nullptr;
		return U2J(Base->AddPath(*Path, PolyType, J2U<bool>(Closed), exception));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperBaseKt",
		"addPath",
		"(JJIZ)Z"
	);

	RegisterNative<+[](ClipperLib::ClipperBase* Base, ClipperLib::Paths* Paths, ClipperLib::PolyType PolyType, jboolean Closed) -> jboolean {
		const char* exception = nullptr;
		return U2J(Base->AddPaths(*Paths, PolyType, J2U<bool>(Closed), exception));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperBaseKt",
		"addPaths",
		"(JJIZ)Z"
	);

	RegisterNative<+[](ClipperLib::ClipperBase* Base) -> void {
		Base->Clear();
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperBaseKt",
		"clear",
		"(J)V"
	);

	RegisterNative<+[](ClipperLib::ClipperBase* Base) -> jobject {
		ClipperLib::IntRect Bounds = Base->GetBounds();
		return U2J(FIntRect(Bounds.left, Bounds.top, Bounds.right, Bounds.bottom));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperBaseKt",
		"getBounds",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Bounds;"
	);

	RegisterNative<+[](ClipperLib::ClipperBase* Base) -> jboolean {
		return U2J(Base->PreserveCollinear());
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperBaseKt",
		"getPreserveCollinear",
		"(J)Z"
	);

	RegisterNative<+[](ClipperLib::ClipperBase* Base, jboolean Value) -> void {
		Base->PreserveCollinear(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperBaseKt",
		"setPreserveCollinear",
		"(JZ)V"
	);
}