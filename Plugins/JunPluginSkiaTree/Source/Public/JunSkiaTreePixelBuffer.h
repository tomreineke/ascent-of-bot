#pragma once

#include "Math/IntPoint.h"
#include "Containers/Array.h"
#include "Templates/SharedPointer.h"
#include "Templates/Atomic.h"
//#include "skiatree.h"

class FJunSkiaTreePixelBuffer
{
public:
	FJunSkiaTreePixelBuffer(FIntPoint Size);
	FJunSkiaTreePixelBuffer(const FJunSkiaTreePixelBuffer&) = delete;
	~FJunSkiaTreePixelBuffer();

	FIntPoint GetSize() const;
	void CopyFrom(const void* RawBuffer);
	uint8* GetData();
	size_t GetByteSize() const;
	bool IsPixelCovered(FIntPoint Position) const;
private:
	FIntPoint Size;
	TArray<uint8> Buffer;
public:
	const FUpdateTextureRegion2D TextureRegion;
};

using FJunSkiaTreePixelBufferPtr = TSharedPtr<FJunSkiaTreePixelBuffer, ESPMode::ThreadSafe>;

