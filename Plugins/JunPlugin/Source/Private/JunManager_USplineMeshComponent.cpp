#include "JunManager.h"
#include "Components/SplineMeshComponent.h"

void FJunManager::RegisterNatives_USplineMeshComponent()
{
	RegisterNative<+[](USplineMeshComponent* Component, jfloat DistanceAlong) -> jobject {
		return U2J(Component->CalcSliceTransform(DistanceAlong));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"calcSliceTransform",
		"(JF)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jfloat Alpha) -> jobject {
		return U2J(Component->CalcSliceTransformAtSplineOffset(Alpha));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"calcSliceTransformAtSplineOffset",
		"(JF)Lcom/cerebrallychallenged/jun/math/geo/Transform3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		float ScaleZ;
		float MinZ;
		Component->CalculateScaleZAndMinZ(ScaleZ, MinZ);
		return U2J(FVector2D(ScaleZ, MinZ));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"calculateScaleZAndMinZ",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> void {
		Component->DestroyBodySetup();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"destroyBodySetup",
		"(J)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jfloat {
		return Component->GetBoundaryMax();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getBoundaryMax",
		"(J)F"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jfloat {
		return Component->GetBoundaryMin();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getBoundaryMin",
		"(J)F"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetEndOffset());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getEndOffset",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetEndPosition());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getEndPosition",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jfloat {
		return Component->GetEndRoll();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getEndRoll",
		"(J)F"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetEndScale());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getEndScale",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetEndTangent());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getEndTangent",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> ESplineMeshAxis::Type {
		return Component->GetForwardAxis();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getForwardAxis",
		"(J)I"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetSplineUpDir());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getSplineUpDir",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetStartOffset());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getStartOffset",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetStartPosition());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getStartPosition",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jfloat {
		return Component->GetStartRoll();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getStartRoll",
		"(J)F"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetStartScale());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getStartScale",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec2f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> jobject {
		return U2J(Component->GetStartTangent());
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"getStartTangent",
		"(J)Lcom/cerebrallychallenged/jun/math/geo/Vec3f;"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> void {
		Component->RecreateCollision();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"recreateCollision",
		"(J)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jfloat Value, jboolean UpdateMesh) -> void {
		Component->SetBoundaryMax(Value, J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setBoundaryMax",
		"(JFZ)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jfloat Value, jboolean UpdateMesh) -> void {
		Component->SetBoundaryMin(Value, J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setBoundaryMin",
		"(JFZ)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetEndOffset(J2U<FVector2D>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setEndOffset",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetEndPosition(J2U<FVector>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setEndPosition",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jfloat Value, jboolean UpdateMesh) -> void {
		Component->SetEndRoll(Value, J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setEndRoll",
		"(JFZ)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetEndScale(J2U<FVector2D>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setEndScale",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetEndTangent(J2U<FVector>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setEndTangent",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, ESplineMeshAxis::Type Value, jboolean UpdateMesh) -> void {
		Component->SetForwardAxis(Value, J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setForwardAxis",
		"(JIZ)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetSplineUpDir(J2U<FVector>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setSplineUpDir",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject StartPos, jobject StartTangent, jobject EndPos, jobject EndTangent, jboolean UpdateMesh) -> void {
		Component->SetStartAndEnd(
			J2U<FVector>(StartPos),
			J2U<FVector>(StartTangent),
			J2U<FVector>(EndPos),
			J2U<FVector>(EndTangent),
			J2U<bool>(UpdateMesh)
		);
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setStartAndEnd",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetStartOffset(J2U<FVector2D>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setStartOffset",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetStartPosition(J2U<FVector>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setStartPosition",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jfloat Value, jboolean UpdateMesh) -> void {
		Component->SetStartRoll(Value, J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setStartRoll",
		"(JFZ)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetStartScale(J2U<FVector2D>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setStartScale",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec2f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component, jobject Value, jboolean UpdateMesh) -> void {
		Component->SetStartTangent(J2U<FVector>(Value), J2U<bool>(UpdateMesh));
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"setStartTangent",
		"(JLcom/cerebrallychallenged/jun/math/geo/Vec3f;Z)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> void {
		Component->UpdateMesh();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"updateMesh",
		"(J)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> void {
		Component->UpdateMesh_Concurrent();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"updateMeshConcurrent",
		"(J)V"
	);

	RegisterNative<+[](USplineMeshComponent* Component) -> void {
		Component->UpdateRenderStateAndCollision();
	}>(
		"com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponentKt",
		"updateRenderStateAndCollision",
		"(J)V"
	);
}