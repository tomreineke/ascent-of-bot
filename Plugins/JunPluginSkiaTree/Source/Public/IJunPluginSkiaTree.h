#pragma once

#include "CoreMinimal.h"
#include "Modules/ModuleInterface.h"
#include "Modules/ModuleManager.h"
#include "jni.h"

DECLARE_LOG_CATEGORY_EXTERN(LogJunSkiaTree, Log, All);

//extern JUNPLUGINCEF_API JNIEnv* GIoEnv;

//class FJunCefApp;

class IJunPluginSkiaTree : public IModuleInterface
{
public:
	static inline IJunPluginSkiaTree& Get()
	{
		return FModuleManager::LoadModuleChecked<IJunPluginSkiaTree>("JunPluginSkiaTree");
	}

	static inline bool IsAvailable()
	{
		return FModuleManager::Get().IsModuleLoaded("JunPluginSkiaTree");
	}
};
