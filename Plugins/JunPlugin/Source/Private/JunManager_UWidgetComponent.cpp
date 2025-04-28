#include "JunManager.h"
#include "Components/WidgetComponent.h"

void FJunManager::RegisterNatives_UWidgetComponent()
{
	RegisterNative<+[](UWidgetComponent* Component) -> jobject {
		return U2J(Component->GetDrawSize());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"getDrawSize",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](UWidgetComponent* Component) -> ULocalPlayer* {
		return Component->GetOwnerPlayer();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"getOwnerPlayer",
		"(J)J"
	);

	RegisterNative<+[](UWidgetComponent* Component) -> jobject {
		return U2J(Component->GetPivot());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"getPivot",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](UWidgetComponent* Component) -> FJunSharedRef* {
		return U2J(Component->GetSlateWidget());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"getSlateWidget",
		"(J)J"
	);

	RegisterNative<+[](UWidgetComponent* Component) -> UUserWidget* {
		return Component->GetUserWidgetObject();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"getWidget",
		"(J)J"
	);

	RegisterNative<+[](UWidgetComponent* Component) -> EWidgetSpace {
		return Component->GetWidgetSpace();
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"getWidgetSpace",
		"(J)B"
	);

	RegisterNative<+[](UWidgetComponent* Component, jobject Size) -> void {
		Component->SetDrawSize(J2U<FVector2D>(Size));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"setDrawSize",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;)V"
	);

	RegisterNative<+[](UWidgetComponent* Component, ULocalPlayer* LocalPlayer) -> void {
		Component->SetOwnerPlayer(LocalPlayer);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"setOwnerPlayer",
		"(JJ)V"
	);

	RegisterNative<+[](UWidgetComponent* Component, jobject Pivot) -> void {
		Component->SetPivot(J2U<FVector2D>(Pivot));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"setPivot",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;)V"
	);

	RegisterNative<+[](UWidgetComponent* Component, FJunSharedRef* Widget) -> void {
		Component->SetSlateWidget(J2U<TSharedPtr<SWidget>>(Widget));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"setSlateWidget",
		"(JJ)V"
	);

	RegisterNative<+[](UWidgetComponent* Component, UUserWidget* Widget) -> void {
		Component->SetWidget(Widget);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"setWidget",
		"(JJ)V"
	);

	RegisterNative<+[](UWidgetComponent* Component, EWidgetSpace NewSpace) -> void {
		Component->SetWidgetSpace(NewSpace);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.UWidgetComponentKt",
		"setWidgetSpace",
		"(JB)V"
	);
}