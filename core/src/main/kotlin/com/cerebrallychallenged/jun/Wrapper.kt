package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.ABrush
import com.cerebrallychallenged.jun.unreal.ACharacter
import com.cerebrallychallenged.jun.unreal.AController
import com.cerebrallychallenged.jun.unreal.AHUD
import com.cerebrallychallenged.jun.unreal.AJunPlayerController
import com.cerebrallychallenged.jun.unreal.APawn
import com.cerebrallychallenged.jun.unreal.APlayerController
import com.cerebrallychallenged.jun.unreal.AVolume
import com.cerebrallychallenged.jun.unreal.UActorComponent
import com.cerebrallychallenged.jun.unreal.UArrowComponent
import com.cerebrallychallenged.jun.unreal.UBlueprintFunctionLibrary
import com.cerebrallychallenged.jun.unreal.UBodySetup
import com.cerebrallychallenged.jun.unreal.UCanvas
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UEngine
import com.cerebrallychallenged.jun.unreal.UGameViewportClient
import com.cerebrallychallenged.jun.unreal.UGameplayStatics
import com.cerebrallychallenged.jun.unreal.ULocalPlayer
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UObjectCompanion
import com.cerebrallychallenged.jun.unreal.UParticleSystem
import com.cerebrallychallenged.jun.unreal.UParticleSystemComponent
import com.cerebrallychallenged.jun.unreal.UPlayer
import com.cerebrallychallenged.jun.unreal.UPrimitiveComponent
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.UScriptViewportClient
import com.cerebrallychallenged.jun.unreal.USkeletalBodySetup
import com.cerebrallychallenged.jun.unreal.UTextRenderComponent
import com.cerebrallychallenged.jun.unreal.UTexture
import com.cerebrallychallenged.jun.unreal.UTexture2D
import com.cerebrallychallenged.jun.unreal.UTexture2DDynamic
import com.cerebrallychallenged.jun.unreal.UTextureLightProfile
import com.cerebrallychallenged.jun.unreal.UVisual
import com.cerebrallychallenged.jun.unreal.UWidgetLayoutLibrary
import com.cerebrallychallenged.jun.unreal.UWorld
import com.cerebrallychallenged.jun.unreal.blueprint.UBlueprint
import com.cerebrallychallenged.jun.unreal.blueprint.UBlueprintCore
import com.cerebrallychallenged.jun.unreal.camera.ACameraActor
import com.cerebrallychallenged.jun.unreal.camera.ACineCameraActor
import com.cerebrallychallenged.jun.unreal.camera.UCameraComponent
import com.cerebrallychallenged.jun.unreal.camera.UCineCameraComponent
import com.cerebrallychallenged.jun.unreal.decal.ADecalActor
import com.cerebrallychallenged.jun.unreal.decal.UDecalComponent
import com.cerebrallychallenged.jun.unreal.font.UFont
import com.cerebrallychallenged.jun.unreal.getName
import com.cerebrallychallenged.jun.unreal.light.ADirectionalLight
import com.cerebrallychallenged.jun.unreal.light.AGeneratedMeshAreaLight
import com.cerebrallychallenged.jun.unreal.light.ALight
import com.cerebrallychallenged.jun.unreal.light.APointLight
import com.cerebrallychallenged.jun.unreal.light.ARectLight
import com.cerebrallychallenged.jun.unreal.light.ASpotLight
import com.cerebrallychallenged.jun.unreal.light.UDirectionalLightComponent
import com.cerebrallychallenged.jun.unreal.light.ULightComponent
import com.cerebrallychallenged.jun.unreal.light.ULightComponentBase
import com.cerebrallychallenged.jun.unreal.light.ULocalLightComponent
import com.cerebrallychallenged.jun.unreal.light.UPointLightComponent
import com.cerebrallychallenged.jun.unreal.light.URectLightComponent
import com.cerebrallychallenged.jun.unreal.light.USkyLightComponent
import com.cerebrallychallenged.jun.unreal.light.USpotLightComponent
import com.cerebrallychallenged.jun.unreal.material.UMaterial
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstance
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceConstant
import com.cerebrallychallenged.jun.unreal.material.UMaterialInstanceDynamic
import com.cerebrallychallenged.jun.unreal.material.UMaterialInterface
import com.cerebrallychallenged.jun.unreal.mesh.AStaticMeshActor
import com.cerebrallychallenged.jun.unreal.mesh.UDestructibleMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.UInstancedStaticMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.UMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.UPoseableMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.USkeletalMesh
import com.cerebrallychallenged.jun.unreal.mesh.USkeletalMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.USkinnedMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.USplineMeshComponent
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMesh
import com.cerebrallychallenged.jun.unreal.mesh.UStaticMeshComponent
import com.cerebrallychallenged.jun.unreal.niagara.ANiagaraActor
import com.cerebrallychallenged.jun.unreal.niagara.UFXSystemAsset
import com.cerebrallychallenged.jun.unreal.niagara.UFXSystemComponent
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraComponent
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraEmitter
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraScript
import com.cerebrallychallenged.jun.unreal.niagara.UNiagaraSystem
import com.cerebrallychallenged.jun.unreal.postprocess.APostProcessVolume
import com.cerebrallychallenged.jun.unreal.sound.AAmbientSound
import com.cerebrallychallenged.jun.unreal.sound.AAudioVolume
import com.cerebrallychallenged.jun.unreal.sound.UAudioComponent
import com.cerebrallychallenged.jun.unreal.sound.UInitialActiveSoundParams
import com.cerebrallychallenged.jun.unreal.sound.USoundAttenuation
import com.cerebrallychallenged.jun.unreal.sound.USoundBase
import com.cerebrallychallenged.jun.unreal.sound.USoundConcurrency
import com.cerebrallychallenged.jun.unreal.sound.USoundCue
import com.cerebrallychallenged.jun.unreal.sound.USoundSimple
import com.cerebrallychallenged.jun.unreal.sound.USoundWave
import com.cerebrallychallenged.jun.unreal.spline.USplineComponent
import com.cerebrallychallenged.jun.unreal.widget.UUserWidget
import com.cerebrallychallenged.jun.unreal.widget.UWidget
import com.cerebrallychallenged.jun.unreal.widget.UWidgetComponent
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.CPointer2ObjectOpenHashMap
import com.cerebrallychallenged.jun.util.isNull
import java.lang.ref.WeakReference
import kotlin.collections.set
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.full.companionObjectInstance

private fun WrapperIncubator.declareCoreWrappers() {
    wrapper(::AActor)
    wrapper(::AAmbientSound)
    wrapper(::AAudioVolume)
    wrapper(::ABrush)
    wrapper(::ACameraActor)
    wrapper(::ACharacter)
    wrapper(::ACineCameraActor)
    wrapper(::UClass)
    wrapper(::AController)
    wrapper(::ADecalActor)
    wrapper(::ADirectionalLight)
    wrapper(::AGeneratedMeshAreaLight)
    wrapper(::AHUD)
    wrapper(::AJunPlayerController)
    wrapper(::ALight)
    wrapper(::ANiagaraActor)
    wrapper(::APawn)
    wrapper(::APlayerController)
    wrapper(::APointLight)
    wrapper(::APostProcessVolume)
    wrapper(::ARectLight)
    wrapper(::ASpotLight)
    wrapper(::AStaticMeshActor)
    wrapper(::AVolume)
    wrapper(::UActorComponent)
    wrapper(::UArrowComponent)
    wrapper(::UAudioComponent)
    wrapper(::UBlueprint)
    wrapper(::UBlueprintCore)
    wrapper(::UBlueprintFunctionLibrary)
    wrapper(::UBodySetup)
    wrapper(::UCameraComponent)
    wrapper(::UCanvas)
    wrapper(::UCineCameraComponent)
    wrapper(::UDecalComponent)
    wrapper(::UDestructibleMeshComponent)
    wrapper(::UDirectionalLightComponent)
    wrapper(::UEngine)
    wrapper(::UFont)
    wrapper(::UFXSystemAsset)
    wrapper(::UFXSystemComponent)
    wrapper(::UGameplayStatics)
    wrapper(::UGameViewportClient)
    wrapper(::UInitialActiveSoundParams)
    wrapper(::UInstancedStaticMeshComponent)
    wrapper(::ULightComponent)
    wrapper(::ULightComponentBase)
    wrapper(::ULocalLightComponent)
    wrapper(::ULocalPlayer)
    wrapper(::UMaterial)
    wrapper(::UMaterialInstance)
    wrapper(::UMaterialInstanceConstant)
    wrapper(::UMaterialInstanceDynamic)
    wrapper(::UMaterialInterface)
    wrapper(::UMeshComponent)
    wrapper(::UNiagaraComponent)
    wrapper(::UNiagaraEmitter)
    wrapper(::UNiagaraScript)
    wrapper(::UNiagaraSystem)
    wrapper(::UObject)
    wrapper(::UParticleSystem)
    wrapper(::UParticleSystemComponent)
    wrapper(::UPointLightComponent)
    wrapper(::UPlayer)
    wrapper(::UPoseableMeshComponent)
    wrapper(::UPrimitiveComponent)
    wrapper(::URectLightComponent)
    wrapper(::USceneComponent)
    wrapper(::UScriptViewportClient)
    wrapper(::USkeletalBodySetup)
    wrapper(::USkeletalMesh)
    wrapper(::USkeletalMeshComponent)
    wrapper(::USkinnedMeshComponent)
    wrapper(::USkyLightComponent)
    wrapper(::USoundAttenuation)
    wrapper(::USoundBase)
    wrapper(::USoundConcurrency)
    wrapper(::USoundCue)
    wrapper(::USoundSimple)
    wrapper(::USoundWave)
    wrapper(::USplineComponent)
    wrapper(::USplineMeshComponent)
    wrapper(::USpotLightComponent)
    wrapper(::UStaticMesh)
    wrapper(::UStaticMeshComponent)
    wrapper(::UTextRenderComponent)
    wrapper(::UTexture)
    wrapper(::UTexture2D)
    wrapper(::UTexture2DDynamic)
    wrapper(::UTextureLightProfile)
    wrapper(::UUserWidget)
    wrapper(::UVisual)
    wrapper(::UWidget)
    wrapper(::UWidgetComponent)
    wrapper(::UWidgetLayoutLibrary)
    wrapper(::UWorld)
}

class WrapperIncubator internal constructor(extensions: List<JunManagerExtension>) {
    private data class Wrapper(val factory: (CPointer) -> UObject, val staticClass: KMutableProperty0<UClass>)

    private val wrapperByName = mutableMapOf<String, Wrapper>()

    inline fun <reified T : UObject> wrapper(noinline factory: (CPointer) -> T) = wrapper(factory, T::class)

    @PublishedApi
    internal fun <T : UObject> wrapper(factory: (CPointer) -> UObject, clazz: KClass<T>) {
        val simpleName = clazz.simpleName ?: throw JunException("Class $clazz has no simple name")
        val wrapperName = simpleName.substring(1)
        wrapperByName[wrapperName] = Wrapper(factory, (clazz.companionObjectInstance as UObjectCompanion)::staticClass)
    }

    init {
        declareCoreWrappers()
        for (extension in extensions) {
            with(extension) {
                declareWrappers()
            }
        }
    }

    private class PreClass(val factory: ((CPointer) -> UObject)?, val parentPtr: CPointer)

    private val preClasses = CPointer2ObjectOpenHashMap<PreClass>()

    internal val unrealClasses = mutableListOf<UClass>()

    internal val unrealObjects = CPointer2ObjectOpenHashMap<WeakReference<UObject>>()

    internal val unrealWrapperFactories = CPointer2ObjectOpenHashMap<(CPointer) -> UObject>()

    /**
     * Called from JNI during [registerClasses].
     */
    @Suppress("unused")
    private fun registerClass(classPtr: CPointer, name: String, parentPtr: CPointer) {
        val factory = wrapperByName[name]?.let { (factory, staticClass) ->
            val unrealClass = UClass(classPtr)
            unrealClasses.add(unrealClass)
            unrealObjects[classPtr] = WeakReference(unrealClass)
            staticClass.set(unrealClass)
            factory
        }
        preClasses[classPtr] = PreClass(factory, parentPtr)
    }

    private fun resolveWrapper(classPtr: CPointer): (CPointer) -> UObject {
        unrealWrapperFactories[classPtr]?.let { return it }
        val preClass
                = preClasses[classPtr]
                ?: throw JunException("Missing PreClass entry for ${getName(classPtr)}")
        preClass.factory?.let {
            unrealWrapperFactories[classPtr] = it
            return it
        }
        val parentPtr = preClass.parentPtr
        if (parentPtr.isNull()) throw JunException("UClass ${getName(classPtr)} has no parent")
        return resolveWrapper(parentPtr).also { unrealWrapperFactories[classPtr] = it }
    }

    internal fun resolve() {
        preClasses.forEachKey { classPtr ->
            resolveWrapper(classPtr)
        }
    }
}
