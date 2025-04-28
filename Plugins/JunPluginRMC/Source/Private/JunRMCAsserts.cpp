#include "RuntimeMeshCore.h"
#include "clipper.h"

static_assert(sizeof(ClipperLib::ClipType) == 4, "Unexpected sizeof(ClipperLib::ClipType)");
static_assert(sizeof(ClipperLib::EndType) == 4, "Unexpected sizeof(ClipperLib::EndType)");
static_assert(sizeof(ClipperLib::InitOptions) == 4, "Unexpected sizeof(ClipperLib::InitOptions)");
static_assert(sizeof(ClipperLib::JoinType) == 4, "Unexpected sizeof(ClipperLib::JoinType)");
static_assert(sizeof(ClipperLib::PolyFillType) == 4, "Unexpected sizeof(ClipperLib::PolyFillType)");
static_assert(sizeof(ClipperLib::PolyType) == 4, "Unexpected sizeof(ClipperLib::PolyType)");

static_assert(sizeof(ERuntimeMeshMobility) == 1, "Unexpected sizeof(ERuntimeMeshMobility)");
static_assert(sizeof(ERuntimeMeshUpdateFrequency) == 1, "Unexpected sizeof(ERuntimeMeshUpdateFrequency)");
static_assert(sizeof(ERuntimeMeshCollisionCookingMode) == 1, "Unexpected sizeof(ERuntimeMeshCollisionCookingMode)");
static_assert(sizeof(FRuntimeMeshTangent) == 16, "Unexpected sizeof(FRuntimeMeshTangent)");