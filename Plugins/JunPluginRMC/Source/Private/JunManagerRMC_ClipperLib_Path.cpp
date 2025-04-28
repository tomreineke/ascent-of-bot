#include "JunManagerRMC.h"
#include "clipper.h"

void FJunManagerRMC::RegisterNatives_ClipperLib_Path()
{
	RegisterNative<+[](jint Count) -> FJunSharedRef* {
		return U2J(MakeShared<ClipperLib::Path>(Count));
	}>(
		"com.cerebrallychallenged.jun.clipper.PathKt",
		"makeSharedOfPath",
		"(I)J"
	);

	RegisterNative<+[](jobject Buffer) -> FJunSharedRef* {
		return U2J(MakeShared<ClipperLib::Path>(J2U<ClipperLib::Path>(Buffer)));
	}>(
		"com.cerebrallychallenged.jun.clipper.PathKt",
		"makeSharedOfPath",
		"(Ljava/nio/ByteBuffer;)J"
	);

	RegisterNative<+[](ClipperLib::Path* Path) -> jobject {
		return U2J(*Path);
	}>(
		"com.cerebrallychallenged.jun.clipper.PathKt",
		"toBuffer",
		"(J)Ljava/nio/ByteBuffer;"
	);
}