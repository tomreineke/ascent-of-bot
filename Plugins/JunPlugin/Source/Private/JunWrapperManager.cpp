#include "JunWrapperManager.h"
#include "JunManager.h"

#include "CineCameraActor.h"
#include "Components/RectLightComponent.h"
#include "Components/SplineMeshComponent.h"
#include "Engine/GameViewportClient.h"
#include "Engine/World.h"
#include <cstring>

JUNPLUGIN_API FJunWrapperManager* GJunWrapperManager = nullptr;

FJunWrapperManager::FJunWrapperManager(UWorld* World, TSharedPtr<FJunClassLoader> ClassLoader)
	: World(World)
	, ClassLoader(ClassLoader)
{
	UE_LOG(LogJun, Log, TEXT("Initializing FJunWrapperManager"));
	Vec2iClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Vec2i");
	Vec2iConstructorID = GEnv->GetMethodID(Vec2iClass, "<init>", "(II)V");
	Vec2iXID = GEnv->GetFieldID(Vec2iClass, "x", "I");
	Vec2iYID = GEnv->GetFieldID(Vec2iClass, "y", "I");

	Vec2fClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Vec2f");
	Vec2fConstructorID = GEnv->GetMethodID(Vec2fClass, "<init>", "(FF)V");
	Vec2fXID = GEnv->GetFieldID(Vec2fClass, "x", "F");
	Vec2fYID = GEnv->GetFieldID(Vec2fClass, "y", "F");

	Vec3fClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Vec3f");
	Vec3fConstructorID = GEnv->GetMethodID(Vec3fClass, "<init>", "(FFF)V");
	Vec3fXID = GEnv->GetFieldID(Vec3fClass, "x", "F");
	Vec3fYID = GEnv->GetFieldID(Vec3fClass, "y", "F");
	Vec3fZID = GEnv->GetFieldID(Vec3fClass, "z", "F");

	Vec4fClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Vec4f");
	Vec4fConstructorID = GEnv->GetMethodID(Vec4fClass, "<init>", "(FFFF)V");
	Vec4fXID = GEnv->GetFieldID(Vec4fClass, "x", "F");
	Vec4fYID = GEnv->GetFieldID(Vec4fClass, "y", "F");
	Vec4fZID = GEnv->GetFieldID(Vec4fClass, "z", "F");
	Vec4fWID = GEnv->GetFieldID(Vec4fClass, "w", "F");

	QuaternionClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Quaternion");
	QuaternionConstructorID = GEnv->GetMethodID(QuaternionClass, "<init>", "(Lcom/cerebrallychallenged/jun/math/geo/Vec4f;)V");
	QuaternionVectorID = GEnv->GetFieldID(QuaternionClass, "vector", "Lcom/cerebrallychallenged/jun/math/geo/Vec4f;");

	Transform3fClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Transform3f");
	Transform3fConstructorID = GEnv->GetMethodID(Transform3fClass, "<init>", "(Lcom/cerebrallychallenged/jun/math/geo/Quaternion;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;)V");
	Transform3fRotationID = GEnv->GetFieldID(Transform3fClass, "rotation", "Lcom/cerebrallychallenged/jun/math/geo/Quaternion;");
	Transform3fTranslationID = GEnv->GetFieldID(Transform3fClass, "translation", "Lcom/cerebrallychallenged/jun/math/geo/Vec3f;");
	Transform3fScaleID = GEnv->GetFieldID(Transform3fClass, "scale", "Lcom/cerebrallychallenged/jun/math/geo/Vec3f;");

	BoundsClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.Bounds");
	BoundsGetMinID = GEnv->GetMethodID(BoundsClass, "getMin", "()Lcom/cerebrallychallenged/jun/math/geo/Vec;");
	BoundsGetMaxID = GEnv->GetMethodID(BoundsClass, "getMax", "()Lcom/cerebrallychallenged/jun/math/geo/Vec;");

	NonEmptyBoundsClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.math.geo.NonEmptyBounds");
	NonEmptyBoundsConstructorID = GEnv->GetMethodID(NonEmptyBoundsClass, "<init>", "(Lcom/cerebrallychallenged/jun/math/geo/Vec;Lcom/cerebrallychallenged/jun/math/geo/Vec;)V");

	PairClass = ClassLoader->LoadClassGlobalRef("kotlin.Pair");
	PairConstructorID = GEnv->GetMethodID(PairClass, "<init>", "(Ljava/lang/Object;Ljava/lang/Object;)V");

	FBoxSphereBoundsClass = ClassLoader->LoadClassGlobalRef("com.cerebrallychallenged.jun.unreal.math.FBoxSphereBounds");
	FBoxSphereBoundsConstructorID = GEnv->GetMethodID(FBoxSphereBoundsClass, "<init>", "(Lcom/cerebrallychallenged/jun/math/geo/Vec3f;Lcom/cerebrallychallenged/jun/math/geo/Vec3f;F)V");
	FBoxSphereBoundsOriginID = GEnv->GetFieldID(FBoxSphereBoundsClass, "origin", "Lcom/cerebrallychallenged/jun/math/geo/Vec3f;");
	FBoxSphereBoundsBoxExtentID = GEnv->GetFieldID(FBoxSphereBoundsClass, "boxExtent", "Lcom/cerebrallychallenged/jun/math/geo/Vec3f;");
	FBoxSphereBoundsSphereRadiusID = GEnv->GetFieldID(FBoxSphereBoundsClass, "sphereRadius", "F");

	jclass ByteOrderClass = ClassLoader->LoadClassLocalRef("java.nio.ByteOrder");
	jfieldID ByteOrderLittleEndianID = GEnv->GetStaticFieldID(ByteOrderClass, "LITTLE_ENDIAN", "Ljava/nio/ByteOrder;");
	ByteOrderLittleEndian = GEnv->NewGlobalRef(GEnv->GetStaticObjectField(ByteOrderClass, ByteOrderLittleEndianID));
	GEnv->DeleteLocalRef(ByteOrderClass);

	ByteBufferClass = ClassLoader->LoadClassGlobalRef("java.nio.ByteBuffer");
	ByteBufferAllocateDirectID = GEnv->GetStaticMethodID(ByteBufferClass, "allocateDirect", "(I)Ljava/nio/ByteBuffer;");
	ByteBufferOrderID = GEnv->GetMethodID(ByteBufferClass, "order", "(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;");
}

FJunWrapperManager::~FJunWrapperManager()
{
	GEnv->DeleteGlobalRef(Vec2iClass);
	GEnv->DeleteGlobalRef(Vec2fClass);
	GEnv->DeleteGlobalRef(Vec3fClass);
	GEnv->DeleteGlobalRef(Vec4fClass);
	GEnv->DeleteGlobalRef(QuaternionClass);
	GEnv->DeleteGlobalRef(Transform3fClass);
	GEnv->DeleteGlobalRef(BoundsClass);
	GEnv->DeleteGlobalRef(NonEmptyBoundsClass);
	GEnv->DeleteGlobalRef(PairClass);
	GEnv->DeleteGlobalRef(FBoxSphereBoundsClass);
	GEnv->DeleteGlobalRef(ByteOrderLittleEndian);
	GEnv->DeleteGlobalRef(ByteBufferClass);
}

jobject FJunWrapperManager::CreateByteBuffer(size_t Capacity, JNIEnv* Env)
{
	jint CapacityJ = Capacity;
	jobject ByteBuffer = Env->CallStaticObjectMethod(
		ByteBufferClass,
		ByteBufferAllocateDirectID,
		CapacityJ
	);
	Env->CallObjectMethod(
		ByteBuffer,
		ByteBufferOrderID,
		ByteOrderLittleEndian
	);
	return ByteBuffer;
}

JUNPLUGIN_API jobject U2J(int32 X, int32 Y)
{
	return GEnv->NewObject(GJunWrapperManager->Vec2iClass, GJunWrapperManager->Vec2iConstructorID, X, Y);
}

JUNPLUGIN_API jobject U2J(FIntPoint Point)
{
	return U2J(Point.X, Point.Y);
}

template<typename T, JUN_IF_SAME((T), (FIntPoint))>
JUNPLUGIN_API FIntPoint J2U(jobject Vec2iObj)
{
	return FIntPoint(
		GEnv->GetIntField(Vec2iObj, GJunWrapperManager->Vec2iXID),
		GEnv->GetIntField(Vec2iObj, GJunWrapperManager->Vec2iYID)
	);
}

template JUNPLUGIN_API FIntPoint J2U<FIntPoint>(jobject Vec2iObj);

JUNPLUGIN_API jobject U2J(FVector2D Vector)
{
	return GEnv->NewObject(
		GJunWrapperManager->Vec2fClass,
		GJunWrapperManager->Vec2fConstructorID,
		static_cast<float>(Vector.X),
		static_cast<float>(Vector.Y)
	);
}

template<typename T, JUN_IF_SAME((T), (FVector2D))>
JUNPLUGIN_API FVector2D J2U(jobject Vec2fObj)
{
	return FVector2D(
		GEnv->GetFloatField(Vec2fObj, GJunWrapperManager->Vec2fXID),
		GEnv->GetFloatField(Vec2fObj, GJunWrapperManager->Vec2fYID)
	);
}

template JUNPLUGIN_API FVector2D J2U<FVector2D>(jobject Vec2fObj);

JUNPLUGIN_API jobject U2J(FVector Vector)
{
	return GEnv->NewObject(
		GJunWrapperManager->Vec3fClass,
		GJunWrapperManager->Vec3fConstructorID,
		static_cast<float>(Vector.X),
		static_cast<float>(Vector.Y),
		static_cast<float>(Vector.Z)
	);
}

template<typename T, JUN_IF_SAME((T), (FVector))>
JUNPLUGIN_API FVector J2U(jobject Vec3fObj)
{
	return FVector(
		GEnv->GetFloatField(Vec3fObj, GJunWrapperManager->Vec3fXID),
		GEnv->GetFloatField(Vec3fObj, GJunWrapperManager->Vec3fYID),
		GEnv->GetFloatField(Vec3fObj, GJunWrapperManager->Vec3fZID)
	);
}

template JUNPLUGIN_API FVector J2U<FVector>(jobject Vec3fObj);

JUNPLUGIN_API jobject U2J(float X, float Y, float Z, float W)
{
	return GEnv->NewObject(
		GJunWrapperManager->Vec4fClass,
		GJunWrapperManager->Vec4fConstructorID,
		X,
		Y,
		Z,
		W
	);
}

JUNPLUGIN_API jobject U2J(FVector4 Vector)
{
	return U2J(
		static_cast<float>(Vector.X),
		static_cast<float>(Vector.Y),
		static_cast<float>(Vector.Z),
		static_cast<float>(Vector.W)
	);
}

template<typename T, JUN_IF_SAME((T), (FVector4))>
JUNPLUGIN_API FVector4 J2U(jobject Vec4fObj)
{
	return FVector4(
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fXID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fYID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fZID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fWID)
	);
}

template JUNPLUGIN_API FVector4 J2U<FVector4>(jobject Vec4fObj);

JUNPLUGIN_API jobject U2J(FLinearColor Color)
{
	return U2J(
		Color.R,
		Color.G,
		Color.B,
		Color.A
	);
}

template<typename T, JUN_IF_SAME((T), (FLinearColor))>
JUNPLUGIN_API FLinearColor J2U(jobject Vec4fObj)
{
	return FLinearColor(
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fXID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fYID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fZID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fWID)
	);
}

template JUNPLUGIN_API FLinearColor J2U<FLinearColor>(jobject Vec4fObj);

JUNPLUGIN_API jobject U2J(FQuat Quat)
{
	return GEnv->NewObject(
		GJunWrapperManager->QuaternionClass,
		GJunWrapperManager->QuaternionConstructorID,
		U2J(
			static_cast<float>(Quat.X),
			static_cast<float>(Quat.Y),
			static_cast<float>(Quat.Z),
			static_cast<float>(Quat.W)
		)
	);
}

template<typename T, JUN_IF_SAME((T), (FQuat))>
JUNPLUGIN_API FQuat J2U(jobject QuaternionObj)
{
	jobject Vec4fObj = GEnv->GetObjectField(QuaternionObj, GJunWrapperManager->QuaternionVectorID);
	return FQuat(
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fXID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fYID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fZID),
		GEnv->GetFloatField(Vec4fObj, GJunWrapperManager->Vec4fWID)
	);
}

template JUNPLUGIN_API FQuat J2U<FQuat>(jobject QuaternionObj);

JUNPLUGIN_API jobject U2J(FTransform Transform)
{
	return GEnv->NewObject(
		GJunWrapperManager->Transform3fClass,
		GJunWrapperManager->Transform3fConstructorID,
		U2J(Transform.GetRotation()),
		U2J(Transform.GetTranslation()),
		U2J(Transform.GetScale3D())
	);
}

template<typename T, JUN_IF_SAME((T), (FTransform))>
JUNPLUGIN_API FTransform J2U(jobject Transform3fObj)
{
	return FTransform(
		J2U<FQuat>(GEnv->GetObjectField(Transform3fObj, GJunWrapperManager->Transform3fRotationID)),
		J2U<FVector>(GEnv->GetObjectField(Transform3fObj, GJunWrapperManager->Transform3fTranslationID)),
		J2U<FVector>(GEnv->GetObjectField(Transform3fObj, GJunWrapperManager->Transform3fScaleID))
	);
}

template JUNPLUGIN_API FTransform J2U<FTransform>(jobject Transform3fObj);

JUNPLUGIN_API jobject U2J(FIntRect Rect)
{
	return GEnv->NewObject(
		GJunWrapperManager->NonEmptyBoundsClass,
		GJunWrapperManager->NonEmptyBoundsConstructorID,
		U2J(Rect.Min),
		U2J(Rect.Max)
	);
}

template<typename T, JUN_IF_SAME((T), (FIntRect))>
JUNPLUGIN_API FIntRect J2U(jobject Bounds2iObj)
{
	jobject MinObj = GEnv->CallObjectMethod(Bounds2iObj, GJunWrapperManager->BoundsGetMinID);
	jobject MaxObj = GEnv->CallObjectMethod(Bounds2iObj, GJunWrapperManager->BoundsGetMaxID);
	jclass Vec2iClass = GJunWrapperManager->Vec2iClass;
	if (GEnv->IsInstanceOf(MinObj, Vec2iClass) && GEnv->IsInstanceOf(MaxObj, Vec2iClass))
	{
		return FIntRect(J2U<FIntPoint>(MinObj), J2U<FIntPoint>(MaxObj));
	}
	else
	{
		UE_LOG(LogJun, Error, TEXT("Vector type V of Bounds<V> must be non-null Vec2i to be converted to FIntRect"));
		return FIntRect();
	}
}

template JUNPLUGIN_API FIntRect J2U<FIntRect>(jobject Bounds2iObj);

JUNPLUGIN_API jobject U2J(FBox2D Box)
{
	return GEnv->NewObject(
		GJunWrapperManager->NonEmptyBoundsClass,
		GJunWrapperManager->NonEmptyBoundsConstructorID,
		U2J(Box.Min),
		U2J(Box.Max)
	);
}

template<typename T, JUN_IF_SAME((T), (FBox2D))>
JUNPLUGIN_API FBox2D J2U(jobject Bounds2fObj)
{
	jobject MinObj = GEnv->CallObjectMethod(Bounds2fObj, GJunWrapperManager->BoundsGetMinID);
	jobject MaxObj = GEnv->CallObjectMethod(Bounds2fObj, GJunWrapperManager->BoundsGetMaxID);
	jclass Vec2fClass = GJunWrapperManager->Vec2fClass;
	if (GEnv->IsInstanceOf(MinObj, Vec2fClass) && GEnv->IsInstanceOf(MaxObj, Vec2fClass))
	{
		return FBox2D(J2U<FVector2D>(MinObj), J2U<FVector2D>(MaxObj));
	}
	else
	{
		UE_LOG(LogJun, Error, TEXT("Vector type V of Bounds<V> must be non-null Vec2f to be converted to FBox2D"));
		return FBox2D();
	}
}

template JUNPLUGIN_API FBox2D J2U<FBox2D>(jobject Bounds2fObj);

JUNPLUGIN_API jobject U2J(FBox Box)
{
	return GEnv->NewObject(
		GJunWrapperManager->NonEmptyBoundsClass,
		GJunWrapperManager->NonEmptyBoundsConstructorID,
		U2J(Box.Min),
		U2J(Box.Max)
	);
}

template<typename T, JUN_IF_SAME((T), (FBox))>
JUNPLUGIN_API FBox J2U(jobject Bounds3fObj)
{
	jobject MinObj = GEnv->CallObjectMethod(Bounds3fObj, GJunWrapperManager->BoundsGetMinID);
	jobject MaxObj = GEnv->CallObjectMethod(Bounds3fObj, GJunWrapperManager->BoundsGetMaxID);
	jclass Vec3fClass = GJunWrapperManager->Vec3fClass;
	if (GEnv->IsInstanceOf(MinObj, Vec3fClass) && GEnv->IsInstanceOf(MaxObj, Vec3fClass))
	{
		return FBox(J2U<FVector>(MinObj), J2U<FVector>(MaxObj));
	}
	else
	{
		UE_LOG(LogJun, Error, TEXT("Vector type V of Bounds<V> must be non-null Vec3f to be converted to FBox"));
		return FBox();
	}
}

template JUNPLUGIN_API FBox J2U<FBox>(jobject Bounds3fObj);

JUNPLUGIN_API jobject U2J(jobject First, jobject Second)
{
	return GEnv->NewObject(GJunWrapperManager->PairClass, GJunWrapperManager->PairConstructorID, First, Second);
}


JUNPLUGIN_API jobject U2J(FBoxSphereBounds BoxSphereBounds)
{
	return GEnv->NewObject(
		GJunWrapperManager->FBoxSphereBoundsClass,
		GJunWrapperManager->FBoxSphereBoundsConstructorID,
		U2J(BoxSphereBounds.Origin),
		U2J(BoxSphereBounds.BoxExtent),
		static_cast<float>(BoxSphereBounds.SphereRadius)
	);
}

template<typename T, JUN_IF_SAME((T), (FBoxSphereBounds))>
JUNPLUGIN_API FBoxSphereBounds J2U(jobject BoxSphereBoundsObj)
{
	return FBoxSphereBounds(
		J2U<FVector>(GEnv->GetObjectField(BoxSphereBoundsObj, GJunWrapperManager->FBoxSphereBoundsOriginID)),
		J2U<FVector>(GEnv->GetObjectField(BoxSphereBoundsObj, GJunWrapperManager->FBoxSphereBoundsBoxExtentID)),
		GEnv->GetFloatField(BoxSphereBoundsObj, GJunWrapperManager->FBoxSphereBoundsSphereRadiusID)
	);
}

template JUNPLUGIN_API FBoxSphereBounds J2U<FBoxSphereBounds>(jobject BoxSphereBoundsObj);

JUNPLUGIN_API void ThrowJunExceptionForBufferCapacity(size_t BufferCapacity, size_t ElementSize, const TCHAR* ElementTypeName, JNIEnv* Env)
{
	GJunManager->ThrowException(*FString::Printf(
		TEXT("Buffer capacity %lld is no integer multitple of element size sizeof(%s) = %lld"),
		BufferCapacity,
		ElementTypeName,
		ElementSize
	), Env);
}
