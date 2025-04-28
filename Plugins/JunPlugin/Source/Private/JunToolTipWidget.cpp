#include "JunToolTipWidget.h"

#include "Framework/Application/SlateApplication.h"

#include "JunManager.h"

void SJunToolTipWidget::Construct(const FArguments& InArgs)
{
	LifeGuard = InArgs._LifeGuard;
	Delay = 0.0;
	StartTime = NAN;
	bShowingToolTip = false;
}

void SJunToolTipWidget::OnMouseEnter(const FGeometry& MyGeometry, const FPointerEvent& MouseEvent)
{
	SBorder::OnMouseEnter(MyGeometry, MouseEvent);
	LastMousePosition = MyGeometry.GetAbsolutePosition();
	StartTime = FSlateApplication::Get().GetCurrentTime();
}

void SJunToolTipWidget::OnMouseLeave(const FPointerEvent& MouseEvent)
{
	SBorder::OnMouseLeave(MouseEvent);
	StartTime = NAN;
	if (bShowingToolTip)
	{
		bShowingToolTip = false;
		LifeGuard->ExecuteIfOpen([this](FJunManager* Manager)
		{
			Manager->JunToolTipWidgetJNI->NotifyHideToolTip(StaticCastSharedRef<SJunToolTipWidget>(AsShared()));
		});
	}
}

void SJunToolTipWidget::Tick(const FGeometry& AllottedGeometry, const double InCurrentTime, const float InDeltaTime)
{
	if (!bShowingToolTip && InCurrentTime >= StartTime + Delay)
	{
		bShowingToolTip = true;
		LifeGuard->ExecuteIfOpen([this, AllottedGeometry](FJunManager* Manager)
		{
			Manager->JunToolTipWidgetJNI->NotifyShowToolTip(StaticCastSharedRef<SJunToolTipWidget>(AsShared()), LastMousePosition, AllottedGeometry.GetRenderBoundingRect());
		});
	}
}

double SJunToolTipWidget::GetDelay() const
{
	return Delay;
}

void SJunToolTipWidget::SetDelay(double NewDelay)
{
	this->Delay = NewDelay;
}




FJunToolTipWidgetJNI::FJunToolTipWidgetJNI(FJunManager& Manager) : Manager(Manager)
{
	//UE_LOG(LogJun, Log, TEXT("Loading com.cerebrallychallenged.jun.unreal.widget.TSharedPtrOfSJunToolTipWidget..."));
	//SharedPtrOfSJunToolTipWidgetClass = Manager.GetClassLoader()->LoadClassGlobalRef("com.cerebrallychallenged.jun.unreal.widget.TSharedPtrOfSJunToolTipWidget");
	//NotifyShowToolTipID = GEnv->GetMethodID(
	//	SharedPtrOfSJunToolTipWidgetClass,
	//	"notifyShowToolTipImpl",
	//	"(Lcom/cerebrallychallenged/jun/math/geo/Vec2f;Lcom/cerebrallychallenged/jun/math/geo/Vec2f;Lcom/cerebrallychallenged/jun/math/geo/Vec2f;)V"
	//);
	//NotifyHideToolTipID = GEnv->GetMethodID(SharedPtrOfSJunToolTipWidgetClass, "notifyHideToolTipImpl", "()V");
}

FJunToolTipWidgetJNI::~FJunToolTipWidgetJNI()
{
	//GEnv->DeleteGlobalRef(SharedPtrOfSJunToolTipWidgetClass);
}

void FJunToolTipWidgetJNI::NotifyShowToolTip(TSharedPtr<SJunToolTipWidget>&& Widget, FVector2D MousePosition, FSlateRect ControlBounds)
{
	//GEnv->CallVoidMethod(
	//	GJunWrapperManager->Wrap<SJunToolTipWidget>(std::move(Widget)),
	//	NotifyShowToolTipID,
	//	U2J(MousePosition),
	//	U2J(ControlBounds.GetTopLeft()),
	//	U2J(ControlBounds.GetBottomRight())
	//);
	//Manager.JunCheckException();
}

void FJunToolTipWidgetJNI::NotifyHideToolTip(TSharedPtr<SJunToolTipWidget>&& Widget)
{
	//GEnv->CallVoidMethod(GJunWrapperManager->Wrap<SJunToolTipWidget>(std::move(Widget)), NotifyHideToolTipID);
	//Manager.JunCheckException();
}
