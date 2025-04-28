#include "JunManagerSkiaTree.h"

FJunManagerSkiaTree::FJunManagerSkiaTree(FJunManager& Manager, FString SkiaTreePath) : FJunManagerExtension(Manager), SkiaTreePath(SkiaTreePath)
{
	Manager.RegisterExtensionClass("com.cerebrallychallenged.jun.skiatree.SkiaTreeExtension");
	GJunManagerSkiaTree = this;
}

FJunManagerSkiaTree::~FJunManagerSkiaTree()
{
	GJunManagerSkiaTree = nullptr;
}

void FJunManagerSkiaTree::RegisterNatives()
{
	RegisterNatives_FJunManagerSkiaTree();
	RegisterNatives_SJunSkiaTreeWidget();
}

FJunManagerSkiaTree* GJunManagerSkiaTree;
