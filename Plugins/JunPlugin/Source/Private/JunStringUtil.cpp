#include "JunStringUtil.h"
#include "JunManager.h"
#include "JunConv.h"

JUNPLUGIN_API jstring U2J(FString String, JNIEnv* Env)
{
	return Env->NewString(TCHAR_TO_JCHAR(*String), String.Len());
}

JUNPLUGIN_API jstring U2J(const TCHAR* String, JNIEnv* Env)
{
	return Env->NewString(TCHAR_TO_JCHAR(String), _tcslen(String));
}

JUNPLUGIN_API jstring U2J(const char* String, JNIEnv* Env)
{
	return Env->NewStringUTF(String);
}

JUNPLUGIN_API jstring U2J(FText Text, JNIEnv* Env)
{
	return U2J(Text.ToString(), Env);
}

/*
 * Obtains a string representation for the specified exception.
 * Is used when no instance to JunApplication is available, which could output the exception via logging.
 */
JUNPLUGIN_API FString JunExtractException(JNIEnv* Env, jthrowable Exception)
{
	jclass StringWriterClass = Env->FindClass("java/io/StringWriter");
	jmethodID StringWriterConstructorID = Env->GetMethodID(StringWriterClass, "<init>", "()V");

	jclass PrintWriterClass = Env->FindClass("java/io/PrintWriter");
	jmethodID PrintWriterConstructorID = Env->GetMethodID(PrintWriterClass, "<init>", "(Ljava/io/Writer;)V");

	jclass ThrowableClass = Env->FindClass("java/lang/Throwable");
	jmethodID PrintStackTraceID = Env->GetMethodID(ThrowableClass, "printStackTrace", "(Ljava/io/PrintWriter;)V");

	jclass ObjectClass = Env->FindClass("java/lang/Object");
	jmethodID ToStringID = Env->GetMethodID(ObjectClass, "toString", "()Ljava/lang/String;");

	jobject StackTrace = Env->NewObject(StringWriterClass, StringWriterConstructorID);
	jobject PrintWriter = Env->NewObject(PrintWriterClass, PrintWriterConstructorID, StackTrace);
	Env->CallVoidMethod(Exception, PrintStackTraceID, PrintWriter);
	FString Result = J2U<FString>(static_cast<jstring>(Env->CallObjectMethod(Exception, ToStringID)), Env);
	Result.Append(FString(TEXT("\nStack Trace:\n")));
	Result.Append(J2U<FString>(static_cast<jstring>(Env->CallObjectMethod(StackTrace, ToStringID)), Env));
	return Result;
}

FJunString::FJunString(jstring String) : String(String != nullptr ? J2U<FString>(String) : FString()), bHasValue(String != nullptr)
{
}

FJunString::operator FName() const
{
	if (bHasValue)
	{
		return FName(*String);
	}
	else
	{
		return NAME_None;
	}
}

FJunString::operator const FString&() const
{
	return String;
}

FJunString::operator const TCHAR*() const
{
	return *String;
}

bool FJunString::HasValue() const
{
	return bHasValue;
}