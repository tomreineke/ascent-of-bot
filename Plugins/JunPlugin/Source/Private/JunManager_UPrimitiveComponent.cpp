#include "JunManager.h"

void FJunManager::RegisterNatives_UPrimitiveComponent()
{
	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastCinematicShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastCinematicShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastDynamicShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastDynamicShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastFarShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastFarShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastHiddenShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastHiddenShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastInsetShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastInsetShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->CastShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastShadowAsTwoSided);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastShadowAsTwoSided",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastStaticShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastStaticShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bCastVolumetricTranslucentShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCastVolumetricTranslucentShadow",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> ECollisionEnabled::Type {
		return Component->GetCollisionEnabled();
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCollisionEnabled",
		"(J)I"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jint {
		return Component->CustomDepthStencilValue;
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getCustomDepthStencilValue",
		"(J)I"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jint Index) -> UMaterialInterface* {
		return Component->GetMaterial(Index);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getMaterial",
		"(JI)J"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jint {
		return Component->GetNumMaterials();
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getNumMaterials",
		"(J)I"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bRenderCustomDepth);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getRenderCustomDepth",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->IsSimulatingPhysics());
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getSimulatePhysics",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bUseAsOccluder);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getUseAsOccluder",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bVisibleInRayTracing);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getVisibleInRayTracing",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component) -> jboolean {
		return U2J(Component->bVisibleInReflectionCaptures);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"getVisibleInReflectionCaptures",
		"(J)Z"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastCinematicShadow = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastCinematicShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastDynamicShadow = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastDynamicShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastFarShadow = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastFarShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastHiddenShadow = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastHiddenShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->SetCastInsetShadow(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastInsetShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->SetCastShadow(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastShadowAsTwoSided = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastShadowAsTwoSided",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastStaticShadow = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastStaticShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bCastVolumetricTranslucentShadow = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCastVolumetricTranslucentShadow",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, ECollisionEnabled::Type Value) -> void {
		Component->SetCollisionEnabled(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCollisionEnabled",
		"(JI)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jint Value) -> void {
		Component->SetCustomDepthStencilValue(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setCustomDepthStencilValue",
		"(JI)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jint Index, UMaterialInterface* Material) -> void {
		Component->SetMaterial(Index, Material);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setMaterial",
		"(JIJ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->SetRenderCustomDepth(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setRenderCustomDepth",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->SetSimulatePhysics(J2U<bool>(Value));
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setSimulatePhysics",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bUseAsOccluder = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setUseAsOccluder",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bVisibleInRayTracing = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setVisibleInRayTracing",
		"(JZ)V"
	);

	RegisterNative<+[](UPrimitiveComponent* Component, jboolean Value) -> void {
		Component->bVisibleInReflectionCaptures = J2U<bool>(Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.UPrimitiveComponentKt",
		"setVisibleInReflectionCaptures",
		"(JZ)V"
	);
}
