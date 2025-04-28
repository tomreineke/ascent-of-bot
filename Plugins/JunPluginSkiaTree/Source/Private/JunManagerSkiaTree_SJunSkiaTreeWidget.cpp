#include "JunManagerSkiaTree.h"
#include "JunSkiaTreeWidget.h"
#include "skiatree.hpp"

void FJunManagerSkiaTree::RegisterNatives_SJunSkiaTreeWidget()
{
	RegisterNative<+[](skiatree::SkiaTreeLibrary* Library, const skiatree::RefCell<skiatree::Forest>* Forest) -> FJunSharedRef* {
		return U2J(SNew(SJunSkiaTreeWidget).Library(Library).Forest(Forest).LifeGuard(GJunManager->LifeGuard));
	}>(
		"com.cerebrallychallenged.jun.unreal.skiatree.SJunSkiaTreeWidgetKt",
		"createBySNewImpl",
		"(JJ)J"
	);

	RegisterNative<+[](
		SJunSkiaTreeWidget* Widget,
		FJunSkiaTreeWidgetTick WidgetTick,
		FJunSkiaTreeWidgetResize WidgetResize
	) -> void {
		return Widget->SetUpcalls(WidgetTick, WidgetResize);
	}>(
		"com.cerebrallychallenged.jun.unreal.skiatree.SJunSkiaTreeWidgetKt",
		"setUpcalls",
		"(JJJ)V"
	);

	RegisterNative<+[](SJunSkiaTreeWidget* Widget, jobject Position) -> jboolean {
		return U2J(Widget->IsPixelCovered(J2U<FIntPoint>(Position)));
	}>(
		"com.cerebrallychallenged.jun.unreal.skiatree.SJunSkiaTreeWidgetKt",
		"isPixelCovered",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2i;)Z"
	);
}
