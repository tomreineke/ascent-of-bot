#pragma once

#include "EngineMinimal.h"
#include "Engine/AssetManagerTypes.h"
#include "Engine/DataAsset.h"
#include "Engine/PrimaryAssetLabel.h"

#include "JunPrimaryAssetLabel.generated.h"

UCLASS()
class JUNPLUGIN_API UJunPrimaryAssetLabel : public UPrimaryDataAsset
{
	GENERATED_BODY()
public:
	UJunPrimaryAssetLabel();

	UPROPERTY(EditAnywhere, Category = Rules, meta = (ShowOnlyInnerProperties))
	FPrimaryAssetRules Rules;

	UPROPERTY(EditAnywhere, Category = PrimaryAssetLabel)
	TArray<FString> AssetLists;

	UPROPERTY(VisibleAnywhere, BlueprintReadOnly, Category = JunPrimaryAssetLabel, meta = (AssetBundles = "Explicit"))
	TArray<TSoftObjectPtr<UObject>> ReferencedAssets;

	UFUNCTION(Category="Helper Functions", CallInEditor)
	void UpdateAssetList();

#if WITH_EDITORONLY_DATA
	virtual void UpdateAssetBundleData() override;
#endif
};
