#include "JunManager.h"

void FJunManager::RegisterNatives_ULightComponent()
{
	RegisterNative<+[](ULightComponent* Component) -> jboolean {
		return U2J(Component->bAffectTranslucentLighting);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getAffectTranslucentLighting",
		"(J)Z"
	);

	RegisterNative<+[](ULightComponent* Component) -> jfloat {
		return Component->BloomScale;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getBloomScale",
		"(J)F"
	);

	RegisterNative<+[](ULightComponent* Component) -> jfloat {
		return Component->BloomThreshold;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getBloomThreshold",
		"(J)F"
	);

	RegisterNative<+[](ULightComponent* Component) -> FColor {
		return Component->BloomTint;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getBloomTint",
		"(J)I"
	);

	RegisterNative<+[](ULightComponent* Component) -> jboolean {
		return U2J(Component->bEnableLightShaftBloom);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getEnableLightShaftBloom",
		"(J)Z"
	);

	RegisterNative<+[](ULightComponent* Component) -> jboolean {
		return U2J(Component->bForceCachedShadowsForMovablePrimitives);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getForceCachedShadowsForMovablePrimitives",
		"(J)Z"
	);

	RegisterNative<+[](ULightComponent* Component) -> jfloat {
		return Component->IESBrightnessScale;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getIESBrightnessScale",
		"(J)F"
	);

	RegisterNative<+[](ULightComponent* Component) -> UTextureLightProfile* {
		return Component->IESTexture;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getIESTexture",
		"(J)J"
	);

	RegisterNative<+[](ULightComponent* Component) -> jfloat {
		return Component->IndirectLightingIntensity;
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getIndirectLightingIntensity",
		"(J)F"
	);

	RegisterNative<+[](ULightComponent* Component) -> jobject {
		return U2J(FLinearColor(Component->LightColor));
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"getLightColor",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec4f;"
	);

	RegisterNative<+[](ULightComponent* Component, jboolean NewValue) -> void {
		Component->SetAffectTranslucentLighting(J2U<bool>(NewValue));
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setAffectTranslucentLighting",
		"(JZ)V"
	);

	RegisterNative<+[](ULightComponent* Component, jfloat NewValue) -> void {
		Component->SetBloomScale(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setBloomScale",
		"(JF)V"
	);

	RegisterNative<+[](ULightComponent* Component, jfloat NewValue) -> void {
		Component->SetBloomThreshold(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setBloomThreshold",
		"(JF)V"
	);

	RegisterNative<+[](ULightComponent* Component, FColor Color) -> void {
		Component->SetBloomTint(Color);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setBloomTint",
		"(JI)V"
	);

	RegisterNative<+[](ULightComponent* Component, jboolean NewValue) -> void {
		Component->SetEnableLightShaftBloom(J2U<bool>(NewValue));
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setEnableLightShaftBloom",
		"(JZ)V"
	);

	RegisterNative<+[](ULightComponent* Component, jboolean NewValue) -> void {
		Component->SetForceCachedShadowsForMovablePrimitives(J2U<bool>(NewValue));
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setForceCachedShadowsForMovablePrimitives",
		"(JZ)V"
	);

	RegisterNative<+[](ULightComponent* Component, jfloat NewValue) -> void {
		Component->SetIESBrightnessScale(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setIESBrightnessScale",
		"(JF)V"
	);

	RegisterNative<+[](ULightComponent* Component, UTextureLightProfile* NewValue) -> void {
		Component->SetIESTexture(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setIESTexture",
		"(JJ)V"
	);

	RegisterNative<+[](ULightComponent* Component, jfloat NewValue) -> void {
		Component->SetIndirectLightingIntensity(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setIndirectLightingIntensity",
		"(JF)V"
	);

	RegisterNative<+[](ULightComponent* Component, jfloat NewValue) -> void {
		Component->SetIntensity(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setIntensity",
		"(JF)V"
	);

	RegisterNative<+[](ULightComponent* Component, jobject NewValue) -> void {
		Component->SetLightColor(J2U<FLinearColor>(NewValue));
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"setLightColor",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec4f;)V"
	);

	RegisterNative<+[](ULightComponent* Component) -> void {
		Component->UpdateColorAndBrightness();
	}>(
		"com.cerebrallychallenged.jun.unreal.light.ULightComponentKt",
		"updateColorAndBrightness",
		"(J)V"
	);
}