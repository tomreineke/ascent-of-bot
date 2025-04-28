#pragma once

#include "CoreMinimal.h"
#include "JunManager.h"
#include "JunManagerExtension.h"
#include "JunConv.h"
//#include "jni.h"

class FJunManagerRMC : public FJunManagerExtension
{
public:
	FJunManagerRMC(FJunManager& Manager);

	void RegisterNatives() override;
private:
	void RegisterNatives_ARuntimeMeshActor();
	void RegisterNatives_ClipperLib();
	void RegisterNatives_ClipperLib_Clipper();
	void RegisterNatives_ClipperLib_ClipperBase();
	void RegisterNatives_ClipperLib_ClipperOffset();
	void RegisterNatives_ClipperLib_Path();
	void RegisterNatives_ClipperLib_Paths();
	void RegisterNatives_ClipperLib_PolyNode();
	void RegisterNatives_ClipperLib_PolyNodes();
	void RegisterNatives_ClipperLib_PolyTree();
	void RegisterNatives_ClipperLib_Triangulation();
	void RegisterNatives_FRuntimeMeshRenderableMeshData();
	void RegisterNatives_FRuntimeMeshSectionProperties();
	void RegisterNatives_URuntimeMesh();
	void RegisterNatives_URuntimeMeshComponent();
	void RegisterNatives_URuntimeMeshComponentStatic();
	void RegisterNatives_URuntimeMeshProvider();
	void RegisterNatives_URuntimeMeshProviderBox();
	void RegisterNatives_URuntimeMeshProviderCollisionFromRenderable();
	void RegisterNatives_URuntimeMeshProviderMemoryCache();
	void RegisterNatives_URuntimeMeshProviderNormals();
	void RegisterNatives_URuntimeMeshProviderPlane();
	void RegisterNatives_URuntimeMeshProviderSphere();
	void RegisterNatives_URuntimeMeshProviderStatic();
	void RegisterNatives_URuntimeMeshProviderStaticMesh();
	void RegisterNatives_URuntimeMeshStaticMeshConverter();

};