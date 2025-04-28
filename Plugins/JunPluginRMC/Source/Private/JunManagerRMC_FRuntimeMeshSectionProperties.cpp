#include "JunManagerRMC.h"
#include "RuntimeMeshRenderable.h"

void FJunManagerRMC::RegisterNatives_FRuntimeMeshSectionProperties()
{
	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jboolean {
		return U2J(Properties->bCastsShadow);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"castsShadow",
		"(J)Z"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jint {
		return Properties->MaterialSlot;
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"getMaterialSlot",
		"(J)I"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jint {
		return Properties->NumTexCoords;
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"getNumTexCoords",
		"(J)I"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> ERuntimeMeshUpdateFrequency {
		return Properties->UpdateFrequency;
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"getUpdateFrequency",
		"(J)B"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jboolean {
		return U2J(Properties->bUseHighPrecisionTangents);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"getUseHighPrecisionTangents",
		"(J)Z"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jboolean {
		return U2J(Properties->bUseHighPrecisionTexCoords);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"getUseHighPrecisionTexCoords",
		"(J)Z"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jboolean {
		return U2J(Properties->bIsVisible);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"isVisible",
		"(J)Z"
	);

	RegisterNative<+[]() -> FJunSharedRef* {
		return U2J(MakeShared<FRuntimeMeshSectionProperties>());
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"makeSharedImpl",
		"()J"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jboolean NewValue) -> void {
		Properties->bCastsShadow = J2U<bool>(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setCastsShadow",
		"(JZ)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jint NewValue) -> void {
		Properties->MaterialSlot = NewValue;
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setMaterialSlot",
		"(JI)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jint NewValue) -> void {
		Properties->NumTexCoords = NewValue;
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setNumTexCoords",
		"(JI)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, ERuntimeMeshUpdateFrequency NewValue) -> void {
		Properties->UpdateFrequency = NewValue;
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setUpdateFrequency",
		"(JB)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jboolean NewValue) -> void {
		Properties->bUseHighPrecisionTangents = J2U<bool>(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setUseHighPrecisionTangents",
		"(JZ)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jboolean NewValue) -> void {
		Properties->bUseHighPrecisionTexCoords = J2U<bool>(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setUseHighPrecisionTexCoords",
		"(JZ)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jboolean NewValue) -> void {
		Properties->bIsVisible = J2U<bool>(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setVisible",
		"(JZ)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties, jboolean NewValue) -> void {
		Properties->bWants32BitIndices = J2U<bool>(NewValue);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"setWants32BitIndices",
		"(JZ)V"
	);

	RegisterNative<+[](FRuntimeMeshSectionProperties* Properties) -> jboolean {
		return U2J(Properties->bWants32BitIndices);
	}>(
		"com.cerebrallychallenged.jun.unreal.rmc.FRuntimeMeshSectionPropertiesKt",
		"wants32BitIndices",
		"(J)Z"
	);
}