#include "JunManager.h"

void FJunManager::RegisterNatives_STextBlock()
{
	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(SNew(STextBlock));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"createBySNewImpl",
		"()J"
	);

	RegisterNative<+[](STextBlock* TextBlock) -> jstring {
		return U2J(TextBlock->GetText().ToString());
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"getText",
		"(J)Ljava/lang/String;"
	);

	RegisterNative<+[](STextBlock* TextBlock, jobject Color) -> void {
		TextBlock->SetColorAndOpacity(FSlateColor(J2U<FLinearColor>(Color)));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"setColorAndOpacity",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec4f;)V"
	);

	RegisterNative<+[](STextBlock* TextBlock, FJunSharedRef* Font) -> void {
		FSlateFontInfo FontInfo = *J2U<TSharedRef<FSlateFontInfo>>(Font);
		TextBlock->SetFont(FontInfo);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"setFont",
		"(JJ)V"
	);

	RegisterNative<+[](STextBlock* TextBlock, ETextJustify::Type Justification) -> void {
		TextBlock->SetJustification(Justification);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"setJustification",
		"(JI)V"
	);

	RegisterNative<+[](STextBlock* TextBlock, jfloat LineHeightPercentage) -> void {
		TextBlock->SetLineHeightPercentage(LineHeightPercentage);
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"setLineHeightPercentage",
		"(JF)V"
	);

	RegisterNative<+[](STextBlock* TextBlock, jstring TextJ) -> void {
		TextBlock->SetText(J2U<FText>(TextJ));
	}>(
		"com.cerebrallychallenged.jun.unreal.widget.STextBlockKt",
		"setText",
		"(JLjava/lang/String;)V"
	);
}