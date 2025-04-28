#include "JunManager.h"

#include "Blueprint/UserWidget.h"
#include "Blueprint/WidgetLayoutLibrary.h"
#include "Brushes/SlateImageBrush.h"
#include "CineCameraComponent.h"
#include "Components/Visual.h"
#include "Components/Widget.h"
#include "Engine/SkeletalMesh.h"
#include "Engine/StaticMesh.h"
#include "Engine/Texture.h"
#include "Engine/Texture2D.h"
#include "Engine/Texture2DDynamic.h"
#include "Engine/UserInterfaceSettings.h"
#include "Engine/World.h"
#include "GameFramework/Controller.h"
#include "GameFramework/PlayerController.h"
#include "HAL/FileManager.h"
#include "HAL/ThreadManager.h"
#include "Kismet/GameplayStatics.h"
#include "Materials/MaterialInstance.h"
#include "Materials/MaterialInstanceConstant.h"
#include "Materials/MaterialInstanceDynamic.h"
#include "Misc/Paths.h"
#include "Particles/ParticleSystem.h"
#include "UObject/GCObjectScopeGuard.h"



#include "JunAudioCallbacks.h"
#include "JunToolTipWidget.h"


JUNPLUGIN_API FJunManager* GJunManager = nullptr;
JUNPLUGIN_API FJunLifeGuard* GJunLifeGuard = nullptr;

FJunManager::FJunManager(UWorld* World, TSharedPtr<FJunClassLoader> ClassLoader, const FString& ApplicationFactoryClassName, TArray<FJunManagerExtensionProvider>& ExtensionProviders)
	: LifeGuard(new FJunLifeGuard(this))
	, ClassLoader(ClassLoader.ToSharedRef())
	, ApplicationFactoryClassName(ApplicationFactoryClassName)
	, World(World)
	, DefaultActor(World->SpawnActor<AJunDefaultActor>())
	, DefaultActorGuard(DefaultActor)
{
	GJunManager = this;
	GJunLifeGuard = LifeGuard;

	for (auto& Provider : ExtensionProviders)
	{
		Extensions.Emplace(Provider(*this));
	}

	UE_LOG(LogJun, Log, TEXT("Creating default actor..."));
	DefaultActor->Init(LifeGuard, GEnv, ClassLoader);

	RegisterNatives();
	for (auto& Extension : Extensions)
	{
		Extension->RegisterNatives();
	}

	JunManagerJNI = MakeShared<FJunManagerJNI>(*this);
	JunAudioCallbacksJNI = MakeShared<FJunAudioCallbacksJNI>(*this);
	JunToolTipWidgetJNI = MakeShared<FJunToolTipWidgetJNI>(*this);

	UE_LOG(LogJun, Log, TEXT("Initializing JunManager..."));
	JunManagerJNI->OnTick(0.0f);

	UE_LOG(LogJun, Log, TEXT("Loading java.lang.Runnable..."));
	RunnableClass = ClassLoader->LoadClassGlobalRef("java.lang.Runnable");
	RunnableRunID = GEnv->GetMethodID(RunnableClass, "run", "()V");

	JunCheckException(true);
}

FJunManager::~FJunManager()
{
	LifeGuard->Close();
	GEnv->DeleteGlobalRef(RunnableClass);
}

TSharedRef<FJunClassLoader>& FJunManager::GetClassLoader()
{
	return ClassLoader;
}

void FJunManager::RegisterExtensionClass(const char* ClassName)
{
	jclass ManagerKtClass = ClassLoader->LoadClassLocalRef("com.cerebrallychallenged.jun.JunManagerKt");
	if (JunCheckException()) return;
	jmethodID RegisterExtensionClassID = GEnv->GetStaticMethodID(ManagerKtClass, "registerExtensionClass", "(Ljava/lang/Class;)V");
	if (JunCheckException()) return;
	jclass ExtensionClass = ClassLoader->LoadClassLocalRef(ClassName);
	if (JunCheckException()) return;
	GEnv->CallStaticVoidMethod(ManagerKtClass, RegisterExtensionClassID, ExtensionClass);
	GEnv->DeleteLocalRef(ExtensionClass);
	GEnv->DeleteLocalRef(ManagerKtClass);
	JunCheckException();
}

void FJunManager::RegisterNatives()
{
	UE_LOG(LogJun, Log, TEXT("Registering native functions..."));
	RegisterNatives_AActor();
	RegisterNatives_AAmbientSound();
	RegisterNatives_AAudioVolume();
	RegisterNatives_ABrush();
	RegisterNatives_ACameraActor();
	RegisterNatives_ACharacter();
	RegisterNatives_AController();
	RegisterNatives_ADecalActor();
	RegisterNatives_ADirectionalLight();
	RegisterNatives_AGeneratedMeshAreaLight();
	RegisterNatives_AHUD();
	RegisterNatives_AJunPlayerController();
	RegisterNatives_ALight();
	RegisterNatives_ANiagaraActor();
	RegisterNatives_APawn();
	RegisterNatives_APlayerController();
	RegisterNatives_APointLight();
	RegisterNatives_APostProcessVolume();
	RegisterNatives_ARectLight();
	RegisterNatives_ASpotLight();
	RegisterNatives_AStaticMeshActor();
	RegisterNatives_AVolume();
	RegisterNatives_FChildren();
	RegisterNatives_FCommandLine();
	RegisterNatives_FDeferredCleanupSlateBrush();
	RegisterNatives_FFontOutlineSettings();
	RegisterNatives_FGenericWindow();
	RegisterNatives_FHitResult();
	RegisterNatives_FMinimalViewInfo();
	RegisterNatives_FPaths();
	RegisterNatives_FPlatformProcess();
	RegisterNatives_FSlateApplication();
	RegisterNatives_FSlateBrush();
	RegisterNatives_FSlateDynamicImageBrush();
	RegisterNatives_FSlateFontInfo();
	RegisterNatives_FSlateImageBrush();
	RegisterNatives_FSlotBase();
	RegisterNatives_FSoftObjectPtr();
	RegisterNatives_FSoundAttenuationSettings();
	RegisterNatives_FViewport();
	RegisterNatives_HalfFloat();
	RegisterNatives_IFileManager();
	RegisterNatives_Int();
	RegisterNatives_JunManager();
	RegisterNatives_Key();
	RegisterNatives_KeyManager();
	RegisterNatives_log();
	RegisterNatives_SBorder();
	RegisterNatives_SBox();
	RegisterNatives_SButton();
	RegisterNatives_SCanvas();
	RegisterNatives_SCompoundWidget();
	RegisterNatives_SImage();
	RegisterNatives_SJunToolTipWidget();
	RegisterNatives_SLeafWidget();
	RegisterNatives_SPanel();
	RegisterNatives_STextBlock();
	RegisterNatives_SWidget();
	RegisterNatives_SWindow();
	RegisterNatives_TSharedRef();
	RegisterNatives_TSlotBase();
	RegisterNatives_UActorComponent();
	RegisterNatives_UArrowComponent();
	RegisterNatives_UAudioComponent();
	RegisterNatives_UBlueprint();
	RegisterNatives_UBlueprintCore();
	RegisterNatives_UBlueprintFunctionLibrary();
	RegisterNatives_UBodySetup();
	RegisterNatives_UCameraComponent();
	RegisterNatives_UCanvas();
	RegisterNatives_UCineCameraComponent();
	RegisterNatives_UClass();
	RegisterNatives_UDecalComponent();
	RegisterNatives_UDestructibleMeshComponent();
	RegisterNatives_UDirectionalLightComponent();
	RegisterNatives_UEngine();
	RegisterNatives_UFont();
	RegisterNatives_UFXSystemAsset();
	RegisterNatives_UFXSystemComponent();
	RegisterNatives_UGameplayStatics();
	RegisterNatives_UGameViewportClient();
	RegisterNatives_UInitialActiveSoundParams();
	RegisterNatives_UInstancedStaticMeshComponent();
	RegisterNatives_ULightComponent();
	RegisterNatives_ULightComponentBase();
	RegisterNatives_ULocalLightComponent();
	RegisterNatives_ULocalPlayer();
	RegisterNatives_UMaterial();
	RegisterNatives_UMaterialInstance();
	RegisterNatives_UMaterialInstanceConstant();
	RegisterNatives_UMaterialInstanceDynamic();
	RegisterNatives_UMaterialInterface();
	RegisterNatives_UMeshComponent();
	RegisterNatives_UNiagaraComponent();
	RegisterNatives_UNiagaraEmitter();
	RegisterNatives_UNiagaraScript();
	RegisterNatives_UNiagaraSystem();
	RegisterNatives_UObject();
	RegisterNatives_UParticleSystem();
	RegisterNatives_UParticleSystemComponent();
	RegisterNatives_UPlayer();
	RegisterNatives_UPointLightComponent();
	RegisterNatives_UPoseableMeshComponent();
	RegisterNatives_UPrimitiveComponent();
	RegisterNatives_URectLightComponent();
	RegisterNatives_USceneComponent();
	RegisterNatives_UScriptViewportClient();
	RegisterNatives_USkeletalBodySetup();
	RegisterNatives_USkeletalMesh();
	RegisterNatives_USkeletalMeshComponent();
	RegisterNatives_USkinnedMeshComponent();
	RegisterNatives_USkyLightComponent();
	RegisterNatives_USoundAttenuation();
	RegisterNatives_USoundBase();
	RegisterNatives_USoundConcurrency();
	RegisterNatives_USoundCue();
	RegisterNatives_USoundSimple();
	RegisterNatives_USoundWave();
	RegisterNatives_USplineComponent();
	RegisterNatives_USplineMeshComponent();
	RegisterNatives_USpotLightComponent();
	RegisterNatives_UStaticMesh();
	RegisterNatives_UStaticMeshComponent();
	RegisterNatives_UTextRenderComponent();
	RegisterNatives_UTexture();
	RegisterNatives_UTexture2D();
	RegisterNatives_UTexture2DDynamic();
	RegisterNatives_UTextureLightProfile();
	RegisterNatives_UUserWidget();
	RegisterNatives_UVisual();
	RegisterNatives_UWidget();
	RegisterNatives_UWidgetComponent();
	RegisterNatives_UWidgetLayoutLibrary();
	RegisterNatives_UWorld();
}

void FJunManager::RegisterNativeInternal(const char* ClassName, char* MethodName, char* Signature, void* fn)
{
	jclass Class = ClassLoader->LoadClassNullableLocalRef(ClassName);
	if (Class == nullptr)
	{
		UE_LOG(LogJun, Fatal, TEXT("Cannot find class %s"), UTF8_TO_TCHAR(ClassName));
	}
	JNINativeMethod NativeMethod;
	NativeMethod.name = MethodName;
	NativeMethod.signature = Signature;
	NativeMethod.fnPtr = fn;
	jint Result = GEnv->RegisterNatives(Class, &NativeMethod, 1);
	if (Result != JNI_OK)
	{
		UE_LOG(LogJun, Fatal, TEXT("Registering Class: %s Method: %s failed"), UTF8_TO_TCHAR(ClassName), UTF8_TO_TCHAR(MethodName));
	}
	GEnv->DeleteLocalRef(Class);
}

void FJunManager::OnBeginPlay()
{
	void* Ptr = JunManagerJNI.Get();
	UE_LOG(LogJun, Log, TEXT("JunManagerJNI is %lld"), Ptr);
	if (!JunManagerJNI.IsValid())
	{
		UE_LOG(LogJun, Fatal, TEXT("Invalid JunManagerJNI on BeginPlay2"));
	}
	JunManagerJNI->OnBeginPlay(ApplicationFactoryClassName, ClassLoader->GetClassLoader());
}


void FJunManager::OnTick(float DeltaSeconds)
{
	JunManagerJNI->OnTick(DeltaSeconds);
}

void FJunManager::OnEndPlay(const EEndPlayReason::Type Reason)
{
	UE_LOG(LogJun, Log, TEXT("FJunManager::EndPlay"));
	JunManagerJNI->OnEndPlay();
	JunCheckException();
}

void FJunManager::OnKeyPressed(FKey Key)
{
	int32 KeyIndex = FJunKeyMap::Get().IndexOf(Key);
	JunManagerJNI->OnKeyPressed(KeyIndex);
}

void FJunManager::OnKeyReleased(FKey Key)
{
	int32 KeyIndex = FJunKeyMap::Get().IndexOf(Key);
	JunManagerJNI->OnKeyReleased(KeyIndex);
}

bool FJunManager::JunCheckException(bool bFatal, JNIEnv* Env)
{
	jthrowable Ex = Env->ExceptionOccurred();
	bool bOccurred = Ex != nullptr;
	if (bOccurred)
	{
		Env->ExceptionClear();
		FString ExDesc = JunExtractException(Env, Ex);
		UE_LOG(LogJun, Error, TEXT("Exception occurred: %s"), *ExDesc);
		if (bFatal)
		{
			UE_LOG(LogJun, Fatal, TEXT("Cannot recover from Exception."));
		}
	}
	return bOccurred;
}

void FJunManager::ThrowException(const TCHAR* Message, JNIEnv* Env)
{
	JunManagerJNI->ThrowException(Message, Env);
}

void FJunManager::ThrowException(const char* Message, JNIEnv* Env)
{
	JunManagerJNI->ThrowException(Message, Env);
}

void FJunManager::ExecuteInMainThread(FJunLifeGuard* LifeGuard, jobject Runnable)
{
	AsyncTask(
		ENamedThreads::GameThread,
		[LifeGuard, Runnable]()
		{
			LifeGuard->ExecuteIfOpen([Runnable](FJunManager* Manager)
			{
				GEnv->CallVoidMethod(Runnable, Manager->RunnableRunID);
				Manager->JunCheckException();
				GEnv->DeleteGlobalRef(Runnable);
			});
		}
	);
}

FJunManagerJNI::FJunManagerJNI(FJunManager& Manager) : Manager(Manager)
{
	JunManagerClass = Manager.GetClassLoader()->LoadClassGlobalRef("com.cerebrallychallenged.jun.JunManager");
	Manager.JunCheckException(true);
	JunManagerBeginPlayID = GEnv->GetStaticMethodID(JunManagerClass, "onBeginPlay", "(Ljava/lang/String;Ljava/net/URLClassLoader;)V");
	Manager.JunCheckException(true);
	JunManagerTickID = GEnv->GetStaticMethodID(JunManagerClass, "onTick", "(F)V");
	JunManagerEndPlayID = GEnv->GetStaticMethodID(JunManagerClass, "onEndPlay", "()V");
	JunManagerKeyPressedID = GEnv->GetStaticMethodID(JunManagerClass, "onKeyPressed", "(I)V");
	JunManagerKeyReleasedID = GEnv->GetStaticMethodID(JunManagerClass, "onKeyReleased", "(I)V");
	JunExceptionClass = Manager.GetClassLoader()->LoadClassGlobalRef("com.cerebrallychallenged.jun.JunException");
	JunExceptionConstructorID = GEnv->GetMethodID(JunExceptionClass, "<init>", "(Ljava/lang/String;)V");
	Manager.JunCheckException();
}

FJunManagerJNI::~FJunManagerJNI()
{
	GEnv->DeleteGlobalRef(JunManagerClass);
	GEnv->DeleteGlobalRef(JunExceptionClass);
}

void FJunManagerJNI::OnBeginPlay(const FString& ApplicationFactoryClassName, jobject ClassLoader)
{
	GEnv->CallStaticVoidMethod(JunManagerClass, JunManagerBeginPlayID, U2J(ApplicationFactoryClassName), ClassLoader);
	Manager.JunCheckException();
}

void FJunManagerJNI::OnTick(float DeltaSeconds)
{
	GEnv->CallStaticVoidMethod(JunManagerClass, JunManagerTickID, DeltaSeconds);
	Manager.JunCheckException();
}

void FJunManagerJNI::OnEndPlay()
{
	GEnv->CallStaticVoidMethod(JunManagerClass, JunManagerEndPlayID);
	Manager.JunCheckException();
}

void FJunManagerJNI::OnKeyPressed(int32 KeyIndex)
{
	GEnv->CallStaticVoidMethod(JunManagerClass, JunManagerKeyPressedID, KeyIndex);
	Manager.JunCheckException();
}

void FJunManagerJNI::OnKeyReleased(int32 KeyIndex)
{
	GEnv->CallStaticVoidMethod(JunManagerClass, JunManagerKeyReleasedID, KeyIndex);
	Manager.JunCheckException();
}

void FJunManagerJNI::ThrowException(const TCHAR* Message, JNIEnv* Env)
{
	Env->Throw(static_cast<jthrowable>(Env->NewObject(JunExceptionClass, JunExceptionConstructorID, U2J(Message, Env))));
}

void FJunManagerJNI::ThrowException(const char* Message, JNIEnv* Env)
{
	Env->Throw(static_cast<jthrowable>(Env->NewObject(JunExceptionClass, JunExceptionConstructorID, U2J(Message, Env))));
}