#pragma once

#include "CoreMinimal.h"
#include "Widgets/Layout/SBorder.h"

#include "JunLifeGuard.h"
#include "JunJNI.h"


class JUNPLUGIN_API SJunToolTipWidget : public SBorder
{
public:
	SLATE_BEGIN_ARGS(SJunToolTipWidget)
	{}

	SLATE_ARGUMENT(FJunLifeGuard*, LifeGuard);

	SLATE_END_ARGS()

	void Construct(const FArguments& InArgs);

	//void Initialize(FJunLifeGuard* NewLifeGuard);

	void OnMouseEnter(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent);

	void OnMouseLeave(const FPointerEvent& MouseEvent);

	void Tick(const FGeometry& AllottedGeometry, const double InCurrentTime, const float InDeltaTime);

	double GetDelay() const;

	void SetDelay(double NewDelay);
private:
	FJunLifeGuard* LifeGuard;

	double Delay;

	FVector2D LastMousePosition;

	double StartTime;
	
	bool bShowingToolTip;
};

class FJunToolTipWidgetJNI
{
public:
	FJunToolTipWidgetJNI(FJunManager& Manager);
	~FJunToolTipWidgetJNI();

	void NotifyShowToolTip(TSharedPtr<SJunToolTipWidget>&& Widget, FVector2D MousePosition, FSlateRect ControlBounds);
	void NotifyHideToolTip(TSharedPtr<SJunToolTipWidget>&& Widget);
private:
	FJunManager& Manager;
	//jclass SharedPtrOfSJunToolTipWidgetClass;
	//jmethodID NotifyShowToolTipID;
	//jmethodID NotifyHideToolTipID;
};