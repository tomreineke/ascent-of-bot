#include "JunManagerRMC.h"

FJunManagerRMC::FJunManagerRMC(FJunManager& Manager) : FJunManagerExtension(Manager)
{
	Manager.RegisterExtensionClass("com.cerebrallychallenged.jun.rmc.RMCExtension");
}

void FJunManagerRMC::RegisterNatives()
{
	RegisterNatives_ARuntimeMeshActor();
	RegisterNatives_ClipperLib();
	RegisterNatives_ClipperLib_Clipper();
	RegisterNatives_ClipperLib_ClipperBase();
	RegisterNatives_ClipperLib_ClipperOffset();
	RegisterNatives_ClipperLib_Path();
	RegisterNatives_ClipperLib_Paths();
	RegisterNatives_ClipperLib_PolyNode();
	RegisterNatives_ClipperLib_PolyNodes();
	RegisterNatives_ClipperLib_PolyTree();
	RegisterNatives_ClipperLib_Triangulation();
	RegisterNatives_FRuntimeMeshRenderableMeshData();
	RegisterNatives_FRuntimeMeshSectionProperties();
	RegisterNatives_URuntimeMesh();
	RegisterNatives_URuntimeMeshComponent();
	RegisterNatives_URuntimeMeshComponentStatic();
	RegisterNatives_URuntimeMeshProvider();
	RegisterNatives_URuntimeMeshProviderBox();
	RegisterNatives_URuntimeMeshProviderCollisionFromRenderable();
	RegisterNatives_URuntimeMeshProviderMemoryCache();
	RegisterNatives_URuntimeMeshProviderNormals();
	RegisterNatives_URuntimeMeshProviderPlane();
	RegisterNatives_URuntimeMeshProviderSphere();
	RegisterNatives_URuntimeMeshProviderStatic();
	RegisterNatives_URuntimeMeshProviderStaticMesh();
	RegisterNatives_URuntimeMeshStaticMeshConverter();
}