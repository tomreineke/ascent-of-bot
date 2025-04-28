#include "JunMathUtil.h"

JUNPLUGIN_API int32 RoundUpPowerOfTwo(int32 v)
{
	// See https://graphics.stanford.edu/~seander/bithacks.html#RoundUpPowerOf2
	v--;
	v |= v >> 1;
	v |= v >> 2;
	v |= v >> 4;
	v |= v >> 8;
	v |= v >> 16;
	v++;
	return v;
}

JUNPLUGIN_API FIntPoint RoundUpPowerOfTwo(FIntPoint Point)
{
	return FIntPoint(RoundUpPowerOfTwo(Point.X), RoundUpPowerOfTwo(Point.Y));
}
