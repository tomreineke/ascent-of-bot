#pragma once

#include "CoreMinimal.h"

#include <type_traits>
#include <typeinfo>
#include <vector>

// Temporary construct until C++20 is available.
#include "span.hpp"
namespace std {
	using tcb::span;
}

#include "Brushes/SlateImageBrush.h"
#include "Slate/DeferredCleanupSlateBrush.h"
#include "Templates/SharedPointerInternals.h"
#include "UObject/GCObjectScopeGuard.h"
#include "Widgets/Images/SImage.h"
#include "Widgets/Input/SButton.h" 
#include "Widgets/Layout/SBox.h"
#include "Widgets/SCanvas.h"
#include "Widgets/Text/STextBlock.h" 

#include "jni.h"

#include "JunConv.h"
#include "JunClassLoader.h"
#include "JunToolTipWidget.h"

class JUNPLUGIN_API FJunWrapperManager
{
public:
	FJunWrapperManager(UWorld* World, TSharedPtr<FJunClassLoader> ClassLoader);
	~FJunWrapperManager();
private:
	UWorld* World;
	TSharedPtr<FJunClassLoader> ClassLoader;
public:
	jclass Vec2iClass;
	jmethodID Vec2iConstructorID;
	jfieldID Vec2iXID;
	jfieldID Vec2iYID;

	jclass Vec2fClass;
	jmethodID Vec2fConstructorID;
	jfieldID Vec2fXID;
	jfieldID Vec2fYID;

	jclass Vec3fClass;
	jmethodID Vec3fConstructorID;
	jfieldID Vec3fXID;
	jfieldID Vec3fYID;
	jfieldID Vec3fZID;

	jclass Vec4fClass;
	jmethodID Vec4fConstructorID;
	jfieldID Vec4fXID;
	jfieldID Vec4fYID;
	jfieldID Vec4fZID;
	jfieldID Vec4fWID;

	jclass QuaternionClass;
	jmethodID QuaternionConstructorID;
	jfieldID QuaternionVectorID;

	jclass Transform3fClass;
	jmethodID Transform3fConstructorID;
	jfieldID Transform3fRotationID;
	jfieldID Transform3fTranslationID;
	jfieldID Transform3fScaleID;

	jclass BoundsClass;
	jmethodID BoundsGetMinID;
	jmethodID BoundsGetMaxID;
	
	jclass NonEmptyBoundsClass;
	jmethodID NonEmptyBoundsConstructorID;

	jclass PairClass;
	jmethodID PairConstructorID;

	jclass FBoxSphereBoundsClass;
	jmethodID FBoxSphereBoundsConstructorID;
	jfieldID FBoxSphereBoundsOriginID;
	jfieldID FBoxSphereBoundsBoxExtentID;
	jfieldID FBoxSphereBoundsSphereRadiusID;

	jobject ByteOrderLittleEndian;

	jclass ByteBufferClass;
	jmethodID ByteBufferAllocateDirectID;
	jmethodID ByteBufferOrderID;

	jobject CreateByteBuffer(size_t Capacity, JNIEnv* Env);
};

JUNPLUGIN_API jobject U2J(int32 X, int32 Y);

JUNPLUGIN_API jobject U2J(FIntPoint Value);

template<typename T, JUN_IF_SAME((T), (FIntPoint)) = 0>
JUNPLUGIN_API FIntPoint J2U(jobject Vec2iObj);

JUNPLUGIN_API jobject U2J(FVector2D Vector);

template<typename T, JUN_IF_SAME((T), (FVector2D)) = 0>
JUNPLUGIN_API FVector2D J2U(jobject Vec2fObj);

JUNPLUGIN_API jobject U2J(FVector Vector);

template<typename T, JUN_IF_SAME((T), (FVector)) = 0>
JUNPLUGIN_API FVector J2U(jobject Vec3fObj);

JUNPLUGIN_API jobject U2J(float X, float Y, float Z, float W);

JUNPLUGIN_API jobject U2J(FVector4 Vector);

template<typename T, JUN_IF_SAME((T), (FVector4)) = 0>
JUNPLUGIN_API FVector4 J2U(jobject Vec4fObj);

JUNPLUGIN_API jobject U2J(FLinearColor Color);

template<typename T, JUN_IF_SAME((T), (FLinearColor)) = 0>
JUNPLUGIN_API FLinearColor J2U(jobject Vec4fObj);

JUNPLUGIN_API jobject U2J(FQuat Quat);

template<typename T, JUN_IF_SAME((T), (FQuat)) = 0>
JUNPLUGIN_API FQuat J2U(jobject QuaternionObj);

JUNPLUGIN_API jobject U2J(FTransform Transform);

template<typename T, JUN_IF_SAME((T), (FTransform)) = 0>
JUNPLUGIN_API FTransform J2U(jobject Transform3fObj);

JUNPLUGIN_API jobject U2J(FIntRect Rect);

template<typename T, JUN_IF_SAME((T), (FIntRect)) = 0>
JUNPLUGIN_API FIntRect J2U(jobject Bounds2iObj);

JUNPLUGIN_API jobject U2J(FBox2D Box);

template<typename T, JUN_IF_SAME((T), (FBox2D)) = 0>
JUNPLUGIN_API FBox2D J2U(jobject Bounds2fObj);

JUNPLUGIN_API jobject U2J(FBox Box);

template<typename T, JUN_IF_SAME((T), (FBox)) = 0>
JUNPLUGIN_API FBox J2U(jobject Bounds3fObj);

JUNPLUGIN_API jobject U2J(jobject First, jobject Second);

JUNPLUGIN_API jobject U2J(FBoxSphereBounds BoxSphereBounds);

template<typename T, JUN_IF_SAME((T), (FBoxSphereBounds)) = 0>
JUNPLUGIN_API FBoxSphereBounds J2U(jobject BoxSphereBoundsObj);

JUNPLUGIN_API void ThrowJunExceptionForBufferCapacity(size_t BufferCapacity, size_t ElementSize, const TCHAR* ElementTypeName, JNIEnv* Env);

template<typename ElementType>
inline jobject U2J(const TArray<ElementType>& Array, JNIEnv* Env = GEnv)
{
	size_t Capacity = Array.Num() * sizeof(ElementType);
	jobject ByteBuffer = GJunWrapperManager->CreateByteBuffer(Capacity, Env);
	void* BufferAddress = Env->GetDirectBufferAddress(ByteBuffer);
	FMemory::Memcpy(BufferAddress, Array.GetData(), Capacity);
	return ByteBuffer;
}

template<typename T, JUN_IF_SAME((T), (TArray<typename T::ElementType>)) = 0>
inline T J2U(jobject Buffer, JNIEnv* Env = GEnv)
{
	using ElementType = typename T::ElementType;
	ElementType* Address = static_cast<ElementType*>(Env->GetDirectBufferAddress(Buffer));
	size_t Capacity = Env->GetDirectBufferCapacity(Buffer);
	constexpr size_t ElementSize = sizeof(ElementType);
	size_t Count = Capacity / ElementSize;
	if (Count * ElementSize != Capacity)
	{
		/*GJunManager->ThrowException(*FString::Printf(
			TEXT("Buffer capacity %lld is no integer multitple of element size sizeof(%s) = %lld"),
			Capacity,
			UTF8_TO_TCHAR(typeid(ElementType).name()),
			ElementSize
		));*/
		ThrowJunExceptionForBufferCapacity(Capacity, ElementSize, UTF8_TO_TCHAR(typeid(ElementType).name()), Env);
		return TArray<ElementType>();
	}
	else
	{
		return TArray<ElementType>(Address, Count);
	}
}

template<typename ElementType>
inline jobject U2J(const std::vector<ElementType>& Array, JNIEnv* Env = GEnv)
{
	size_t Capacity = Array.size() * sizeof(ElementType);
	jobject ByteBuffer = GJunWrapperManager->CreateByteBuffer(Capacity, Env);
	void* BufferAddress = Env->GetDirectBufferAddress(ByteBuffer);
	FMemory::Memcpy(BufferAddress, Array.data(), Capacity);
	return ByteBuffer;
}

template<typename T, JUN_IF_SAME((T), (std::vector<typename T::value_type>)) = 0>
inline T J2U(jobject Buffer, JNIEnv* Env = GEnv)
{
	using ElementType = typename T::value_type;
	ElementType* Address = static_cast<ElementType*>(Env->GetDirectBufferAddress(Buffer));
	size_t Capacity = Env->GetDirectBufferCapacity(Buffer);
	constexpr size_t ElementSize = sizeof(ElementType);
	size_t Count = Capacity / ElementSize;
	if (Count * ElementSize != Capacity)
	{
		ThrowJunExceptionForBufferCapacity(Capacity, ElementSize, UTF8_TO_TCHAR(typeid(ElementType).name()), Env);
		return std::vector<ElementType>();
	}
	else
	{
		std::vector<ElementType> Result(Count);
		for (int I = 0; I < Count; ++I)
		{
			Result[I] = Address[I];
		}
		return Result;
	}
}

template<typename ElementType>
inline jobject U2J(const std::span<ElementType>& Span, JNIEnv* Env = GEnv)
{
	size_t Capacity = Span.size() * sizeof(ElementType);
	jobject ByteBuffer = GJunWrapperManager->CreateByteBuffer(Capacity, Env);
	void* BufferAddress = Env->GetDirectBufferAddress(ByteBuffer);
	FMemory::Memcpy(BufferAddress, Span.data(), Capacity);
	return ByteBuffer;
}

template<typename T, JUN_IF_SAME((T), (std::span<typename T::value_type>)) = 0>
inline T J2U(jobject Buffer, JNIEnv* Env = GEnv)
{
	using ElementType = typename T::value_type;
	ElementType* Address = static_cast<ElementType*>(Env->GetDirectBufferAddress(Buffer));
	size_t Capacity = Env->GetDirectBufferCapacity(Buffer);
	constexpr size_t ElementSize = sizeof(ElementType);
	size_t Count = Capacity / ElementSize;
	if (Count * ElementSize != Capacity)
	{
		ThrowJunExceptionForBufferCapacity(Capacity, ElementSize, UTF8_TO_TCHAR(typeid(ElementType).name()), Env);
		return std::span<ElementType>();
	}
	else
	{
		return std::span(Address, Count);
	}
}

extern JUNPLUGIN_API FJunWrapperManager* GJunWrapperManager;