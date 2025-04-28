#include "JunManager.h"
#include "Components/TextRenderComponent.h"

void FJunManager::RegisterNatives_UTextRenderComponent()
{
	RegisterNative<+[](UTextRenderComponent* Component) -> UFont* {
		return Component->Font;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getFont",
		"(J)J"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> EHorizTextAligment {
		return Component->HorizontalAlignment;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getHorizontalAlignment",
		"(J)I"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> jfloat {
		return Component->HorizSpacingAdjust;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getHorizSpacingAdjust",
		"(J)F"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> jstring {
		return U2J(Component->Text);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getText",
		"(J)Ljava/lang/String;"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> UMaterialInterface* {
		return Component->TextMaterial;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getTextMaterial",
		"(J)J"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> FColor {
		return Component->TextRenderColor;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getTextRenderColor",
		"(J)I"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> EVerticalTextAligment {
		return Component->VerticalAlignment;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getVerticalAlignment",
		"(J)I"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> jfloat {
		return Component->VertSpacingAdjust;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getVertSpacingAdjust",
		"(J)F"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> jfloat {
		return Component->WorldSize;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getWorldSize",
		"(J)F"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> jfloat {
		return Component->XScale;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getXScale",
		"(J)F"
	);

	RegisterNative<+[](UTextRenderComponent* Component) -> jfloat {
		return Component->YScale;
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"getYScale",
		"(J)F"
	);

	RegisterNative<+[](UTextRenderComponent* Component, UFont* Font) -> void {
		Component->SetFont(Font);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setFont",
		"(JJ)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, EHorizTextAligment NewValue) -> void {
		Component->SetHorizontalAlignment(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setHorizontalAlignment",
		"(JI)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, jfloat NewValue) -> void {
		Component->SetHorizSpacingAdjust(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setHorizSpacingAdjust",
		"(JF)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, jstring TextJ) -> void {
		Component->SetText(J2U<FText>(TextJ));
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setText",
		"(JLjava/lang/String;)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, UMaterialInterface* Material) -> void {
		Component->SetTextMaterial(Material);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setTextMaterial",
		"(JJ)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, FColor Color) -> void {
		Component->SetTextRenderColor(Color);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setTextRenderColor",
		"(JI)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, EVerticalTextAligment NewValue) -> void {
		Component->SetVerticalAlignment(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setVerticalAlignment",
		"(JI)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, jfloat NewValue) -> void {
		Component->SetVertSpacingAdjust(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setVertSpacingAdjust",
		"(JF)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, jfloat NewValue) -> void {
		Component->SetWorldSize(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setWorldSize",
		"(JF)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, jfloat NewValue) -> void {
		Component->SetXScale(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setXScale",
		"(JF)V"
	);

	RegisterNative<+[](UTextRenderComponent* Component, jfloat NewValue) -> void {
		Component->SetYScale(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.UTextRenderComponentKt",
		"setYScale",
		"(JF)V"
	);
}