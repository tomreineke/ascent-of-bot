#pragma once

#include "CoreMinimal.h"
#include "Modules/ModuleInterface.h"
#include "Modules/ModuleManager.h"
#include "jni.h"
#include "JunManagerExtension.h"

DECLARE_LOG_CATEGORY_EXTERN(LogJun, Log, All);

class FJunManagerExtension;

class IJunPlugin : public IModuleInterface
{
public:
	static inline IJunPlugin& Get()
	{
		return FModuleManager::LoadModuleChecked<IJunPlugin>("JunPlugin");
	}

	static inline bool IsAvailable()
	{
		return FModuleManager::Get().IsModuleLoaded("JunPlugin");
	}

	virtual JavaVM* GetJvm() const = 0;

	virtual JNIEnv* GetEnv() const = 0;

	virtual void AddExtensionProvider(FJunManagerExtensionProvider ExtensionProvider) = 0;

	virtual void RemoveExtensionProvider(FJunManagerExtensionProvider ExtensionProvider) = 0;
};
