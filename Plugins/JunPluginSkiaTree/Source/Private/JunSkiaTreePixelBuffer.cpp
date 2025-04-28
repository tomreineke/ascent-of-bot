#include "JunSkiaTreePixelBuffer.h"

#include "Containers/ArrayView.h"

FJunSkiaTreePixelBuffer::FJunSkiaTreePixelBuffer(FIntPoint Size)
	: Size(Size)
	, TextureRegion(0, 0, 0, 0, Size.X, Size.Y)
{
	Buffer.SetNum(Size.X * Size.Y * 4);
}

FJunSkiaTreePixelBuffer::~FJunSkiaTreePixelBuffer()
{
}

FIntPoint FJunSkiaTreePixelBuffer::GetSize() const
{
	return Size;
}

void FJunSkiaTreePixelBuffer::CopyFrom(const void* RawBuffer)
{
	FMemory::Memcpy(Buffer.GetData(), RawBuffer, Buffer.Num());
}

uint8* FJunSkiaTreePixelBuffer::GetData()
{
	return Buffer.GetData();
}

size_t FJunSkiaTreePixelBuffer::GetByteSize() const
{
	return Buffer.Num();
}

bool FJunSkiaTreePixelBuffer::IsPixelCovered(FIntPoint Position) const
{
	if (Position.X < 0 || Size.X <= Position.X || Position.Y < 0 || Size.Y <= Position.Y) return false;
	return Buffer[(Position.Y * Size.X + Position.X) * 4 + 3] > 0;
}
