#include "JunClipperGeometry.h"

ClipperLib::IntPoint Convert(FVector Vector, float Scale)
{
	return ClipperLib::IntPoint(FMath::RoundHalfToEven(Vector.X * Scale), FMath::RoundHalfToEven(Vector.Y * Scale));
}

FVector Convert(ClipperLib::IntPoint Vector, float Scale)
{
	return FVector(Vector.X / Scale, Vector.Y / Scale, 0.0f);
}

ClipperLib::IntPoint operator+(ClipperLib::IntPoint First, ClipperLib::IntPoint Second)
{
	return ClipperLib::IntPoint(First.X + Second.X, First.Y + Second.Y);
}

ClipperLib::IntPoint operator-(ClipperLib::IntPoint First, ClipperLib::IntPoint Second)
{
	return ClipperLib::IntPoint(First.X - Second.X, First.Y - Second.Y);
}

ClipperLib::IntPoint operator*(ClipperLib::IntPoint Vector, int64 Factor)
{
	return ClipperLib::IntPoint(Vector.X * Factor, Vector.Y * Factor);
}

ClipperLib::IntPoint operator*(int64 Factor, ClipperLib::IntPoint Vector)
{
	return Vector * Factor;
}

ClipperLib::IntPoint operator*(ClipperLib::IntPoint Vector, double Factor)
{
	return ClipperLib::IntPoint(ClipperLib::Round(Vector.X * Factor), ClipperLib::Round(Vector.Y * Factor));
}

ClipperLib::IntPoint operator*(double Factor, ClipperLib::IntPoint Vector)
{
	return Vector * Factor;
}

ClipperLib::IntPoint RotateClockwise(ClipperLib::IntPoint Vector)
{
	return ClipperLib::IntPoint(-Vector.Y, Vector.X);
}

FVector2D RotateClockwise(FVector2D Vector)
{
	return FVector2D(-Vector.Y, Vector.X);
}

int64 SquaredLength(ClipperLib::IntPoint Vector)
{
	return Vector.X * Vector.X + Vector.Y * Vector.Y;
}

double Length(ClipperLib::IntPoint Vector)
{
	return sqrt(SquaredLength(Vector));
}

ClipperLib::IntPoint NormalizeToLength(ClipperLib::IntPoint Vector, double NewLength)
{
	return Vector * (NewLength / Length(Vector));
}

int64 SquaredDistance(const ClipperLib::IntPoint& First, const ClipperLib::IntPoint& Second)
{
	return SquaredLength(Second - First);
}

double Distance(const ClipperLib::IntPoint& First, const ClipperLib::IntPoint& Second)
{
	return sqrt(SquaredDistance(First, Second));
}

/*
Positive => Test is right of line given by Base and Direction.
*/
int64 Orientation(const ClipperLib::IntPoint& Base, const ClipperLib::IntPoint& Direction, const ClipperLib::IntPoint& Test)
{
	return Direction.X * (Test.Y - Base.Y) - Direction.Y * (Test.X - Base.X);
}

double IntersectionCoefficient(
	const ClipperLib::IntPoint& FirstBase,
	const ClipperLib::IntPoint& FirstDirection,
	const ClipperLib::IntPoint& SecondBase,
	const ClipperLib::IntPoint& SecondDirection
)
{
	int64 Nominator = (SecondBase.X - FirstBase.X) * SecondDirection.Y - (SecondBase.Y - FirstBase.Y) * SecondDirection.X;
	int64 Denominator = FirstDirection.X * SecondDirection.Y - FirstDirection.Y * SecondDirection.X;
	return Nominator / static_cast<double>(Denominator);
}

ClipperLib::IntPoint Intersection(
	const ClipperLib::IntPoint& FirstBase,
	const ClipperLib::IntPoint& FirstDirection,
	const ClipperLib::IntPoint& SecondBase,
	const ClipperLib::IntPoint& SecondDirection
)
{
	return FirstBase + IntersectionCoefficient(FirstBase, FirstDirection, SecondBase, SecondDirection) * FirstDirection;
}