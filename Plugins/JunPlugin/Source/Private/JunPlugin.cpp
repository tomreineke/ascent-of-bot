#include "IJunPlugin.h"
#include "Containers/Ticker.h"
#include "CoreMinimal.h"
#include "Engine/World.h"
#include "GenericPlatform/GenericPlatformProcess.h"
#include "HAL/FileManager.h"
#include "HAL/ThreadManager.h"
#include "Interfaces/IPluginManager.h"
#include "Misc/CoreDelegates.h"
#include "Misc/FileHelper.h"
#include "Misc/Paths.h"
#include "Modules/ModuleManager.h"
#include "XmlFile.h"
#include "XmlNode.h"

#include "JunJNI.h"
#include "JunManager.h"
#include "JunStringUtil.h"

DEFINE_LOG_CATEGORY(LogJun);

enum class EClassPathTime {
	Boot,
	BeginPlay
};

class FJunPlugin : public IJunPlugin
{
	void StartupModule() override;
	void ShutdownModule() override;
public:
	FJunPlugin();
	JavaVM* GetJvm() const override;
	JNIEnv* GetEnv() const override;

	void AddExtensionProvider(FJunManagerExtensionProvider ExtensionProvider) override;

	void RemoveExtensionProvider(FJunManagerExtensionProvider ExtensionProvider) override;
private:
	void InitJavaVm(const TCHAR* JvmDllPath, const TCHAR* ClassPath);
	void InitClassLoaderClass();
	jclass LoadClass(const char* ClassName);
	jmethodID GetMethod(jclass Class, const char* Name, const char* Sig);
	void InitDelegates();

	void OnTick(float DeltaSeconds);
	void OnPreWorldInitialization(UWorld* World, UWorld::InitializationValues InitializationValues);
	void OnPreWorldFinishDestroy(UWorld* World);

	TPair<EClassPathTime, FString> ParseXmlClassPathEntry(FXmlNode* Xml);
	static FString AbsolutePath(FString Path);

	FString PluginPath;
	TArray<FString> ClassPaths;
	JavaVM* Jvm;
	JNIEnv* Env;
	jobjectArray EmptyStringArray;
	jclass PathsClass;
	jmethodID PathsGetMethodID;
	jclass PathClass;
	jmethodID PathToURIMethodID;
	jclass URIClass;
	jmethodID URIToURLMethodID;
	jclass URLClass;
	jclass URLClassLoaderClass;
	jmethodID URLClassLoaderConstructorID;

	FString ApplicationFactoryClassName;
	FTSTicker::FDelegateHandle TickDelegateHandle;
	FDelegateHandle OnPreWorldInitializationDelegateHandle;
	FDelegateHandle OnPreWorldFinishDestroyDelegateHandle;

	TArray<FJunManagerExtensionProvider> ExtensionProviders;
};

IMPLEMENT_MODULE(FJunPlugin, JunPlugin)

EClassPathTime EClassPathTimeFromString(const FString& String)
{
	if (String.Equals(TEXT("boot")))
	{
		return EClassPathTime::Boot;
	}
	else if (String.Equals(TEXT("BeginPlay")))
	{
		return EClassPathTime::BeginPlay;
	}
	else
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot parse '%s' as EClassPathTime"), *String);

		// unreachable
		return EClassPathTime::Boot;
	}
	
}

FJunPlugin::FJunPlugin()
		: PluginPath(IPluginManager::Get().FindPlugin("JunPlugin")->GetBaseDir())
		, Jvm(nullptr)
		, Env(nullptr)
{
}

TPair<EClassPathTime, FString> FJunPlugin::ParseXmlClassPathEntry(FXmlNode* Xml)
{
	TPair<EClassPathTime, FString> Result;
	for (const FXmlAttribute& Attribute : Xml->GetAttributes())
	{
		if (Attribute.GetTag().Equals(TEXT("time")))
		{
			Result.Key = EClassPathTimeFromString(Attribute.GetValue());
		}
		else if (Attribute.GetTag().Equals(TEXT("path")))
		{
			Result.Value = Attribute.GetValue();
		}
	}
	if (Result.Value.IsEmpty())
	{
		UE_LOG(LogJun, Fatal, TEXT("<entry> in jun.xml requires path attribute."))
	}
	return Result;
}

FString FJunPlugin::AbsolutePath(FString Path)
{
	if (FPaths::IsRelative(Path))
	{
		return IFileManager::Get().ConvertToAbsolutePathForExternalAppForRead(*(FPaths::ProjectDir() / Path));
	}
	else
	{
		return Path;
	}
}

void FJunPlugin::StartupModule()
{
	if (IsRunningCommandlet())
	{
		UE_LOG(LogJun, Log, TEXT("Skipping startup of JunPlugin because run from commandlet."));
		return;
	}
	UE_LOG(LogJun, Log, TEXT("ProjectConfigDir is %s"), *FPaths::ProjectConfigDir());
	FXmlFile ConfigXml(FPaths::ProjectConfigDir() / TEXT("jun.xml"));
	FXmlNode* RootNode = ConfigXml.GetRootNode();
	ApplicationFactoryClassName = RootNode->GetAttribute(TEXT("applicationFactoryClass"));
	FString JvmDllPath = AbsolutePath(RootNode->GetAttribute(TEXT("jvmDllPath")));

	FString ClassPath;
	for (FXmlNode* TopLevelChild : RootNode->GetChildrenNodes())
	{
		if (TopLevelChild->GetTag().Equals(TEXT("classpath")))
		{
			for (FXmlNode* Entry : TopLevelChild->GetChildrenNodes())
			{
				TPair<EClassPathTime, FString> Pair = ParseXmlClassPathEntry(Entry);
				switch (Pair.Key)
				{
					case EClassPathTime::Boot:
						ClassPath += AbsolutePath(Pair.Value);
						ClassPath += FPlatformMisc::GetPathVarDelimiter();
						break;
					case EClassPathTime::BeginPlay:
						ClassPaths.Add(AbsolutePath(Pair.Value));
						break;
				}
			}
		}
	}
	UE_LOG(LogJun, Log, TEXT("Starting JunPlugin with JVM DLL %s and classpath %s"), *JvmDllPath, *ClassPath);

	InitJavaVm(*JvmDllPath, *ClassPath);
	InitClassLoaderClass();
	InitDelegates();
}

void FJunPlugin::InitJavaVm(const TCHAR* JvmDllPath, const TCHAR* ClassPath)
{
	UE_LOG(LogJun, Log, TEXT("Initializing Java VM..."));
	void* DllHandle = FPlatformProcess::GetDllHandle(JvmDllPath);
	using F_JNI_CreateJavaVM = decltype(JNI_CreateJavaVM)*;

	F_JNI_CreateJavaVM CreateJavaVM = static_cast<F_JNI_CreateJavaVM>(FPlatformProcess::GetDllExport(DllHandle, TEXT("JNI_CreateJavaVM")));

	auto ClassPathOption = StringCast<ANSICHAR>(*FString::Printf(TEXT("-Djava.class.path=%s"), ClassPath));
	TArray<JavaVMOption> Options;
	Options.Add({ const_cast<char*>(ClassPathOption.Get()), nullptr });
	//TODO: Only if debugging.
	//Options.Add({ "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6009", nullptr });
	Options.Add({ "--add-opens=java.base/java.lang=ALL-UNNAMED", nullptr });
	Options.Add({ "--add-opens=java.base/java.util=ALL-UNNAMED", nullptr });
	Options.Add({ "--enable-native-access=ALL-UNNAMED", nullptr });
	Options.Add({ "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0", nullptr });
	Options.Add({ "-Dforeign.restricted=permit", nullptr });
	Options.Add({ "-Dfile.encoding=UTF-8", nullptr });
	//Options.Add({ "-XX:+UseShenandoahGC", nullptr });

	Options.Add({ "-Xmx8G", nullptr });
	Options.Add({ "-Xms2G", nullptr });

	JavaVMInitArgs VmArgs;
	VmArgs.ignoreUnrecognized = false;
	VmArgs.options = &Options[0];
	VmArgs.nOptions = Options.Num();
	VmArgs.version = JNI_VERSION_10;

	jint Result = CreateJavaVM(&Jvm, reinterpret_cast<void**>(&Env), &VmArgs);
	if (Result != JNI_OK)
	{
		UE_LOG(LogJun, Fatal, TEXT("JNI_CreateJavaVM returned %d"), Result);
		return;
	}
	GlobalEnv = Env;
}

void FJunPlugin::InitClassLoaderClass()
{
	UE_LOG(LogJun, Log, TEXT("Initializing class ClassLoader..."));

	jclass StringClass = GEnv->FindClass("java/lang/String");
	jobjectArray LocalEmptyStringArray = GEnv->NewObjectArray(0, StringClass, nullptr);
	GEnv->DeleteLocalRef(StringClass);
	EmptyStringArray = static_cast<jobjectArray>(GEnv->NewGlobalRef(LocalEmptyStringArray));
	GEnv->DeleteLocalRef(LocalEmptyStringArray);

	PathsClass = LoadClass("java/nio/file/Paths");
	PathsGetMethodID = GEnv->GetStaticMethodID(PathsClass, "get", "(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;");
	if (PathsGetMethodID == 0)
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot find method Paths.get(String)"));
		return;
	}
	PathClass = LoadClass("java/nio/file/Path");
	PathToURIMethodID = GEnv->GetMethodID(PathClass, "toUri", "()Ljava/net/URI;");
	if (PathToURIMethodID  == 0)
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot find method Path.toURI()"));
		return;
	}
	URIClass = LoadClass("java/net/URI");
	URIToURLMethodID = GEnv->GetMethodID(URIClass, "toURL", "()Ljava/net/URL;");
	if (URIToURLMethodID == 0)
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot find method URI.toURL()"));
		return;
	}
	URLClass = LoadClass("java/net/URL");
	URLClassLoaderClass = LoadClass("java/net/URLClassLoader");
	URLClassLoaderConstructorID = GEnv->GetMethodID(URLClassLoaderClass, "<init>", "([Ljava/net/URL;)V");
	if (URLClassLoaderConstructorID == nullptr)
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot find constructor for URLClassLoader"));
		return;
	}
}

jclass FJunPlugin::LoadClass(const char* ClassName)
{
	jclass LocalRef = GEnv->FindClass(ClassName);
	if (LocalRef == nullptr)
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot find find class %s"), ANSI_TO_TCHAR(ClassName));
	}
	jclass GlobalRef = static_cast<jclass>(GEnv->NewGlobalRef(LocalRef));
	GEnv->DeleteLocalRef(LocalRef);
	return GlobalRef;
}

void FJunPlugin::InitDelegates()
{
	UE_LOG(LogJun, Log, TEXT("Initializing delegates..."));
	//TickDelegateHandle = FTicker::GetCoreTicker().AddTicker(FTickerDelegate::CreateLambda([this](float DeltaSeconds)
	TickDelegateHandle = FTSTicker::GetCoreTicker().AddTicker(FTickerDelegate::CreateLambda([this](float DeltaSeconds)
	{
		this->OnTick(DeltaSeconds);
		return true;
	}));

	OnPreWorldInitializationDelegateHandle = FWorldDelegates::OnPreWorldInitialization.AddLambda([this](UWorld* World, UWorld::InitializationValues InitializationValues)
	{
		this->OnPreWorldInitialization(World, InitializationValues);
	});
	OnPreWorldFinishDestroyDelegateHandle = FWorldDelegates::OnPreWorldFinishDestroy.AddLambda([this](UWorld* World)
	{
		this->OnPreWorldFinishDestroy(World);
	});
}

void FJunPlugin::ShutdownModule()
{
	if (IsRunningCommandlet())
	{
		UE_LOG(LogJun, Log, TEXT("Skipping shutdown of JunPlugin because run from commandlet."));
		return;
	}
	UE_LOG(LogJun, Warning, TEXT("Trying to shutdown Jun..."));
	FWorldDelegates::OnPreWorldFinishDestroy.Remove(OnPreWorldFinishDestroyDelegateHandle);
	FWorldDelegates::OnPreWorldInitialization.Remove(OnPreWorldInitializationDelegateHandle);
	FTSTicker::GetCoreTicker().RemoveTicker(TickDelegateHandle);
	if (EmptyStringArray != nullptr)
	{
		GEnv->DeleteGlobalRef(EmptyStringArray);
		EmptyStringArray = nullptr;
	}
	if (PathsClass != nullptr)
	{
		GEnv->DeleteGlobalRef(PathsClass);
		PathsClass = nullptr;
	}
	if (PathClass != nullptr)
	{
		GEnv->DeleteGlobalRef(PathClass);
		PathClass = nullptr;
	}
	if (URIClass != nullptr)
	{
		GEnv->DeleteGlobalRef(URIClass);
		URIClass = nullptr;
	}
	if (URLClass != nullptr)
	{
		GEnv->DeleteGlobalRef(URLClass);
		URLClass = nullptr;
	}
	if (URLClassLoaderClass != nullptr)
	{
		GEnv->DeleteGlobalRef(URLClassLoaderClass);
		URLClassLoaderClass = nullptr;
	}
	if (Jvm != nullptr)
	{
		//TODO: Find out why the shutdown process hangs.
		Jvm->DetachCurrentThread();
		// Jvm->DestroyJavaVM();
		Jvm = nullptr;
		Env = nullptr;
		GlobalEnv = nullptr;
	}
	UE_LOG(LogJun, Warning, TEXT("SHUTDOWN Jun"));
}

void FJunPlugin::OnTick(float DeltaSeconds)
{
	if (GJunManager != nullptr)
	{
		GJunManager->OnTick(DeltaSeconds);
	}
}

void FJunPlugin::OnPreWorldInitialization(UWorld* World, UWorld::InitializationValues)
{
	EWorldType::Type WorldType = World->WorldType;
	UE_LOG(
		LogJun,
		Log,
		TEXT("FJunPlugin::OnPreWorldInitialization World=0x%llx WorldType=%d\nWorld->IsGameWorld()=%d\nWorld->GetNetMode()=%d\nWorld->IsEditorWorld()=%d\nWorld->IsGameWorld()=%d"),
		reinterpret_cast<long long int>(World),
		static_cast<int>(WorldType),
		World->IsGameWorld(),
		World->GetNetMode(),
		World->IsEditorWorld(),
		World->IsGameWorld()
	);
	
	
	if (WorldType == EWorldType::Game || WorldType == EWorldType::PIE)
	{
		int32 PathNum = ClassPaths.Num();
		jobjectArray URLArray = GEnv->NewObjectArray(PathNum, URLClass, nullptr);
		for (int32 I = 0; I < PathNum; ++I)
		{
			jstring ClassPathString = U2J(ClassPaths[I]);
			jobject Path = GEnv->CallStaticObjectMethod(PathsClass, PathsGetMethodID, ClassPathString, EmptyStringArray);
			GEnv->DeleteLocalRef(ClassPathString);
			jobject URI = GEnv->CallObjectMethod(Path, PathToURIMethodID);
			GEnv->DeleteLocalRef(Path);
			jobject URL = GEnv->CallObjectMethod(URI, URIToURLMethodID);
			GEnv->DeleteLocalRef(URI);
			GEnv->SetObjectArrayElement(URLArray, I, URL);
			GEnv->DeleteLocalRef(URL);
		}
		jobject ClassLoaderInstance = GEnv->NewObject(URLClassLoaderClass, URLClassLoaderConstructorID, URLArray);
		if (ClassLoaderInstance == nullptr)
		{
			UE_LOG(LogJun, Fatal, TEXT("Cannot instantiate URLClassLoader"));
		}
		UE_LOG(LogJun, Log, TEXT("Creating ClassLoader..."));
		TSharedPtr<FJunClassLoader> ClassLoader = MakeShared<FJunClassLoader>(URLClassLoaderClass, ClassLoaderInstance);
		/*for (const FString& ClassPath : ClassPaths)
		{
			UE_LOG(LogJun, Log, TEXT("Adding to classpath: %s"), *ClassPath);
			ClassLoader->AddPath(*ClassPath);
		}*/
		UE_LOG(LogJun, Log, TEXT("Registering wrapper classes..."));
		GJunWrapperManager = new FJunWrapperManager(World, ClassLoader);
		UE_LOG(LogJun, Log, TEXT("Creating GJunManager..."));
		new FJunManager(World, ClassLoader, ApplicationFactoryClassName, ExtensionProviders);
	}
}

void FJunPlugin::OnPreWorldFinishDestroy(UWorld* World)
{
	if (GJunManager != nullptr && GJunManager->World == World)
	{
		delete GJunManager;
		GJunManager = nullptr;
		delete GJunWrapperManager;
		GJunWrapperManager = nullptr;
		UE_LOG(LogJun, Log, TEXT("FJunPlugin::OnPreWorldFinishDestroy"));
	}
}

JavaVM* FJunPlugin::GetJvm() const
{
	return Jvm;
}

JNIEnv* FJunPlugin::GetEnv() const
{
	return Env;
}

void FJunPlugin::AddExtensionProvider(FJunManagerExtensionProvider ExtensionProvider)
{
	ExtensionProviders.Add(ExtensionProvider);
}

void FJunPlugin::RemoveExtensionProvider(FJunManagerExtensionProvider ExtensionProvider)
{
	ExtensionProviders.Remove(ExtensionProvider);
}
