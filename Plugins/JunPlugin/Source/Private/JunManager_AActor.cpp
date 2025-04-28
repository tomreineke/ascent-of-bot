#include "JunManager.h"
#include "GameFramework/Actor.h"

void FJunManager::RegisterNatives_AActor()
{
	RegisterNative<+[](AActor* Actor, AActor* ParentActor, jint AttachmentRulesMagic, jstring SocketNameJ) -> void {
		FJunString SocketName = SocketNameJ;
		FAttachmentTransformRules AttachmentRules = J2UFAttachmentTransformRules(AttachmentRulesMagic);
		Actor->AttachToActor(ParentActor, AttachmentRules, SocketName);
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"attachToActor",
		"(JJILjava/lang/String;)V"
	);

	RegisterNative<+[](AActor* Actor, USceneComponent* Component, jint AttachmentRulesMagic, jstring SocketNameJ) -> void {
		FJunString SocketName = SocketNameJ;
		FAttachmentTransformRules AttachmentRules = J2UFAttachmentTransformRules(AttachmentRulesMagic);
		Actor->AttachToComponent(Component, AttachmentRules, SocketName);
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"attachToComponent",
		"(JJILjava/lang/String;)V"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetActorLocation());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getActorLocation",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetActorQuat());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getActorQuat",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Quaternion;"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetActorRelativeScale3D());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getActorRelativeScale3D",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetActorScale());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getActorScale",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetActorScale3D());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getActorScale3D",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetActorTransform());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getActorTransform",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative < +[](AActor* Actor) -> AActor* {
		return Actor->GetAttachParentActor();
	} > (
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getAttachParentActor",
		"(J)J"
	);

	RegisterNative < +[](AActor* Actor) -> USceneComponent* {
		return Actor->GetRootComponent();
	} > (
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getRootComponent",
		"(J)J"
	);

	RegisterNative<+[](AActor* Actor) -> jobject {
		return U2J(Actor->GetTransform());
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getTransform",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative<+[](AActor* Actor) -> UWorld* {
		return Actor->GetWorld();
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"getWorld",
		"(J)J"
	);

	RegisterNative<+[](AActor* Actor) -> void {
		return Actor->PostInitializeComponents();
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"postInitializeComponents",
		"(J)V"
	);

	RegisterNative<+[](AActor* Actor, jobject Location) -> void {
		Actor->SetActorLocation(J2U<FVector>(Location));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorLocation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject Location, jobject Rotation) -> void {
		Actor->SetActorLocationAndRotation(
			J2U<FVector>(Location),
			J2U<FQuat>(Rotation)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorLocationAndRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject RelativeLocation) -> void {
		Actor->SetActorRelativeLocation(J2U<FVector>(RelativeLocation));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorRelativeLocation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject RelativeRotation) -> void {
		Actor->SetActorRelativeRotation(J2U<FQuat>(RelativeRotation));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorRelativeRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject RelativeScale) -> void {
		Actor->SetActorRelativeScale3D(J2U<FVector>(RelativeScale));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorRelativeScale3D",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject RelativeTransform) -> void {
		Actor->SetActorRelativeTransform(J2U<FTransform>(RelativeTransform));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorRelativeTransform",
		"(JLcom/cerebrallychallenged/jun/math/geo/Transform3f;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject Rotation) -> void {
		Actor->SetActorRotation(J2U<FQuat>(Rotation));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject Scale) -> void {
		Actor->SetActorScale3D(J2U<FVector>(Scale));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorScale3D",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](AActor* Actor, jobject Transform) -> void {
		Actor->SetActorTransform(J2U<FTransform>(Transform));
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setActorTransform",
		"(JLcom/cerebrallychallenged/jun/math/geo/Transform3f;)V"
	);

	RegisterNative<+[](AActor* Actor, USceneComponent* NewRootComponent) -> void {
		Actor->SetRootComponent(NewRootComponent);
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"setRootComponent",
		"(JJ)V"
	);

	RegisterNative<+[](UWorld* World, UClass* ActorClass, jstring NameJ, jobject TransformJ) -> AActor* {
		FActorSpawnParameters SpawnParameters;
		FString Name;
		if (NameJ != nullptr)
		{
			Name = J2U<FString>(NameJ);
			SpawnParameters.Name = *Name;
		}
		FTransform Transform = TransformJ != nullptr ? J2U<FTransform>(TransformJ) : FTransform::Identity;
		AActor* Actor = World->SpawnActor(ActorClass, &Transform, SpawnParameters);
		return Actor;
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"spawnActor",
		"(JJLjava/lang/String;Lcom/cerebrallychallenged/jun/math/geo/Transform3f;)J"
	);

	RegisterNative <+[](AActor* Actor) -> void {
		Actor->UpdateComponentTransforms();
	}>(
		"com.cerebrallychallenged.jun.unreal.AActorKt",
		"updateComponentTransforms",
		"(J)V"
	);
}
