#pragma once

#include "CoreMinimal.h"
#include "Input/Events.h"

#include "jni.h"

#include "JunConv.h"
#include "JunJNI.h"

inline const jchar* TCHAR_TO_JCHAR(const TCHAR* String)
{
	return reinterpret_cast<const jchar*>(TCHAR_TO_WCHAR(String));
}

inline const TCHAR* JCHAR_TO_TCHAR(const jchar* String)
{
	return WCHAR_TO_TCHAR(reinterpret_cast<const wchar_t*>(String));
}

JUNPLUGIN_API jstring U2J(FString String, JNIEnv* Env = GEnv);

template<typename T, JUN_IF_SAME((T), (FString)) = 0>
inline FString J2U(jstring String, JNIEnv* Env = GEnv)
{
	const jchar* Chars = Env->GetStringCritical(String, nullptr);
	FString Result = JCHAR_TO_TCHAR(Chars);
	Env->ReleaseStringCritical(String, Chars);
	return Result;
}

JUNPLUGIN_API jstring U2J(const TCHAR* String, JNIEnv* Env = GEnv);

JUNPLUGIN_API jstring U2J(const char* String, JNIEnv* Env = GEnv);

JUNPLUGIN_API FString JunExtractException(JNIEnv* Env, jthrowable Exception);

JUNPLUGIN_API jstring U2J(FText Text, JNIEnv* Env = GEnv);

template<typename T, JUN_IF_SAME((T), (FText)) = 0>
inline FText J2U(jstring String, JNIEnv* Env = GEnv)
{
	return FText::AsCultureInvariant(J2U<FString>(String, Env));
}

class JUNPLUGIN_API FJunString {
public:
	FJunString(jstring String);
	operator FName() const;
	operator const FString&() const;
	operator const TCHAR*() const;
	bool HasValue() const;
private:
	FString String;
	bool bHasValue;
};