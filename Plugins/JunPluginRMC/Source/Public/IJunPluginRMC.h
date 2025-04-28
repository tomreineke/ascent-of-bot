#pragma once

#include "CoreMinimal.h"
#include "Modules/ModuleInterface.h"
#include "Modules/ModuleManager.h"

DECLARE_LOG_CATEGORY_EXTERN(LogJunRMC, Log, All);

class IJunPluginRMC : public IModuleInterface
{
	static inline IJunPluginRMC& Get()
	{
		return FModuleManager::LoadModuleChecked<IJunPluginRMC>("JunPluginRMC");
	}

	static inline bool IsAvailable()
	{
		return FModuleManager::Get().IsModuleLoaded("JunPluginRMC");
	}
};
