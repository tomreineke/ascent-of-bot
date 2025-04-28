#include "JunSkiaTreeWidget.h"
#include "JunKeyMap.h"

#include "skiatree.hpp"

FReply SJunSkiaTreeWidget::ReplyOf(bool bHandled)
{
	return bHandled ? FReply::Handled() : FReply::Unhandled();
}

FIntPoint SJunSkiaTreeWidget::ExtractPosition(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	return (MouseEvent.GetScreenSpacePosition() - MyGeometry.LocalToAbsolute(FVector2D::ZeroVector)).IntPoint();
}

uint8 SJunSkiaTreeWidget::ExtractModifiers(const FInputEvent& Event)
{
	uint8 Modifiers = 0;
	if (Event.IsShiftDown())
	{
		Modifiers |= skiatree::MODIFIER_SHIFT;
	}
	if (Event.IsControlDown())
	{
		Modifiers |= skiatree::MODIFIER_CTRL;
	}
	if (Event.IsAltDown())
	{
		Modifiers |= skiatree::MODIFIER_ALT;
	}
	if (Event.IsCommandDown())
	{
		Modifiers |= skiatree::MODIFIER_COMMAND;
	}
	return Modifiers;
}

FReply SJunSkiaTreeWidget::OnKeyDown(const FGeometry& MyGeometry, const FKeyEvent& KeyEvent)
{
	uint32 KeyIndex = FJunKeyMap::Get().IndexOf(KeyEvent.GetKey());
	uint8 Modifiers = ExtractModifiers(KeyEvent);
	return ReplyOf(skiatree::skiatree_input_key_down(Library, Forest, KeyIndex, Modifiers));
}

FReply SJunSkiaTreeWidget::OnKeyUp(const FGeometry& MyGeometry, const FKeyEvent& KeyEvent)
{
	uint32 KeyIndex = FJunKeyMap::Get().IndexOf(KeyEvent.GetKey());
	uint8 Modifiers = ExtractModifiers(KeyEvent);
	return ReplyOf(skiatree::skiatree_input_key_up(Library, Forest, KeyIndex, Modifiers));
}

FReply SJunSkiaTreeWidget::OnKeyChar(const FGeometry& MyGeometry, const FCharacterEvent& CharacterEvent)
{
	return FReply::Handled();
}

FReply SJunSkiaTreeWidget::OnDragDetected(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	return FReply::Unhandled();
}

FReply SJunSkiaTreeWidget::OnMouseButtonDoubleClick(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	FIntPoint Position = ExtractPosition(MyGeometry, MouseEvent);
	if (!IsPixelCovered(Position)) return FReply::Unhandled();
	uint32 ButtonIndex = FJunKeyMap::Get().IndexOf(MouseEvent.GetEffectingButton());
	uint8 Modifiers = ExtractModifiers(MouseEvent);
	return ReplyOf(skiatree::skiatree_input_double_click(Library, Forest, Position, ButtonIndex, Modifiers));
}

FReply SJunSkiaTreeWidget::OnMouseButtonDown(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	FIntPoint Position = ExtractPosition(MyGeometry, MouseEvent);
	if (!IsPixelCovered(Position)) return FReply::Unhandled();
	FKey Button = MouseEvent.GetEffectingButton();
	uint32 ButtonIndex = FJunKeyMap::Get().IndexOf(Button);
	uint8 Modifiers = ExtractModifiers(MouseEvent);
	FReply Reply = ReplyOf(skiatree::skiatree_input_mouse_button_down(Library, Forest, Position, ButtonIndex, Modifiers));
	if (Button == EKeys::LeftMouseButton)
	{
		bDragging = true;
		Reply.CaptureMouse(AsShared());
	}
	return Reply;
}

FReply SJunSkiaTreeWidget::OnMouseButtonUp(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	FIntPoint Position = ExtractPosition(MyGeometry, MouseEvent);
	FKey Button = MouseEvent.GetEffectingButton();
	uint32 ButtonIndex = FJunKeyMap::Get().IndexOf(Button);
	uint8 Modifiers = ExtractModifiers(MouseEvent);
	FReply Reply = ReplyOf(skiatree::skiatree_input_mouse_button_up(Library, Forest, Position, ButtonIndex, Modifiers));
	if (Button == EKeys::LeftMouseButton)
	{
		bDragging = false;
		Reply.ReleaseMouseCapture();
	}
	return Reply;
}

void SJunSkiaTreeWidget::OnMouseEnter(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
}

void SJunSkiaTreeWidget::OnMouseLeave(const FPointerEvent& MouseEvent)
{
	skiatree::skiatree_input_mouse_leave(Library, Forest);
}

FReply SJunSkiaTreeWidget::OnMouseMove(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	FIntPoint Position = ExtractPosition(MyGeometry, MouseEvent);
	uint8 Modifiers = ExtractModifiers(MouseEvent);
	skiatree::skiatree_input_mouse_move(Library, Forest, Position, Modifiers);
	if (bDragging || IsPixelCovered(Position))
	{
		bMouseOver = true;
		return FReply::Handled();
	}
	else
	{
		if (bMouseOver)
		{
			skiatree::skiatree_input_mouse_leave(Library, Forest);
			bMouseOver = false;
			return FReply::Handled();
		}
		else
		{
			return FReply::Unhandled();
		}
	}
	return FReply::Unhandled();
}

FReply SJunSkiaTreeWidget::OnMouseWheel(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	FIntPoint Position = ExtractPosition(MyGeometry, MouseEvent);
	if (!bDragging && !IsPixelCovered(Position)) return FReply::Unhandled();
	uint8 Modifiers = ExtractModifiers(MouseEvent);
	skiatree::skiatree_input_mouse_wheel(Library, Forest, Position, MouseEvent.GetWheelDelta(), Modifiers);
	return FReply::Handled();
}

FReply SJunSkiaTreeWidget::OnTouchEnded(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	return FReply::Unhandled();
}

FReply SJunSkiaTreeWidget::OnTouchGesture(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	return FReply::Unhandled();
}

FReply SJunSkiaTreeWidget::OnTouchMoved(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	return FReply::Unhandled();
}

FReply SJunSkiaTreeWidget::OnTouchStarted(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	return FReply::Unhandled();
}

FCursorReply SJunSkiaTreeWidget::OnCursorQuery(const FGeometry& MyGeometry, const FPointerEvent& CursorEvent) const
{
	return SLeafWidget::OnCursorQuery(MyGeometry, CursorEvent);
}

bool SJunSkiaTreeWidget::SupportsKeyboardFocus() const
{
	return true;
}
