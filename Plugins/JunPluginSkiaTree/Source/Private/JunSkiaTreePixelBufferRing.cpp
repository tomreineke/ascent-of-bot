#include "JunSkiaTreePixelBufferRing.h"

FJunSkiaTreePixelBufferRing::FJunSkiaTreePixelBufferRing()
{
	check(IsInGameThread());
}

void FJunSkiaTreePixelBufferRing::EnqueueForRecycling(FJunSkiaTreePixelBufferPtr PixelBuffer)
{
	RecyclingQueue.Enqueue(PixelBuffer);
}

FJunSkiaTreePixelBufferPtr FJunSkiaTreePixelBufferRing::ObtainBuffer(FIntPoint PaintSize)
{
	check(IsInGameThread());
	FJunSkiaTreePixelBufferPtr Buffer;
	while (RecyclingQueue.Dequeue(Buffer))
	{
		if (Buffer->GetSize() == PaintSize)
		{
			LastWriteBuffer = Buffer;
			return Buffer;
		}
	}
	LastWriteBuffer = MakeShared<FJunSkiaTreePixelBuffer, ESPMode::ThreadSafe>(PaintSize);
	return LastWriteBuffer;
}


bool FJunSkiaTreePixelBufferRing::IsPixelCovered(FIntPoint Position) const
{
	if (LastWriteBuffer)
	{
		return LastWriteBuffer->IsPixelCovered(Position);
	}
	else
	{
		return false;
	}
}
