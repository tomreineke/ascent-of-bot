#include "JunManager.h"

void FJunManager::RegisterNatives_USkeletalMeshComponent()
{
	RegisterNative<+[](USkeletalMeshComponent* Component, USkeletalMesh* NewMesh, jboolean bReinitPose) -> void {
		Component->SetSkeletalMesh(NewMesh, J2U<bool>(bReinitPose));
	} > (
		"com.cerebrallychallenged.jun.unreal.mesh.USkeletalMeshComponentKt",
		"setSkeletalMesh",
		"(JJZ)V"
	);
}