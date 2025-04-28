#include "IJunPluginSkiaTree.h"
#include "IJunPlugin.h"
#include "JunManager.h"
#include "JunManagerSkiaTree.h"
#include "Modules/ModuleInterface.h"

#include "jni.h"
#include "skiatree.hpp"

DEFINE_LOG_CATEGORY(LogJunSkiaTree);

class FJunPluginSkiaTree : public IJunPluginSkiaTree
{
public:
	void StartupModule() override;

	void ShutdownModule() override;

	static TUniquePtr<FJunManagerExtension> CreateExtension(FJunManager& Manager);
private:
	static FString SkiaTreePath;
};

IMPLEMENT_MODULE(FJunPluginSkiaTree, JunPluginSkiaTree)

FString FJunPluginSkiaTree::SkiaTreePath;

void SkiaTreeLog(const char* Message)
{
	UE_LOG(LogJunSkiaTree, Log, TEXT("%s"), UTF8_TO_TCHAR(Message));
}

void FJunPluginSkiaTree::StartupModule()
{
	if (IsRunningCommandlet())
	{
		UE_LOG(LogJunSkiaTree, Log, TEXT("Skipping startup of JunPluginSkiaTree because run from commandlet."));
		return;
	}
	IJunPlugin& JunPlugin = IJunPlugin::Get();
	JunPlugin.AddExtensionProvider(FJunPluginSkiaTree::CreateExtension);

#if IS_MONOLITHIC
	SkiaTreePath = FPaths::ConvertRelativePathToFull(FPaths::Combine(
		FPaths::ProjectDir(),
		TEXT("Binaries/Win64")
	));
#else
	SkiaTreePath = FPaths::ConvertRelativePathToFull(FPaths::Combine(
		FModuleManager::Get().GetModuleFilename("JunPluginSkiaTree"),
		TEXT("../../../skia-tree/target/debug")
	));
#endif // IS_MONOLITHIC
	FPlatformProcess::GetDllHandle(*FPaths::Combine(SkiaTreePath, TEXT("skiatree.dll")));

	skiatree::skiatree_set_log_fn(SkiaTreeLog);
}

void FJunPluginSkiaTree::ShutdownModule()
{
	if (IsRunningCommandlet())
	{
		UE_LOG(LogJunSkiaTree, Log, TEXT("Skipping shutdown of JunPluginSkiaTree because run from commandlet."));
		return;
	}
	IJunPlugin::Get().RemoveExtensionProvider(FJunPluginSkiaTree::CreateExtension);
}

TUniquePtr<FJunManagerExtension> FJunPluginSkiaTree::CreateExtension(FJunManager& Manager)
{
	return MakeUnique<FJunManagerSkiaTree>(Manager, SkiaTreePath);
}
