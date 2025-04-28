#pragma once

#include "JunManager.h"
#include "JunManagerExtension.h"
#include "JunConv.h"
//#include "skiatree.h"

typedef void* FJunSkiaTreeFunctionTable[2];

class SJunSkiaTreeWidget;
class FJunSkiaTreeWidgetJNI;

class JUNPLUGINSKIATREE_API FJunManagerSkiaTree : public FJunManagerExtension
{
public:
	FJunManagerSkiaTree(FJunManager& Manager, FString SkiaTreePath);
	~FJunManagerSkiaTree();

	void RegisterNatives() override;
private:
	FString SkiaTreePath;
	//skiatree::SkiaTreeLibrary* Library;

	void RegisterNatives_FJunManagerSkiaTree();
	void RegisterNatives_SJunSkiaTreeWidget();

	//TSharedPtr<FJunCefSkiaTreeJNI> JunCefWidgetJNI;

	friend class SJunSkiaTreeWidget;
	//friend class FJunCefClient;
	//friend class FJunCefResourceRequestHandler;
};

JUNPLUGINSKIATREE_API extern FJunManagerSkiaTree* GJunManagerSkiaTree;