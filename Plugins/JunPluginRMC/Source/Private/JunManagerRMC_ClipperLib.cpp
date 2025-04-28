#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib()
{
	RegisterNative<+[](ClipperLib::Path* Path) -> jdouble {
		return ClipperLib::Area(*Path);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"area",
		"(J)D"
	);

	RegisterNative<+[](ClipperLib::Path* InPoly, ClipperLib::Path* OutPoly, jdouble Distance) -> void {
		ClipperLib::CleanPolygon(*InPoly, *OutPoly, Distance);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"cleanPolygon",
		"(JJD)V"
	);

	RegisterNative<+[](ClipperLib::Paths* InPolys, ClipperLib::Paths* OutPolys, jdouble Distance) -> void {
		ClipperLib::CleanPolygons(*InPolys, *OutPolys, Distance);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"cleanPolygons",
		"(JJD)V"
	);

	RegisterNative<+[](ClipperLib::PolyTree* PolyTree, ClipperLib::Paths* Paths) -> void {
		ClipperLib::ClosedPathsFromPolyTree(*PolyTree, *Paths);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"closedPathsFromPolyTree",
		"(JJ)V"
	);

	RegisterNative<+[](ClipperLib::Path* Poly1, ClipperLib::Path* Poly2, ClipperLib::Paths* Solution) -> void {
		const char* exception = nullptr;
		ClipperLib::MinkowskiDiff(*Poly1, *Poly2, *Solution, exception);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"minkowskiDiff",
		"(JJJ)V"
	);

	RegisterNative<+[](ClipperLib::Path* Poly1, ClipperLib::Path* Poly2, ClipperLib::Paths* Solution, jboolean PathIsClosed) -> void {
		const char* exception = nullptr;
		ClipperLib::MinkowskiSum(*Poly1, *Poly2, *Solution, J2U<bool>(PathIsClosed), exception);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"minkowskiSum",
		"(JJJZ)V"
	);

	RegisterNative<+[](ClipperLib::PolyTree* PolyTree, ClipperLib::Paths* Paths) -> void {
		ClipperLib::OpenPathsFromPolyTree(*PolyTree, *Paths);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"openPathsFromPolyTree",
		"(JJ)V"
	);

	RegisterNative<+[](ClipperLib::Path* Path) -> jboolean {
		return U2J(ClipperLib::Orientation(*Path));
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"orientation",
		"(J)Z"
	);

	RegisterNative<+[](jobject Point, ClipperLib::Path* Poly) -> jint {
		FIntPoint Pt = J2U<FIntPoint>(Point);
		return ClipperLib::PointInPolygon(ClipperLib::IntPoint(Pt.X, Pt.Y), *Poly);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"pointInPolygon",
		"(Lcom/cerebrallychallenged/jun/math/geo/Vec2i;J)I"
	);

	RegisterNative<+[](ClipperLib::PolyTree* PolyTree, ClipperLib::Paths* Paths) -> void {
		ClipperLib::PolyTreeToPaths(*PolyTree, *Paths);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"polyTreeToPaths",
		"(JJ)V"
	);

	RegisterNative<+[](ClipperLib::Path* Path) -> void {
		ClipperLib::ReversePath(*Path);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"reversePath",
		"(J)V"
	);

	RegisterNative<+[](ClipperLib::Paths* Paths) -> void {
		ClipperLib::ReversePaths(*Paths);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"reversePaths",
		"(J)V"
	);

	RegisterNative<+[](ClipperLib::Path* InPoly, ClipperLib::Paths* OutPolys, ClipperLib::PolyFillType PolyFillType) -> void {
		const char* exception = nullptr;
		ClipperLib::SimplifyPolygon(*InPoly, *OutPolys, exception, PolyFillType);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"simplifyPolygon",
		"(JJI)V"
	);

	RegisterNative<+[](ClipperLib::Paths* InPolys, ClipperLib::Paths* OutPolys, ClipperLib::PolyFillType PolyFillType) -> void {
		const char* exception = nullptr;
		ClipperLib::SimplifyPolygons(*InPolys, *OutPolys, exception, PolyFillType);
	}>(
		"com.cerebrallychallenged.jun.clipper.ClipperLibKt",
		"simplifyPolygons",
		"(JJI)V"
	);
}