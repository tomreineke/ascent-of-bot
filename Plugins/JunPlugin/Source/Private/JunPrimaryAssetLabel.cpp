#include "JunPrimaryAssetLabel.h"
#include "Engine/AssetManager.h"
#include "Misc/FileHelper.h"
#include "Misc/Crc.h"

UJunPrimaryAssetLabel::UJunPrimaryAssetLabel()
{
	Rules.bApplyRecursively = true;
	Rules.Priority = 0;
	Rules.ChunkId = 0;
	Rules.CookRule = EPrimaryAssetCookRule::AlwaysCook;
}

void UJunPrimaryAssetLabel::UpdateAssetList()
{
	if (!UAssetManager::IsValid())
	{
		return;
	}

	UAssetManager& Manager = UAssetManager::Get();
	IAssetRegistry& AssetRegistry = Manager.GetAssetRegistry();

	ReferencedAssets.Empty();

	for (FString& RelPath : AssetLists)
	{
		FString AbsPath = IFileManager::Get().ConvertToAbsolutePathForExternalAppForRead(*(FPaths::ProjectDir() / RelPath));
		TArray<FString> Lines;
		bool bSuccess = FFileHelper::LoadFileToStringArray(Lines, *AbsPath);
		if (bSuccess)
		{
			for (FString& Line : Lines)
			{
				Line.TrimEndInline();
				if (!Line.IsEmpty())
				{
					FAssetData FoundAsset = AssetRegistry.GetAssetByObjectPath(FName(Line));
					FSoftObjectPath AssetRef = Manager.GetAssetPathForData(FoundAsset);
					if (!AssetRef.IsNull())
					{
						ReferencedAssets.Add(TSoftObjectPtr<UObject>(AssetRef));
					}
					else
					{
						UE_LOG(LogJun, Warning, TEXT("JunPrimaryAssetLabel: Could not find asset %s"), *Line);
					}
				}
			}
		}
		else
		{
			UE_LOG(LogJun, Warning, TEXT("JunPrimaryAssetLabel: Could not read %s"), *AbsPath);
		}
	}

	UPackage* Package = GetOutermost();
	if (Package != nullptr)
	{
		Package->SetDirtyFlag(true);
	}
}


#if WITH_EDITORONLY_DATA
void UJunPrimaryAssetLabel::UpdateAssetBundleData()
{
	if (!UAssetManager::IsValid())
	{
		return;
	}
	UAssetManager::Get().SetPrimaryAssetRules(GetPrimaryAssetId(), Rules);
	Super::UpdateAssetBundleData();
}
#endif
