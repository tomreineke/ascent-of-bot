#pragma once

#include "CoreMinimal.h"
#include "Engine/StreamableManager.h"
#include "GameFramework/Actor.h"

#include "IJunPlugin.h"
#include "JunClassLoader.h"
#include "JunConv.h"
#include "JunDefaultActor.h"
#include "JunJNI.h"
#include "JunKeyMap.h"
#include "JunLifeGuard.h"
#include "JunSharedRef.h"
#include "JunStringUtil.h"
#include "JunWrapperManager.h"


class FJunManagerJNI;
class FJunAudioCallbacksJNI;
class FJunToolTipWidgetJNI;

class JUNPLUGIN_API FJunManager
{
public:
	FJunManager(UWorld* World, TSharedPtr<FJunClassLoader> ClassLoader, const FString& ApplicationFactoryClassName, TArray<FJunManagerExtensionProvider>& ExtensionProviders);
    
	~FJunManager();

	TSharedRef<FJunClassLoader>& GetClassLoader();

	void RegisterExtensionClass(const char* ClassName);

	void OnBeginPlay();

	void OnTick(float DeltaSeconds);

	void OnEndPlay(const EEndPlayReason::Type Reason);

	void OnKeyPressed(FKey Key);

	void OnKeyReleased(FKey Key);

	bool JunCheckException(bool bFatal = false, JNIEnv* Env = GEnv);

	void ThrowException(const TCHAR* Message, JNIEnv* Env = GEnv);

	void ThrowException(const char* Message, JNIEnv* Env = GEnv);
private:
	void RegisterNatives();
	void RegisterNatives_AActor();
	void RegisterNatives_AAmbientSound();
	void RegisterNatives_AAudioVolume();
	void RegisterNatives_ABrush();
	void RegisterNatives_ACameraActor();
	void RegisterNatives_ACharacter();
	void RegisterNatives_AController();
	void RegisterNatives_ADecalActor();
	void RegisterNatives_ADirectionalLight();
	void RegisterNatives_AGeneratedMeshAreaLight();
	void RegisterNatives_AHUD();
	void RegisterNatives_AJunPlayerController();
	void RegisterNatives_ALight();
	void RegisterNatives_ANiagaraActor();
	void RegisterNatives_APawn();
	void RegisterNatives_APlayerController();
	void RegisterNatives_APointLight();
	void RegisterNatives_APostProcessVolume();
	void RegisterNatives_ARectLight();
	void RegisterNatives_ASpotLight();
	void RegisterNatives_AStaticMeshActor();
	void RegisterNatives_AVolume();
	void RegisterNatives_FChildren();
	void RegisterNatives_FCommandLine();
	void RegisterNatives_FDeferredCleanupSlateBrush();
	void RegisterNatives_FFontOutlineSettings();
	void RegisterNatives_FGenericWindow();
	void RegisterNatives_FHitResult();
	void RegisterNatives_FMinimalViewInfo();
	void RegisterNatives_FPaths();
	void RegisterNatives_FPlatformProcess();
	void RegisterNatives_FSlateApplication();
	void RegisterNatives_FSlateBrush();
	void RegisterNatives_FSlateDynamicImageBrush();
	void RegisterNatives_FSlateFontInfo();
	void RegisterNatives_FSlateImageBrush();
	void RegisterNatives_FSlotBase();
	void RegisterNatives_FSoftObjectPtr();
	void RegisterNatives_FSoundAttenuationSettings();
	void RegisterNatives_FViewport();
	void RegisterNatives_HalfFloat();
	void RegisterNatives_IFileManager();
	void RegisterNatives_Int();
	void RegisterNatives_JunManager();
	void RegisterNatives_Key();
	void RegisterNatives_KeyManager();
	void RegisterNatives_log();
	void RegisterNatives_SBorder();
	void RegisterNatives_SBox();
	void RegisterNatives_SButton();
	void RegisterNatives_SCanvas();
	void RegisterNatives_SCompoundWidget();
	void RegisterNatives_SImage();
	void RegisterNatives_SJunToolTipWidget();
	void RegisterNatives_SLeafWidget();
	void RegisterNatives_SPanel();
	void RegisterNatives_STextBlock();
	void RegisterNatives_SWidget();
	void RegisterNatives_SWindow();
	void RegisterNatives_TSharedRef();
	void RegisterNatives_TSlotBase();
	void RegisterNatives_UActorComponent();
	void RegisterNatives_UArrowComponent();
	void RegisterNatives_UAudioComponent();
	void RegisterNatives_UBlueprint();
	void RegisterNatives_UBlueprintCore();
	void RegisterNatives_UBlueprintFunctionLibrary();
	void RegisterNatives_UBodySetup();
	void RegisterNatives_UCameraComponent();
	void RegisterNatives_UCanvas();
	void RegisterNatives_UCineCameraComponent();
	void RegisterNatives_UClass();
	void RegisterNatives_UDecalComponent();
	void RegisterNatives_UDestructibleMeshComponent();
	void RegisterNatives_UDirectionalLightComponent();
	void RegisterNatives_UEngine();
	void RegisterNatives_UFont();
	void RegisterNatives_UFXSystemAsset();
	void RegisterNatives_UFXSystemComponent();
	void RegisterNatives_UGameplayStatics();
	void RegisterNatives_UGameViewportClient();
	void RegisterNatives_UInitialActiveSoundParams();
	void RegisterNatives_UInstancedStaticMeshComponent();
	void RegisterNatives_ULightComponent();
	void RegisterNatives_ULightComponentBase();
	void RegisterNatives_ULocalLightComponent();
	void RegisterNatives_ULocalPlayer();
	void RegisterNatives_UMaterial();
	void RegisterNatives_UMaterialInstance();
	void RegisterNatives_UMaterialInstanceConstant();
	void RegisterNatives_UMaterialInstanceDynamic();
	void RegisterNatives_UMaterialInterface();
	void RegisterNatives_UMeshComponent();
	void RegisterNatives_UNiagaraComponent();
	void RegisterNatives_UNiagaraEmitter();
	void RegisterNatives_UNiagaraScript();
	void RegisterNatives_UNiagaraSystem();
	void RegisterNatives_UObject();
	void RegisterNatives_UParticleSystem();
	void RegisterNatives_UParticleSystemComponent();
	void RegisterNatives_UPlayer();
	void RegisterNatives_UPointLightComponent();
	void RegisterNatives_UPoseableMeshComponent();
	void RegisterNatives_UPrimitiveComponent();
	void RegisterNatives_URectLightComponent();
	void RegisterNatives_USceneComponent();
	void RegisterNatives_UScriptViewportClient();
	void RegisterNatives_USkeletalBodySetup();
	void RegisterNatives_USkeletalMesh();
	void RegisterNatives_USkeletalMeshComponent();
	void RegisterNatives_USkinnedMeshComponent();
	void RegisterNatives_USkyLightComponent();
	void RegisterNatives_USoundAttenuation();
	void RegisterNatives_USoundBase();
	void RegisterNatives_USoundConcurrency();
	void RegisterNatives_USoundCue();
	void RegisterNatives_USoundSimple();
	void RegisterNatives_USoundWave();
	void RegisterNatives_USplineComponent();
	void RegisterNatives_USplineMeshComponent();
	void RegisterNatives_USpotLightComponent();
	void RegisterNatives_UStaticMesh();
	void RegisterNatives_UStaticMeshComponent();
	void RegisterNatives_UTextRenderComponent();
	void RegisterNatives_UTexture();
	void RegisterNatives_UTexture2D();
	void RegisterNatives_UTexture2DDynamic();
	void RegisterNatives_UTextureLightProfile();
	void RegisterNatives_UUserWidget();
	void RegisterNatives_UVisual();
	void RegisterNatives_UWidget();
	void RegisterNatives_UWidgetComponent();
	void RegisterNatives_UWidgetLayoutLibrary();
	void RegisterNatives_UWorld();

	template<auto fn>
	void RegisterNative(const char* ClassName, char* MethodName, char* Signature)
	{
		RegisterNativeInternal(ClassName, MethodName, Signature, AugmentJNIParameters<fn>::augmented);
	}

	template<auto fn>
	void RegisterNativeWithEnv(const char* ClassName, char* MethodName, char* Signature)
	{
		RegisterNativeInternal(ClassName, MethodName, Signature, AugmentJNIParametersWithEnv<fn>::augmented);
	}

	void RegisterNativeInternal(const char* ClassName, char* MethodName, char* Signature, void* fn);

	static void ExecuteInMainThread(FJunLifeGuard* LifeGuard, jobject Runnable);
public:
	FJunLifeGuard* LifeGuard;
	TSharedRef<FJunClassLoader> ClassLoader;
private:
	FString ApplicationFactoryClassName;
	UWorld* World;
	AJunDefaultActor* DefaultActor;
	FGCObjectScopeGuard DefaultActorGuard;

	FStreamableManager StreamableManager;

	jclass RunnableClass;
	jmethodID RunnableRunID;

	TSharedPtr<FJunManagerJNI> JunManagerJNI;
	TSharedPtr<FJunAudioCallbacksJNI> JunAudioCallbacksJNI;
	TSharedPtr<FJunToolTipWidgetJNI> JunToolTipWidgetJNI;

	TArray<TUniquePtr<FJunManagerExtension>> Extensions;

	friend class FJunPlugin;
	friend class FJunManagerExtension;
	friend class FJunWrapperManager;
	friend class SJunToolTipWidget;
	friend class UJunAudioCallbacks;
	friend class UJunStatics;
	friend jobject JunSpawnActor(UWorld* World, UClass* ActorClass, jstring NameJ, const FTransform* Transform);
};

class FJunManagerJNI {
public:
	FJunManagerJNI(FJunManager& Manager);
	~FJunManagerJNI();

	void OnBeginPlay(const FString& ApplicationFactoryClassName, jobject ClassLoader);
	void OnTick(float DeltaSeconds);
	void OnEndPlay();
	void OnKeyPressed(int32 KeyIndex);
	void OnKeyReleased(int32 KeyIndex);
	void ThrowException(const TCHAR* Message, JNIEnv* Env = GEnv);
	void ThrowException(const char* Message, JNIEnv* Env = GEnv);
private:
	FJunManager& Manager;
	jclass JunManagerClass;
	jmethodID JunManagerBeginPlayID;
	jmethodID JunManagerTickID;
	jmethodID JunManagerEndPlayID;
	jmethodID JunManagerKeyPressedID;
	jmethodID JunManagerKeyReleasedID;
	jmethodID JunManagerThrowExceptionID;
	jclass JunExceptionClass;
	jmethodID JunExceptionConstructorID;
};

JUNPLUGIN_API extern FJunManager* GJunManager;
JUNPLUGIN_API extern FJunLifeGuard* GJunLifeGuard;
