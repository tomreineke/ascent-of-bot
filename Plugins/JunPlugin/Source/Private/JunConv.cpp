#include "JunConv.h"

JUNPLUGIN_API jint U2J(EVisibility Visibility)
{
	if (Visibility == EVisibility::Visible)
	{
		return 0;
	}
	else if (Visibility == EVisibility::Collapsed)
	{
		return 1;
	}
	else if (Visibility == EVisibility::Hidden)
	{
		return 2;
	}
	else if (Visibility == EVisibility::HitTestInvisible)
	{
		return 3;
	}
	else if (Visibility == EVisibility::SelfHitTestInvisible)
	{
		return 4;
	}
	else
	{
		return 5;
	}
}

JUNPLUGIN_API EVisibility J2UEVisibility(jint VisibilityMagic)
{
	switch (VisibilityMagic)
	{
	case 0:
		return EVisibility::Visible;
	case 1:
		return EVisibility::Collapsed;
	case 2:
		return EVisibility::Hidden;
	case 3:
		return EVisibility::HitTestInvisible;
	case 4:
		return EVisibility::SelfHitTestInvisible;
	default:
		return EVisibility::All;
	}
}

JUNPLUGIN_API FAttachmentTransformRules J2UFAttachmentTransformRules(jint Magic)
{
	EAttachmentRule LocationRule = static_cast<EAttachmentRule>(Magic & 0b11);
	EAttachmentRule RotationRule = static_cast<EAttachmentRule>((Magic >> 2) & 0b11);
	EAttachmentRule ScaleRule = static_cast<EAttachmentRule>((Magic >> 4) & 0b11);
	bool bWeldSimulatedBodies = ((Magic >> 6) & 0b1) != 0;
	return FAttachmentTransformRules(LocationRule, RotationRule, ScaleRule, bWeldSimulatedBodies);
}

JUNPLUGIN_API FDetachmentTransformRules J2UFDetachmentTransformRules(jint Magic)
{
	EDetachmentRule LocationRule = static_cast<EDetachmentRule>(Magic & 0b11);
	EDetachmentRule RotationRule = static_cast<EDetachmentRule>((Magic >> 2) & 0b11);
	EDetachmentRule ScaleRule = static_cast<EDetachmentRule>((Magic >> 4) & 0b11);
	bool bCallModify = ((Magic >> 6) & 0b1) != 0;
	return FDetachmentTransformRules(LocationRule, RotationRule, ScaleRule, bCallModify);
}

JUNPLUGIN_API jint EncodeModifiers(const FInputEvent& Event)
{
	jint result = 0;
	if (Event.IsShiftDown()) result |= (1 << 0);
	if (Event.IsLeftShiftDown()) result |= (1 << 1);
	if (Event.IsRightShiftDown()) result |= (1 << 2);
	if (Event.IsControlDown()) result |= (1 << 3);
	if (Event.IsLeftControlDown()) result |= (1 << 4);
	if (Event.IsRightControlDown()) result |= (1 << 5);
	if (Event.IsAltDown()) result |= (1 << 6);
	if (Event.IsLeftAltDown()) result |= (1 << 7);
	if (Event.IsRightAltDown()) result |= (1 << 8);
	if (Event.IsCommandDown()) result |= (1 << 9);
	if (Event.IsLeftCommandDown()) result |= (1 << 10);
	if (Event.IsRightCommandDown()) result |= (1 << 11);
	return result;
}

JUNPLUGIN_API jint EncodeMouseButton(const FPointerEvent& Event)
{
	FKey Key = Event.GetEffectingButton();
	if (Key.IsMouseButton())
	{
		if (Key == EKeys::LeftMouseButton)
		{
			return 1;
		}
		else if (Key == EKeys::MiddleMouseButton)
		{
			return 2;
		}
		else if (Key == EKeys::RightMouseButton)
		{
			return 3;
		}
		else if (Key == EKeys::ThumbMouseButton)
		{
			return 4;
		}
		else if (Key == EKeys::ThumbMouseButton2)
		{
			return 5;
		}
	}
	return 0;
}
