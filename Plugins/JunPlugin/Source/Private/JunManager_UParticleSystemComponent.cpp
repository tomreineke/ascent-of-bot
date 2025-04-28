#include "JunManager.h"

void FJunManager::RegisterNatives_UParticleSystemComponent()
{
	RegisterNative<+[](UParticleSystemComponent* Component) -> UParticleSystem* {
		return Component->Template;
	}>(
		"com.cerebrallychallenged.jun.unreal.UParticleSystemComponentKt",
		"getTemplate",
		"(J)J"
	);

	RegisterNative<+[](UParticleSystemComponent* Component) -> jboolean {
		return U2J(Component->HasCompleted());
	}>(
		"com.cerebrallychallenged.jun.unreal.UParticleSystemComponentKt",
		"hasCompleted",
		"(J)Z"
	);

	RegisterNative<+[](UParticleSystemComponent* Component, UParticleSystem* Template) -> void {
		Component->SetTemplate(Template);
	}>(
		"com.cerebrallychallenged.jun.unreal.UParticleSystemComponentKt",
		"setTemplate",
		"(JJ)V"
	);
}
