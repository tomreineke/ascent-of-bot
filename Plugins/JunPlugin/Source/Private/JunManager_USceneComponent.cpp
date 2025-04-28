#include "JunManager.h"

void FJunManager::RegisterNatives_USceneComponent()
{
	RegisterNative<+[](USceneComponent* Component, USceneComponent* Parent, jint AttachmentRulesMagic, jstring SocketNameJ) -> void {
		FJunString SocketName = SocketNameJ;
		FAttachmentTransformRules AttachmentRules = J2UFAttachmentTransformRules(AttachmentRulesMagic);
		Component->AttachToComponent(Parent, AttachmentRules, SocketName);
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"attachToComponent",
		"(JJILjava/lang/String;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jint DetachmentRulesMagic) -> void {
		FDetachmentTransformRules DetachmentRules = J2UFDetachmentTransformRules(DetachmentRulesMagic);
		Component->DetachFromComponent(DetachmentRules);
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"detachFromComponent",
		"(JI)V"
	);

	RegisterNative <+[](USceneComponent* Component, jstring SocketNameJ) -> jboolean {
		FJunString SocketName = SocketNameJ;
		return U2J(Component->DoesSocketExist(SocketName));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"doesSocketExist",
		"(JLjava/lang/String;)Z"
	);

	RegisterNative <+[](USceneComponent* Component) -> USceneComponent* {
		return Component->GetAttachParent();
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getAttachParent",
		"(J)J"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetComponentLocation());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getComponentLocation",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetComponentQuat());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getComponentQuat",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Quaternion;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetComponentScale());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getComponentScale",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetComponentTransform());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getComponentTransform",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative<+[](USceneComponent* Component) -> EComponentMobility::Type {
		return Component->Mobility;
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getMobility",
		"(J)I"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetRelativeLocation());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getRelativeLocation",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetRelativeRotation().Quaternion());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getRelativeRotation",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Quaternion;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetRelativeScale3D());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getRelativeScale3D",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jobject {
		return U2J(Component->GetRelativeTransform());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getRelativeTransform",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative <+[](USceneComponent* Component, jstring SocketNameJ) -> jobject {
		FJunString SocketName = SocketNameJ;
		return U2J(Component->GetSocketLocation(SocketName));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getSocketLocation",
		"(JLjava/lang/String;)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USceneComponent* Component, jstring SocketNameJ) -> jobject {
		FJunString SocketName = SocketNameJ;
		return U2J(Component->GetSocketQuaternion(SocketName));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getSocketQuaternion",
		"(JLjava/lang/String;)Lcom/cerebrallychallenged/jun/math/geo/Quaternion;"
	);

	RegisterNative<+[](USceneComponent* Component, jstring SocketNameJ) -> jobject {
		FJunString SocketName = SocketNameJ;
		return U2J(Component->GetSocketTransform(SocketName));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getSocketTransform",
		"(JLjava/lang/String;)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative<+[](USceneComponent* Component) -> jboolean {
		return U2J(Component->IsVisible());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"getVisibility",
		"(J)Z"
	);

	RegisterNative<+[](USceneComponent* Component, EComponentMobility::Type NewMobility) -> void {
		Component->SetMobility(NewMobility);
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setMobility",
		"(JI)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewLocation) -> void {
		Component->SetRelativeLocation(J2U<FVector>(NewLocation));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setRelativeLocation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewLocation, jobject NewRotation) -> void {
		Component->SetRelativeLocationAndRotation(J2U<FVector>(NewLocation), J2U<FQuat>(NewRotation));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setRelativeLocationAndRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewRotation) -> void {
		Component->SetRelativeRotation(J2U<FQuat>(NewRotation).Rotator());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setRelativeRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewScale3D) -> void {
		Component->SetRelativeScale3D(J2U<FVector>(NewScale3D));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setRelativeScale3D",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewTransform) -> void {
		Component->SetRelativeTransform(J2U<FTransform>(NewTransform));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setRelativeTransform",
		"(JLcom/cerebrallychallenged/jun/math/geo/Transform3f;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jboolean NewVisibility, jboolean PropagateToChildren) -> void {
		Component->SetVisibility(J2U<bool>(NewVisibility), J2U<bool>(PropagateToChildren));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setVisibility",
		"(JZZ)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewLocation) -> void {
		Component->SetWorldLocation(J2U<FVector>(NewLocation));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setWorldLocation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewLocation, jobject NewRotation) -> void {
		Component->SetWorldLocationAndRotation(J2U<FVector>(NewLocation), J2U<FQuat>(NewRotation));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setWorldLocationAndRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewRotation) -> void {
		Component->SetWorldRotation(J2U<FQuat>(NewRotation).Rotator());
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setWorldRotation",
		"(JLcom/cerebrallychallenged/jun/math/geo/Quaternion;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewScale3D) -> void {
		Component->SetWorldScale3D(J2U<FVector>(NewScale3D));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setWorldScale3D",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;)V"
	);

	RegisterNative<+[](USceneComponent* Component, jobject NewTransform) -> void {
		Component->SetWorldTransform(J2U<FTransform>(NewTransform));
	}>(
		"com.cerebrallychallenged.jun.unreal.USceneComponentKt",
		"setWorldTransform",
		"(JLcom/cerebrallychallenged/jun/math/geo/Transform3f;)V"
	);
}