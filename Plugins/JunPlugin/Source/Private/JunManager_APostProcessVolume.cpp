#include "JunManager.h"
#include "Engine/PostProcessVolume.h"
#include "Materials/MaterialInterface.h"

void FJunManager::RegisterNatives_APostProcessVolume()
{
	RegisterNative<+[](APostProcessVolume* PostProcessVolume, UMaterialInterface* BlendableObject, jfloat Weight) -> void {
		PostProcessVolume->AddOrUpdateBlendable(TScriptInterface<IBlendableInterface>(BlendableObject), Weight);
	}>(
		"com.cerebrallychallenged.jun.unreal.postprocess.APostProcessVolumeKt",
		"addOrUpdateBlendable",
		"(JJF)V"
	);

	RegisterNative<+[](APostProcessVolume* PostProcessVolume) -> jboolean {
		return U2J(PostProcessVolume->bUnbound);
	}>(
		"com.cerebrallychallenged.jun.unreal.postprocess.APostProcessVolumeKt",
		"getUnbound",
		"(J)Z"
	);

	RegisterNative<+[](APostProcessVolume* PostProcessVolume, jboolean Unbound) -> void {
		PostProcessVolume->bUnbound = J2U<bool>(Unbound);
	}>(
		"com.cerebrallychallenged.jun.unreal.postprocess.APostProcessVolumeKt",
		"setUnbound",
		"(JZ)V"
	);
}