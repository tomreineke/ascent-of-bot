#include "JunSkiaTreeWidget.h"

#include "Rendering/DrawElements.h" 
#include "SlateOptMacros.h"

#include "JunManager.h"
#include "JunManagerSkiaTree.h"
#include "JunMathUtil.h"

SJunSkiaTreeWidget::SJunSkiaTreeWidget()
	: Size(FIntPoint(800, 600))
	, PaddedSize(FIntPoint::NoneValue)
	, Texture(nullptr)
	, PixelBufferRing(MakeShared<FJunSkiaTreePixelBufferRing, ESPMode::ThreadSafe>())
	, Library(nullptr)
	, Forest(nullptr)
	, Surface(nullptr)
	, WidgetTick(nullptr)
	, WidgetResize(nullptr)
{
}

SJunSkiaTreeWidget::~SJunSkiaTreeWidget()
{
}

void SJunSkiaTreeWidget::Construct(const FArguments& InArgs)
{
	Library = InArgs._Library;
	Forest = skiatree::skiatree_forest_clone(InArgs._Forest);
	LifeGuard = InArgs._LifeGuard;
}

void SJunSkiaTreeWidget::SetUpcalls(
	FJunSkiaTreeWidgetTick NewWidgetTick,
	FJunSkiaTreeWidgetResize NewWidgetResize
) {
	this->WidgetTick = NewWidgetTick;
	this->WidgetResize = NewWidgetResize;
}

bool SJunSkiaTreeWidget::IsPixelCovered(FIntPoint Position) const
{
	return PixelBufferRing->IsPixelCovered(Position);
}

FReply SJunSkiaTreeWidget::OnFocusReceived(const FGeometry& MyGeometry, const FFocusEvent& FocusEvent)
{
	return SLeafWidget::OnFocusReceived(MyGeometry, FocusEvent);
}

void SJunSkiaTreeWidget::OnFocusLost(const FFocusEvent& FocusEvent)
{
	SLeafWidget::OnFocusLost(FocusEvent);
}

FVector2D SJunSkiaTreeWidget::ComputeDesiredSize(float) const
{
	return FVector2D(800.0, 600.0);
}

void SJunSkiaTreeWidget::Tick(
	const FGeometry& AllottedGeometry,
	const double InCurrentTime,
	const float InDeltaTime
) {
	SLeafWidget::Tick(AllottedGeometry, InCurrentTime, InDeltaTime);
	FPaintGeometry PaintGeometry = AllottedGeometry.ToPaintGeometry();
	FIntPoint NewSize = AllottedGeometry.GetAbsoluteSize().IntPoint();
	if (Size != NewSize)
	{
		Size = NewSize;
		if (WidgetResize != nullptr)
		{
			WidgetResize(NewSize.X, NewSize.Y);
		}
		if (Surface != nullptr)
		{
			skiatree::skiatree_surface_delete(Surface);
			Surface = nullptr;
		}
		Surface = skiatree::skiatree_surface_new(Library, NewSize.X, NewSize.Y);

		FIntPoint NewPaddedSize = RoundUpPowerOfTwo(NewSize);
		if (NewPaddedSize != PaddedSize)
		{
			PaddedSize = NewPaddedSize;
			UTexture2D* PrevTexture = Texture;
			Texture = UTexture2D::CreateTransient(PaddedSize.X, PaddedSize.Y, EPixelFormat::PF_B8G8R8A8);
			Texture->AddToRoot();
			Texture->UpdateResource();
			Brush = MakeShared<FSlateDynamicImageBrush>(Texture, FVector2D(PaddedSize), NAME_None);
			if (PrevTexture != nullptr)
			{
				PrevTexture->RemoveFromRoot();
			}
		}
		Brush->SetUVRegion(FBox2D(
			FVector2D::ZeroVector,
			FVector2D(Size.X / (double) PaddedSize.X, Size.Y / (double) PaddedSize.Y)
		));
	}
	
	if (WidgetTick != nullptr)
	{
		WidgetTick(InDeltaTime);
	}
	UpdateTexture();
	skiatree::skiatree_forest_tick(Library, Forest);
}

int32 SJunSkiaTreeWidget::OnPaint(const FPaintArgs& Args, const FGeometry& AllottedGeometry, const FSlateRect& MyCullingRect, FSlateWindowElementList& OutDrawElements, int32 LayerId, const FWidgetStyle& InWidgetStyle, bool bParentEnabled) const
{
	if (Brush && Brush.IsValid())
	{
		FSlateDrawElement::MakeBox(OutDrawElements, LayerId, AllottedGeometry.ToPaintGeometry(), &*Brush, ESlateDrawEffect::None, FLinearColor::White);
	}
	return LayerId;
}

void SJunSkiaTreeWidget::UpdateTexture()
{
	check(IsInGameThread());

	FJunSkiaTreePixelBufferRingRef Ring = PixelBufferRing;
	FJunSkiaTreePixelBufferPtr Buffer = PixelBufferRing->ObtainBuffer(Size);

	if (Library != nullptr && Forest != nullptr && Surface != nullptr) {
		skiatree::skiatree_forest_draw_on_surface(Library, Forest, Surface);
		skiatree::skiatree_surface_flush_and_submit(Surface);
		skiatree::skiatree_surface_read_pixels(Surface, Buffer->GetData(), Buffer->GetByteSize());
		Texture->UpdateTextureRegions(
			0,
			1,
			&Buffer->TextureRegion,
			Size.X * 4,
			4,
			Buffer->GetData(),
			[Buffer, Ring](uint8*, const FUpdateTextureRegion2D* Region)
			{
				Ring->EnqueueForRecycling(Buffer);
			}
		);
	}
}
