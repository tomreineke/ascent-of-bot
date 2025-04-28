#pragma once

#include "CoreMinimal.h"
#include "jni.h"
#include "Input/Events.h"
#include "Layout/Visibility.h"
#include "JunTraits.h"

inline jboolean U2J(bool Value)
{
	return Value ? JNI_TRUE : JNI_FALSE;
}

template<typename T, JUN_IF_SAME((T), (bool)) = 0>
inline bool J2U(jboolean Value)
{
	return Value != 0;
}

JUNPLUGIN_API jint U2J(EVisibility Visibility);

JUNPLUGIN_API EVisibility J2UEVisibility(jint VisibilityMagic);

JUNPLUGIN_API FAttachmentTransformRules J2UFAttachmentTransformRules(jint Magic);

JUNPLUGIN_API FDetachmentTransformRules J2UFDetachmentTransformRules(jint Magic);

JUNPLUGIN_API jint EncodeModifiers(const FInputEvent& Event);

JUNPLUGIN_API jint EncodeMouseButton(const FPointerEvent& Event);
