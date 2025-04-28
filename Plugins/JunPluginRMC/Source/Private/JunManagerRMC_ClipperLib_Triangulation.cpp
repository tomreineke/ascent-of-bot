#include "IJunPluginRMC.h"
#include "JunManagerRMC.h"
#include "RuntimeMeshRenderable.h"
#include "clipper.h"
#include "earcut.hpp"
#include "JunClipperGeometry.h"
#include "JunJNI.h"
#include "span.hpp"

namespace mapbox {
	namespace util {
		template <>
		struct nth<0, ClipperLib::IntPoint> {
			inline static auto get(const ClipperLib::IntPoint& Point) {
				return Point.X;
			};
		};

		template <>
		struct nth<1, ClipperLib::IntPoint> {
			inline static auto get(const ClipperLib::IntPoint& Point) {
				return Point.Y;
			};
		};

	}
}

void ProcessPolygon(ClipperLib::PolyNode* Node, TArray<FVector3f>& Vertices, TArray<int>& Indices, float Scale, const TArray<ClipperLib::IntPoint>& SteinerPoints);

void ProcessRootOrHole(ClipperLib::PolyNode* Node, TArray<FVector3f>& Vertices, TArray<int>& Indices, float Scale, const TArray<ClipperLib::IntPoint>& SteinerPoints, ClipperLib::Paths* Paths)
{
	if (Paths != nullptr)
	{
		Paths->push_back(Node->Contour);
	}
	for (ClipperLib::PolyNode* Polygon : Node->Childs)
	{
		ProcessPolygon(Polygon, Vertices, Indices, Scale, SteinerPoints);
	}
}

void ProcessPolygon(ClipperLib::PolyNode* Node, TArray<FVector3f>& Vertices, TArray<int>& Indices, float Scale, const TArray<ClipperLib::IntPoint>& SteinerPoints)
{
	ClipperLib::Paths Paths;
	Paths.push_back(Node->Contour);
	for (ClipperLib::PolyNode* Hole : Node->Childs)
	{
		ProcessRootOrHole(Hole, Vertices, Indices, Scale, SteinerPoints, &Paths);
	}
	for (const ClipperLib::IntPoint& SteinerPoint : SteinerPoints)
	{
		Paths.emplace_back(ClipperLib::Path(1, SteinerPoint));
	}
	int IndexBase = Vertices.Num();
	for (ClipperLib::Path& Path : Paths)
	{
		for (ClipperLib::IntPoint& Point : Path)
		{
			Vertices.Emplace(FVector3f(Point.X / Scale, Point.Y / Scale, 0.0f));
		}
	}
	for (int Index : mapbox::earcut<int>(Paths))
	{
		Indices.Add(IndexBase + Index);
	}
	std::vector<int> Ids = mapbox::earcut<int>(Paths);
	size_t IndexCount = Ids.size();
	for (int I = 0; I < IndexCount; I += 3)
	{
		Indices.Add(IndexBase + Ids[I + 2]);
		Indices.Add(IndexBase + Ids[I + 1]);
		Indices.Add(IndexBase + Ids[I]);
	}
}

class FPathView
{
public:
	FPathView(const ClipperLib::Path& Path, int32 FirstIndex, int32 LastIndex, bool bAscending, int32 TriangleIndexBase)
		: Path(Path), PathSize(Path.size()), FirstIndex(FirstIndex), LastIndex(LastIndex), bAscending(bAscending), TriangleIndexBase(TriangleIndexBase) {}

	const ClipperLib::IntPoint& operator[](int32 Index) const
	{
		return Path[(PathSize + FirstIndex + Index * (bAscending ? 1 : -1)) % PathSize];
	}

	int32 GetVertexIndex(int32 Index) const
	{
		return ComputeIndex(Index) + TriangleIndexBase;
	}

	bool IsLast(int32 Index) const
	{
		return ComputeIndex(Index) == LastIndex;
	}

	int32 GetSize() const
	{
		if (bAscending)
		{
			if (FirstIndex <= LastIndex)
			{
				return LastIndex - FirstIndex + 1;
			}
			else
			{
				return LastIndex + PathSize - FirstIndex + 1;
			}
		}
		else
		{
			if (LastIndex < FirstIndex)
			{
				return FirstIndex - LastIndex + 1;
			}
			else
			{
				return FirstIndex + PathSize - LastIndex + 1;
			}
		}
	}
private:
	int32 ComputeIndex(int32 Index) const
	{
		return (PathSize + FirstIndex + Index * (bAscending ? 1 : -1)) % PathSize;
	}
	const ClipperLib::Path& Path;
	const int32 PathSize;
	const int32 FirstIndex;
	const int32 LastIndex;
	const bool bAscending;
	const int32 TriangleIndexBase;
};

FVector3f RightNormal2D(const FVector3f& Vector)
{
	if (FMath::IsNearlyZero(Vector.X) && FMath::IsNearlyZero(Vector.Y))
	{
		return FVector3f::ForwardVector;
	}
	else
	{
		float Scale = 1.0f / Vector.Size2D();
		return FVector3f(-Vector.Y * Scale, Vector.X * Scale, 0.0f);
	}
}

FVector3f TurnClockwise2D(const FVector3f& Vector)
{
	return FVector3f(-Vector.Y, Vector.X, 0.0f);
}

/*
 * Positive => Test is right of line given by Base and Direction.
 */
int64 Orientation2D(const FVector3f& Base, const FVector3f& Direction, const FVector3f& Test)
{
	return Direction.X * (Test.Y - Base.Y) - Direction.Y * (Test.X - Base.X);
}

float IntersectionCoefficient2D(
	const FVector3f& FirstBase,
	const FVector3f& FirstDirection,
	const FVector3f& SecondBase,
	const FVector3f& SecondDirection
)
{
	float Nominator = (SecondBase.X - FirstBase.X) * SecondDirection.Y - (SecondBase.Y - FirstBase.Y) * SecondDirection.X;
	float Denominator = FirstDirection.X * SecondDirection.Y - FirstDirection.Y * SecondDirection.X;
	return Nominator / Denominator;
}

FVector3f Intersection2D(
	const FVector3f& FirstBase,
	const FVector3f& FirstDirection,
	const FVector3f& SecondBase,
	const FVector3f& SecondDirection
)
{
	return 0.5f * (
		FirstBase + IntersectionCoefficient2D(FirstBase, FirstDirection, SecondBase, SecondDirection) * FirstDirection
		+ SecondBase + IntersectionCoefficient2D(SecondBase, SecondDirection, FirstBase, FirstDirection) * SecondDirection
	);
}

void FJunManagerRMC::RegisterNatives_ClipperLib_Triangulation()
{
	RegisterNative<+[](ClipperLib::PolyTree* Tree, jfloat Scale, jobject SteinerPointsBuffer) -> jobject {
		TArray<ClipperLib::IntPoint> SteinerPoints = J2U<TArray<ClipperLib::IntPoint>>(SteinerPointsBuffer);
		for (ClipperLib::IntPoint& Point : SteinerPoints)
		{
			UE_LOG(LogJunRMC, Log, TEXT("Steiner: (%lld, %lld)"), Point.X, Point.Y);
		}
		TArray<FVector3f> Vertices;
		TArray<int> Indices;
		ProcessRootOrHole(Tree, Vertices, Indices, Scale, SteinerPoints, nullptr);
		return U2J(U2J(Vertices), U2J(Indices));
	}>(
		"com.cerebrallychallenged.jun.clipper.TriangulationKt",
		"triangulate",
		"(JFLjava/nio/ByteBuffer;)Lkotlin/Pair;"
	);

	RegisterNative<+[](jobject PolylineBuffer, FRuntimeMeshSectionProperties* SectionProps, jfloat Distance) -> FJunSharedRef* {
		std::span<FVector3f> Polyline = J2U<std::span<FVector3f>>(PolylineBuffer);
		int32 PolylineSize = Polyline.size();
		if (Polyline.size() < 2)
		{
			return JunThrow<FJunSharedRef*>("Polyline contains less than two points.");
		}

		// Up to 4 vertices per segment join plus 3 vertices at each end.
		//int32 EstimatedVertexCount = (PolylineSize - 2) * 4 + 2 * 3;
		// Up to 5 triangles per new segment.
		//int32 EstimatedTriangleCount = (PolylineSize - 1) * 5;

		TSharedRef<FRuntimeMeshRenderableMeshData> MeshData = MakeShared<FRuntimeMeshRenderableMeshData>(*SectionProps);

		auto& Positions = MeshData->Positions;
		auto& Triangles = MeshData->Triangles;
		auto& TexCoords = MeshData->TexCoords;
		auto& Tangents = MeshData->Tangents;

		float TotalLength = 0.0f;
		FVector3f Dir[2] = {
			FVector3f(),
			Polyline[2] - Polyline[1]
		};
		FVector3f DirNormal;
		FVector3f RightNormal;
		FVector3f UpNormal;
		FVector3f OrthoRight[2] = {
			FVector3f(),
			RightNormal * Distance
		};
		auto UpdateNormals = [&]() {
			DirNormal = Dir[1].GetUnsafeNormal();
			RightNormal = TurnClockwise2D(DirNormal);
			UpNormal = FVector3f::CrossProduct(DirNormal, RightNormal);
			OrthoRight[1] = RightNormal * Distance;
		};
		UpdateNormals();

		Positions.AddF(Polyline[0] - OrthoRight[1]);
		TexCoords.AddF(FVector2f(TotalLength, -1.0f));
		Tangents.AddF(DirNormal, UpNormal);
		Positions.AddF(Polyline[0]);
		TexCoords.AddF(FVector2f(TotalLength, 0.0f));
		Tangents.AddF(DirNormal, UpNormal);
		Positions.AddF(Polyline[0] + OrthoRight[1]);
		TexCoords.AddF(FVector2f(TotalLength, 1.0f));
		Tangents.AddF(DirNormal, UpNormal);
		
		for (int32 I = 1; I < PolylineSize - 1; ++I)
		{
			FVector3f Mid = Polyline[I];
			TotalLength += FVector3f::Distance(Polyline[I - 1], Mid);
			Dir[0] = Dir[1];
			OrthoRight[0] = OrthoRight[1];
			Dir[1] = Polyline[I + 1] - Mid;
			UpdateNormals();
			int32 VertexCount = Positions.Num();
			int32 Orientation = Orientation2D(Mid, Dir[0], Polyline[I + 1]);
			int32 SquaredOrientation = Orientation * Orientation;
			int32 SquaredDirLength = Dir[0].SizeSquared();
			if (SquaredDirLength == 0)
			{
				continue;
			}
			if (SquaredOrientation > 2 * SquaredDirLength)
			{
				if (Orientation < 0)
				{
					FVector3f Crossing = Intersection2D(Mid - OrthoRight[0], Dir[0], Mid - OrthoRight[1], Dir[1]);
					Positions.AddF(Mid + OrthoRight[0]);
					TexCoords.AddF(FVector2f(TotalLength, 1.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Positions.AddF(Crossing);
					TexCoords.AddF(FVector2f(TotalLength, -1.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Positions.AddF(Mid);
					TexCoords.AddF(FVector2f(TotalLength, 0.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Positions.AddF(Mid + OrthoRight[1]);
					TexCoords.AddF(FVector2f(TotalLength, 1.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Triangles.AddTriangle(VertexCount - 2, VertexCount + 1, VertexCount - 3);
					Triangles.AddTriangle(VertexCount - 2, VertexCount + 2, VertexCount + 1);
					Triangles.AddTriangle(VertexCount - 1, VertexCount + 2, VertexCount - 2);
					Triangles.AddTriangle(VertexCount - 1, VertexCount, VertexCount + 2);
					Triangles.AddTriangle(VertexCount, VertexCount + 3, VertexCount + 2);
				}
				else // Orientation > 0
				{
					FVector3f Crossing = Intersection2D(Mid + OrthoRight[0], Dir[0], Mid + OrthoRight[1], Dir[1]);
					Positions.AddF(Mid - OrthoRight[0]);
					TexCoords.AddF(FVector2f(TotalLength, -1.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Positions.AddF(Mid - OrthoRight[1]);
					TexCoords.AddF(FVector2f(TotalLength, -1.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Positions.AddF(Mid);
					TexCoords.AddF(FVector2f(TotalLength, 0.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Positions.AddF(Crossing);
					TexCoords.AddF(FVector2f(TotalLength, 1.0f));
					Tangents.AddF(DirNormal, UpNormal);
					Triangles.AddTriangle(VertexCount - 2, VertexCount, VertexCount - 3);
					Triangles.AddTriangle(VertexCount - 2, VertexCount + 2, VertexCount);
					Triangles.AddTriangle(VertexCount - 1, VertexCount + 2, VertexCount - 2);
					Triangles.AddTriangle(VertexCount - 1, VertexCount + 3, VertexCount + 2);
					Triangles.AddTriangle(VertexCount + 2, VertexCount + 1, VertexCount);
				}
			}
			else // Orientation near 0
			{
				Positions.AddF(Mid - OrthoRight[1]);
				TexCoords.AddF(FVector2f(TotalLength, -1.0f));
				Tangents.AddF(DirNormal, UpNormal);
				Positions.AddF(Mid);
				TexCoords.AddF(FVector2f(TotalLength, 0.0f));
				Tangents.AddF(DirNormal, UpNormal);
				Positions.AddF(Mid + OrthoRight[1]);
				TexCoords.AddF(FVector2f(TotalLength, 1.0f));
				Tangents.AddF(DirNormal, UpNormal);
				Triangles.AddTriangle(VertexCount - 2, VertexCount, VertexCount - 3);
				Triangles.AddTriangle(VertexCount - 2, VertexCount + 1, VertexCount);
				Triangles.AddTriangle(VertexCount - 1, VertexCount + 1, VertexCount - 2);
				Triangles.AddTriangle(VertexCount - 1, VertexCount + 2, VertexCount + 1);
			}
		}
		int32 VertexCount = Positions.Num();
		FVector3f Mid = Polyline[PolylineSize - 1];
		Positions.AddF(Mid - OrthoRight[1]);
		TexCoords.AddF(FVector2f(TotalLength, -1.0f));
		Tangents.AddF(DirNormal, UpNormal);
		Positions.AddF(Mid);
		TexCoords.AddF(FVector2f(TotalLength, 0.0f));
		Tangents.AddF(DirNormal, UpNormal);
		Positions.AddF(Mid + OrthoRight[1]);
		TexCoords.AddF(FVector2f(TotalLength, 1.0f));
		Tangents.AddF(DirNormal, UpNormal);
		Triangles.AddTriangle(VertexCount - 2, VertexCount, VertexCount - 3);
		Triangles.AddTriangle(VertexCount - 2, VertexCount + 1, VertexCount);
		Triangles.AddTriangle(VertexCount - 1, VertexCount + 1, VertexCount - 2);
		Triangles.AddTriangle(VertexCount - 1, VertexCount + 2, VertexCount + 1);
		VertexCount += 3;
		auto& Colors = MeshData->Colors;
		Colors.SetNum(VertexCount);
		for (int I = 0; I < VertexCount; ++I)
		{
			Colors.SetColor(I, FColor::White);
		}
		return U2J(MeshData);
	}>(
		"com.cerebrallychallenged.jun.clipper.TriangulationKt",
		"triangulatePolyline",
		"(Ljava/nio/ByteBuffer;JF)J"
	);
}
