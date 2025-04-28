#include "JunManagerRMC.h"
#include "RuntimeMeshComponent.h"
#include "RuntimeMeshProvider.h"

void FJunManagerRMC::RegisterNatives_URuntimeMeshComponent()
{
	RegisterNative<+[](URuntimeMeshComponent* Component) -> URuntimeMesh* {
		return Component->GetOrCreateRuntimeMesh();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"getOrCreateRuntimeMesh",
		"(J)J"
	);

	RegisterNative<+[](URuntimeMeshComponent* Component) -> URuntimeMeshProvider* {
		return Component->GetProvider();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"getProvider",
		"(J)J"
	);

	RegisterNative<+[](URuntimeMeshComponent* Component) -> URuntimeMesh* {
		return Component->GetRuntimeMesh();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"getRuntimeMesh",
		"(J)J"
	);

	RegisterNative<+[](URuntimeMeshComponent* Component) -> ERuntimeMeshMobility {
		return Component->GetRuntimeMeshMobility();
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"getRuntimeMeshMobility",
		"(J)B"
	);

	RegisterNative<+[](URuntimeMeshComponent* Component, URuntimeMeshProvider* Provider) -> void {
		Component->Initialize(Provider);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"initialize",
		"(JJ)V"
	);

	RegisterNative<+[](URuntimeMeshComponent* Component, URuntimeMesh* NewMesh) -> void {
		Component->SetRuntimeMesh(NewMesh);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"setRuntimeMesh",
		"(JJ)V"
	);

	RegisterNative<+[](URuntimeMeshComponent* Component, ERuntimeMeshMobility NewMobility) -> void {
		Component->SetRuntimeMeshMobility(NewMobility);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.URuntimeMeshComponentKt",
		"setRuntimeMeshMobility",
		"(JB)V"
	);
}