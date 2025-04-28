#pragma once

#include "CoreMinimal.h"
#include "Brushes/SlateDynamicImageBrush.h"

#include "JunManager.h"
#include "JunPointerEventKind.h"
#include "JunLifeGuard.h"
#include "JunJNI.h"
#include "JunSkiaTreePixelBufferRing.h"
//#include "JunSkiaTreeInput.h"

#include "skiatree.hpp"

using FJunSkiaTreeWidgetTick = void(*)(float DeltaTime);

using FJunSkiaTreeWidgetResize = void(*)(int Widget, int Height);

class JUNPLUGINSKIATREE_API SJunSkiaTreeWidget : public SLeafWidget
{
public:
	SJunSkiaTreeWidget();
	~SJunSkiaTreeWidget();

	SLATE_BEGIN_ARGS(SJunSkiaTreeWidget)

	{}
	SLATE_ARGUMENT(skiatree::SkiaTreeLibrary*, Library);
	SLATE_ARGUMENT(const skiatree::RefCell<skiatree::Forest>*, Forest);
	SLATE_ARGUMENT(FJunLifeGuard*, LifeGuard);

	SLATE_END_ARGS()

	void Construct(const FArguments& InArgs);

	void SetUpcalls(
		FJunSkiaTreeWidgetTick NewWidgetTick,
		FJunSkiaTreeWidgetResize NewWidgetResize
	);

	bool IsPixelCovered(FIntPoint Position) const;

	FReply OnFocusReceived(const FGeometry& MyGeometry, const FFocusEvent& FocusEvent) override;

	void OnFocusLost(const FFocusEvent& FocusEvent) override;
protected:
	void Tick(
		const FGeometry& AllottedGeometry,
		const double InCurrentTime,
		const float InDeltaTime
	) override;

	int32 OnPaint( const FPaintArgs& Args, const FGeometry& AllottedGeometry, const FSlateRect& MyCullingRect, FSlateWindowElementList& OutDrawElements, int32 LayerId, const FWidgetStyle& InWidgetStyle, bool bParentEnabled ) const;

	FVector2D ComputeDesiredSize(float) const override;
private:
	void UpdateTexture();

	FIntPoint Size;

	FIntPoint PaddedSize;

	UTexture2D* Texture;

	TSharedPtr<FSlateDynamicImageBrush> Brush;

	FJunSkiaTreePixelBufferRingRef PixelBufferRing;

	skiatree::SkiaTreeLibrary* Library;

	const skiatree::RefCell<skiatree::Forest>* Forest;

	skiatree::Surface* Surface;

	FJunLifeGuard* LifeGuard;

	FJunSkiaTreeWidgetTick WidgetTick;

	FJunSkiaTreeWidgetResize WidgetResize;

	// -----
	// Input
	// -----
private:
	static FReply ReplyOf(bool bHandled);

	static FIntPoint ExtractPosition(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent);

	static uint8 ExtractModifiers(const FInputEvent& Event);
public:
	FReply OnKeyDown(const FGeometry& MyGeometry, const FKeyEvent& KeyEvent);

	FReply OnKeyUp(const FGeometry& MyGeometry, const FKeyEvent& KeyEvent);

	FReply OnKeyChar(const FGeometry& MyGeometry, const FCharacterEvent& CharacterEvent);

	FReply OnDragDetected(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnMouseButtonDoubleClick(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnMouseButtonDown(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnMouseButtonUp(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	void OnMouseEnter(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	void OnMouseLeave(const FPointerEvent& MouseEvent) override;

	FReply OnMouseMove(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnMouseWheel(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnTouchEnded(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnTouchGesture(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnTouchMoved(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FReply OnTouchStarted(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent) override;

	FCursorReply OnCursorQuery(const FGeometry& MyGeometry, const FPointerEvent& CursorEvent) const override;

	bool SupportsKeyboardFocus() const;
private:
	bool bMouseOver;
	bool bDragging;
};
