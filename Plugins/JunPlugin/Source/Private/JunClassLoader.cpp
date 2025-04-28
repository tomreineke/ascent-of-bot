#include "JunClassLoader.h"

#include "GenericPlatform/GenericPlatformFile.h"
#include "HAL/FileManager.h"
#include "HAL/PlatformFilemanager.h"
#include "Misc/Paths.h"

#include "JunConv.h"
#include "JunManager.h"
#include "JunJNI.h"
#include "JunStringUtil.h"


FJunClassLoader::FJunClassLoader(jclass ClassLoaderClass, jobject ClassLoader) : ClassLoader(GEnv->NewGlobalRef(ClassLoader))
{
	//AddPathID = GEnv->GetMethodID(ClassLoaderClass, "addPath", "(Ljava/lang/String;)Z");
	LoadClassID = GEnv->GetMethodID(ClassLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
}

FJunClassLoader::~FJunClassLoader()
{
	GEnv->DeleteGlobalRef(ClassLoader);
}

//void FJunClassLoader::AddPath(const TCHAR* Path)
//{
//	jstring ClassPathString = U2J(Path);
//	jboolean bSuccess = GEnv->CallBooleanMethod(ClassLoader, AddPathID, ClassPathString);
//	if (bSuccess == JNI_FALSE)
//	{
//		UE_LOG(LogJun, Fatal, TEXT("Cannot add classpath '%s' to class loader"), Path);
//	}
//	GEnv->DeleteLocalRef(ClassPathString);
//}

jclass FJunClassLoader::LoadClassNullableLocalRef(const char* ClassName) const
{
	jstring ClassNameString = GEnv->NewStringUTF(ClassName);
	jobject Object = GEnv->CallObjectMethod(ClassLoader, LoadClassID, ClassNameString);
	jthrowable Ex = GEnv->ExceptionOccurred();
	if (Ex != nullptr) {
		FString ExDesc = JunExtractException(GEnv, Ex);
		UE_LOG(LogJun, Fatal, TEXT("Exception loading class %s: %s"), UTF8_TO_TCHAR(ClassName), *ExDesc);
	}
	jclass Class = static_cast<jclass>(Object);
	GEnv->DeleteLocalRef(ClassNameString);
	return Class;
}

jclass FJunClassLoader::LoadClassLocalRef(const char* ClassName) const
{
	jclass LocalClass = LoadClassNullableLocalRef(ClassName);
	if (LocalClass == nullptr)
	{
		UE_LOG(
			LogJun,
			Fatal,
			TEXT("Cannot find class %s"),
			UTF8_TO_TCHAR(ClassName)
		);
	}
	return LocalClass;
}

jclass FJunClassLoader::LoadClassGlobalRef(const char* ClassName) const
{
	jclass LocalClass = LoadClassLocalRef(ClassName);
	return static_cast<jclass>(GEnv->NewGlobalRef(LocalClass));
}

jobject FJunClassLoader::GetClassLoader() const
{
	return ClassLoader;
}