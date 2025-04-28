#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_PolyNodes()
{
	RegisterNative<+[](ClipperLib::PolyNodes* Nodes, size_t Index) -> ClipperLib::PolyNode* {
		return (*Nodes)[Index];
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodesKt",
		"get",
		"(JJ)J"
	);

	RegisterNative<+[](ClipperLib::PolyNodes* Nodes) -> size_t {
		return Nodes->size();
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodesKt",
		"getSize",
		"(J)J"
	);
}