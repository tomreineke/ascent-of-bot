#pragma once

#include "Math/IntPoint.h"
#include "Containers/Array.h"
#include "Containers/Queue.h"

#include "JunSkiaTreePixelBuffer.h"
//#include "skiatree.h"

class FJunSkiaTreePixelBufferRing
{
public:
	FJunSkiaTreePixelBufferRing();
	void EnqueueForRecycling(FJunSkiaTreePixelBufferPtr PixelBuffer);
	FJunSkiaTreePixelBufferPtr ObtainBuffer(FIntPoint PaintSize);
	//void EnqueueCopyForPainting(const void* Data, FIntPoint PaintSize);
	//FJunSkiaTreePixelBufferPtr DequeueForPainting();
	bool IsPixelCovered(FIntPoint Position) const;
private:
	//int32 Capacity;
	//int32 Count;
	//int32 ReadIndex;
	//int32 WriteIndex;
	//TArray<FJunSkiaTreePixelBufferPtr> Array;
	TQueue<FJunSkiaTreePixelBufferPtr, EQueueMode::Mpsc> RecyclingQueue;
	FJunSkiaTreePixelBufferPtr LastWriteBuffer;
};

using FJunSkiaTreePixelBufferRingRef = TSharedRef<FJunSkiaTreePixelBufferRing, ESPMode::ThreadSafe>;
