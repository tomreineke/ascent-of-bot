#include "JunManager.h"
#include "NiagaraComponent.h"

void FJunManager::RegisterNatives_UNiagaraComponent()
{
	RegisterNative<+[](UNiagaraComponent* Component) -> UNiagaraSystem* {
		return Component->GetAsset();
	}>(
		"com.cerebrallychallenged.jun.unreal.niagara.UNiagaraComponentKt",
		"getAsset",
		"(J)J"
	);

	RegisterNative<+[](UNiagaraComponent* Component) -> jboolean {
		return U2J(Component->IsComplete());
	}>(
		"com.cerebrallychallenged.jun.unreal.niagara.UNiagaraComponentKt",
		"isComplete",
		"(J)Z"
	);

	RegisterNative<+[](UNiagaraComponent* Component, UNiagaraSystem* Asset, jboolean bResetExistingOverrideParameters) -> void {
		Component->SetAsset(Asset, J2U<bool>(bResetExistingOverrideParameters));
	}>(
		"com.cerebrallychallenged.jun.unreal.niagara.UNiagaraComponentKt",
		"setAsset",
		"(JJZ)V"
	);
}
