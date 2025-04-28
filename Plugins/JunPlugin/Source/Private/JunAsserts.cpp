#include "JunManager.h"
#include "Components/SplineComponent.h"
#include "Components/SplineMeshComponent.h"
#include "Components/TextRenderComponent.h"
#include "Components/WidgetComponent.h"

// Make sure that the enums are correctly reinterpreted as jbyte or jint.
static_assert(sizeof(EAttachmentRule) == 1, "Unexpected sizeof(EAttachmentRule)");
static_assert(sizeof(EAudioFaderCurve) == 1, "Unexpected sizeof(EAudioFaderCurve)");
static_assert(sizeof(ECameraProjectionMode::Type) == 4, "Unexpected sizeof(ECameraProjectionMode::Type)");
static_assert(sizeof(ECollisionChannel) == 4, "Unexpected sizeof(ECollisionChannel)");
static_assert(sizeof(ECollisionEnabled::Type) == 4, "Unexpected sizeof(ECollisionEnabled)");
static_assert(sizeof(EComponentMobility::Type) == 4, "Unexpected sizeof(EComponentMobility::Type)");
static_assert(sizeof(EFocusCause) == 1, "Unexpected sizeof(EFocusCause)");
static_assert(sizeof(EHorizontalAlignment) == 4, "Unexpected sizeof(EHorizontalAlignment)");
static_assert(sizeof(EHorizTextAligment) == 4, "Unexpected sizeof(EHorizTextAligment)");
static_assert(sizeof(ELightUnits) == 1, "Unexpected sizeof(ELightUnits)");
static_assert(sizeof(EMaterialDomain) == 4, "Unexpected sizeof(EMaterialDomain)");
static_assert(sizeof(EMouseCursor::Type) == 4, "Unexpected sizeof(EMouseCursor::Type)");
static_assert(sizeof(EMouseLockMode) == 1, "Unexpected sizeof(EMouseLockMode)");
static_assert(sizeof(EPixelFormat) == 1, "Unexpected sizeof(EPixelFormat)");
static_assert(sizeof(EPSCPoolMethod) == 1, "Unexpected sizeof(EPSCPoolMethod)");
static_assert(sizeof(ESlateBrushImageType::Type) == 4, "Unexpected sizeof(ESlateBrushImageType)");
static_assert(sizeof(ESlateBrushTileType::Type) == 4, "Unexpected sizeof(ESlateBrushTileType)");
static_assert(sizeof(ESplineCoordinateSpace::Type) == 4, "Unexpected sizeof(ESplineCoordinateSpace)");
static_assert(sizeof(ESplineMeshAxis::Type) == 4, "Unexpected sizeof(ESplineMeshAxis)");
static_assert(sizeof(ETextJustify::Type) == 4, "Unexpected sizeof(ETextJustify::Type)");
static_assert(sizeof(EVerticalAlignment) == 4, "Unexpected sizeof(EVerticalAlignment)");
static_assert(sizeof(EVerticalTextAligment) == 4, "Unexpected sizeof(EVerticalTextAligment)");
static_assert(sizeof(EVisibility) == 1, "Unexpected sizeof(EVisibility)");
static_assert(sizeof(EWidgetSpace) == 1, "Unexpected sizeof(EWidgetSpace)");
static_assert(sizeof(FColor) == 4, "Unexpected sizeof(FColor)");

// Make sure that data passed through the ArgumentTransferBuffer has the expected size.
static_assert(sizeof(FVector) == 3 * sizeof(double), "Unexpected sizeof(FVector)");
static_assert(sizeof(FTransform) == 3 * 4 * sizeof(double), "Unexpected sizeof(FTransform)");
static_assert(sizeof(FLinearColor) == 4 * sizeof(float), "Unexpected sizeof(FLinearColor)");
static_assert(sizeof(FRotator) == 3 * sizeof(double), "Unexpected sizeof(FRotator)");

static_assert(sizeof(TEnumAsByte<ESplinePointType::Type>) == 1, "Unexpected sizeof(TEnumAsByte<ESplinePointType::Type>)");


//static_assert(sizeof(FSplinePoint) == 68, "Unexpected sizeof(FSplinePoint)");
static_assert(sizeof(FSplinePoint) == 136, "Unexpected sizeof(FSplinePoint)");
static_assert(offsetof(FSplinePoint, InputKey) == 0, "Unexpected offsetof(FSplinePoint, InputKey)");
static_assert(offsetof(FSplinePoint, Position) == 8, "Unexpected offsetof(FSplinePoint, Position)");
static_assert(offsetof(FSplinePoint, ArriveTangent) == 32, "Unexpected offsetof(FSplinePoint, ArriveTangent)");
static_assert(offsetof(FSplinePoint, LeaveTangent) == 56, "Unexpected offsetof(FSplinePoint, LeaveTangent)");
static_assert(offsetof(FSplinePoint, Rotation) == 80, "Unexpected offsetof(FSplinePoint, Rotation)");
static_assert(offsetof(FSplinePoint, Scale) == 104, "Unexpected offsetof(FSplinePoint, Scale)");
static_assert(offsetof(FSplinePoint, Type) == 128, "Unexpected offsetof(FSplinePoint, Type)");
