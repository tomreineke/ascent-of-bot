#include "IJunPluginRMC.h"
#include "IJunPlugin.h"
#include "JunManager.h"
#include "JunManagerRMC.h"
#include "Modules/ModuleInterface.h"

DEFINE_LOG_CATEGORY(LogJunRMC);

class FJunPluginRMC : public IJunPluginRMC
{
	void StartupModule() override;

	void ShutdownModule() override;

	static TUniquePtr<FJunManagerExtension> CreateExtension(FJunManager& Manager);
};

IMPLEMENT_MODULE(FJunPluginRMC, JunPluginRMC)

void FJunPluginRMC::StartupModule()
{
	if (IsRunningCommandlet())
	{
		UE_LOG(LogJunRMC, Log, TEXT("Skipping startup of JunPluginRMC because run from commandlet."));
		return;
	}
	IJunPlugin::Get().AddExtensionProvider(FJunPluginRMC::CreateExtension);
}

void FJunPluginRMC::ShutdownModule()
{
	if (IsRunningCommandlet())
	{
		UE_LOG(LogJunRMC, Log, TEXT("Skipping shutdown of JunPluginRMC because run from commandlet."));
		return;
	}
	IJunPlugin::Get().RemoveExtensionProvider(FJunPluginRMC::CreateExtension);
}

TUniquePtr<FJunManagerExtension> FJunPluginRMC::CreateExtension(FJunManager& Manager)
{
	return MakeUnique<FJunManagerRMC>(Manager);
}