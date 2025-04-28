#pragma once

#include "clipper.h"

ClipperLib::IntPoint Convert(FVector Vector, float Scale);

FVector Convert(ClipperLib::IntPoint, float Scale);

ClipperLib::IntPoint operator+(ClipperLib::IntPoint First, ClipperLib::IntPoint Second);

ClipperLib::IntPoint operator-(ClipperLib::IntPoint First, ClipperLib::IntPoint Second);

ClipperLib::IntPoint operator*(ClipperLib::IntPoint Vector, int64 Factor);

ClipperLib::IntPoint operator*(int64 Factor, ClipperLib::IntPoint Vector);

ClipperLib::IntPoint operator*(ClipperLib::IntPoint Vector, double Factor);

ClipperLib::IntPoint operator*(double Factor, ClipperLib::IntPoint Vector);

ClipperLib::IntPoint RotateClockwise(ClipperLib::IntPoint Vector);

FVector2D RotateClockwise(FVector2D Vector);

int64 SquaredLength(ClipperLib::IntPoint Vector);

double Length(ClipperLib::IntPoint Vector);

ClipperLib::IntPoint NormalizeToLength(ClipperLib::IntPoint Vector, double NewLength);

int64 SquaredDistance(const ClipperLib::IntPoint& First, const ClipperLib::IntPoint& Second);

double Distance(const ClipperLib::IntPoint& First, const ClipperLib::IntPoint& Second);

int64 Orientation(const ClipperLib::IntPoint& Start, const ClipperLib::IntPoint& End, const ClipperLib::IntPoint& Test);

double IntersectionCoefficient(
	const ClipperLib::IntPoint& FirstBase,
	const ClipperLib::IntPoint& FirstDirection,
	const ClipperLib::IntPoint& SecondBase,
	const ClipperLib::IntPoint& SecondDirection
);

ClipperLib::IntPoint Intersection(
	const ClipperLib::IntPoint& FirstBase,
	const ClipperLib::IntPoint& FirstDirection,
	const ClipperLib::IntPoint& SecondBase,
	const ClipperLib::IntPoint& SecondDirection
);