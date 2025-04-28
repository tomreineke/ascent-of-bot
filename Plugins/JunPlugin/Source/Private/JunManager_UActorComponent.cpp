#include "JunManager.h"

void FJunManager::RegisterNatives_UActorComponent()
{
	RegisterNative<+[](UActorComponent* Component, jboolean PromoteChildren) -> void {
		Component->DestroyComponent(J2U<bool>(PromoteChildren));
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"destroyComponent",
		"(JZ)V"
	);

	RegisterNative<+[](UActorComponent* Component) -> jboolean {
		return U2J(Component->IsRegistered());
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"isRegistered",
		"(J)Z"
	);

	RegisterNative<+[](UActorComponent* Component) -> void {
		Component->MarkRenderDynamicDataDirty();
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"markRenderDynamicDataDirty",
		"(J)V"
	);

	RegisterNative<+[](UActorComponent* Component) -> void {
		Component->MarkRenderStateDirty();
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"markRenderStateDirty",
		"(J)V"
	);

	RegisterNative<+[](UActorComponent* Component) -> void {
		Component->MarkRenderTransformDirty();
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"markRenderTransformDirty",
		"(J)V"
	);

	RegisterNative<+[](UActorComponent* Component) -> void {
		Component->RegisterComponent();
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"registerComponent",
		"(J)V"
	);

	RegisterNative<+[](UActorComponent* Component) -> void {
		Component->UnregisterComponent();
	}>(
		"com.cerebrallychallenged.jun.unreal.UActorComponentKt",
		"unregisterComponent",
		"(J)V"
	);
}