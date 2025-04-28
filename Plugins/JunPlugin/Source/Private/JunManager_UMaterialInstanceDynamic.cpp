#include "JunManager.h"

void FJunManager::RegisterNatives_UMaterialInstanceDynamic()
{
	RegisterNative<+[](UMaterialInterface* ParentMaterial, UObject* Outer) -> UMaterialInstanceDynamic* {
		return UMaterialInstanceDynamic::Create(ParentMaterial, Outer);
	}>(
		"com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamicKt",
		"create",
		"(JJ)J"
	);

	RegisterNative<+[](UMaterialInstanceDynamic* MaterialInstance, jstring NameJ, jfloat Value) -> void {
		FString Name = J2U<FString>(NameJ);
		MaterialInstance->SetScalarParameterValue(*Name, Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamicKt",
		"setScalarParameterValue",
		"(JLjava/lang/String;F)V"
	);

	RegisterNative<+[](UMaterialInstanceDynamic* MaterialInstance, jstring NameJ, UTexture* Value) -> void {
		FString Name = J2U<FString>(NameJ);
		MaterialInstance->SetTextureParameterValue(*Name, Value);
	}>(
		"com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamicKt",
		"setTextureParameterValue",
		"(JLjava/lang/String;J)V"
	);

	RegisterNative<+[](UMaterialInstanceDynamic* MaterialInstance, jstring NameJ, jobject Value) -> void {
		FString Name = J2U<FString>(NameJ);
		MaterialInstance->SetVectorParameterValue(*Name, J2U<FLinearColor>(Value));
	}>(
		"com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamicKt",
		"setVectorParameterValue",
		"(JLjava/lang/String;Lcom/cerebrallychallenged/jun/math/geo/Vec4f;)V"
	);
}