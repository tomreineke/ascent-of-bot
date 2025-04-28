#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_PolyNode()
{
	RegisterNative<+[](ClipperLib::PolyNode* Node) -> ClipperLib::PolyNodes* {
		return &Node->Childs;
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodeKt",
		"getChilds",
		"(J)J"
	);

	RegisterNative<+[](ClipperLib::PolyNode* Node) -> ClipperLib::Path* {
		return &Node->Contour;
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodeKt",
		"getContour",
		"(J)J"
	);

	RegisterNative<+[](ClipperLib::PolyNode* Node) -> ClipperLib::PolyNode* {
		return Node->GetNext();
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodeKt",
		"getNext",
		"(J)J"
	);

	RegisterNative<+[](ClipperLib::PolyNode* Node) -> ClipperLib::PolyNode* {
		return Node->Parent;
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodeKt",
		"getParent",
		"(J)J"
	);

	RegisterNative<+[](ClipperLib::PolyNode* Node) -> jboolean {
		return U2J(Node->IsHole());
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodeKt",
		"isHole",
		"(J)Z"
	);

	RegisterNative<+[](ClipperLib::PolyNode* Node) -> jboolean {
		return U2J(Node->IsOpen());
	}>(
		"com.cerebrallychallenged.jun.clipper.PolyNodeKt",
		"isOpen",
		"(J)Z"
	);
}