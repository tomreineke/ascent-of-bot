#include "JunManager.h"
#include "Components/SplineComponent.h"

void FJunManager::RegisterNatives_USplineComponent()
{
	RegisterNative<+[](USplineComponent* Component, const FSplinePoint* Data, size_t ElementCount, jboolean UpdateSpline) -> void {
		Component->AddPoints(TArray<FSplinePoint>(Data, ElementCount), J2U<bool>(UpdateSpline));
	}>(
		"com.cerebrallychallenged.jun.unreal.spline.USplineComponentKt",
		"addPoints",
		"(JJJZ)V"
	);

	RegisterNative<+[](USplineComponent* Component, jboolean UpdateSpline) -> void {
		Component->ClearSplinePoints(J2U<bool>(UpdateSpline));
	}>(
		"com.cerebrallychallenged.jun.unreal.spline.USplineComponentKt",
		"clearSplinePoints",
		"(JZ)V"
	);

	RegisterNative<+[](USplineComponent* Component) -> jboolean {
		return U2J(Component->bDrawDebug);
	}>(
		"com.cerebrallychallenged.jun.unreal.spline.USplineComponentKt",
		"getDrawDebug",
		"(J)Z"
	);

	RegisterNative<+[](USplineComponent* Component, jboolean DrawDebug) -> void {
		Component->SetDrawDebug(J2U<bool>(DrawDebug));
	}>(
		"com.cerebrallychallenged.jun.unreal.spline.USplineComponentKt",
		"setDrawDebug",
		"(JZ)V"
	);

	/*RegisterNative<+[](USplineComponent* Component, const FSplinePoint* Data, size_t ElementCount, ESplineCoordinateSpace::Type CoordinateSpace, jboolean UpdateSpline) -> void {
		Component->SetSplinePoints(TArray<FSplinePoint>(Data, ElementCount), CoordinateSpace, J2U<bool>(UpdateSpline));
	}>(
		"com.cerebrallychallenged.jun.unreal.spline.USplineComponentKt",
		"setSplinePoints",
		"(JJJIZ)V"
	);*/

	RegisterNative<+[](USplineComponent* Component) -> void {
		Component->UpdateSpline();
	}>(
		"com.cerebrallychallenged.jun.unreal.spline.USplineComponentKt",
		"updateSpline",
		"(J)V"
	);
}
