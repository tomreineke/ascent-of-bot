#pragma once

#include "CoreMinimal.h"

class JUNPLUGIN_API FJunSharedRef
{
public:
    virtual ~FJunSharedRef() = default;

	virtual void* GetDirect() = 0;
};

template<typename ElementType, ESPMode Mode = ESPMode::Fast>
class TJunSharedRef : public FJunSharedRef
{
public:
	TJunSharedRef(TSharedRef<ElementType, Mode> SharedRef) : SharedRef(SharedRef) {}

	TSharedRef<ElementType, Mode>& GetSharedRef()
	{
		return SharedRef;
	}

	void* GetDirect() override {
		return &SharedRef.Get();
	}
private:
	TSharedRef<ElementType, Mode> SharedRef;
};

template<typename ElementType, ESPMode Mode = ESPMode::Fast>
inline FJunSharedRef* U2J(TSharedRef<ElementType, Mode> SharedRef)
{
	return new TJunSharedRef<ElementType, Mode>(SharedRef);
}

template<typename T, ESPMode Mode = ESPMode::Fast, JUN_IF_SAME((T), (TSharedRef<typename T::ElementType, Mode>)) = 0>
inline TSharedRef<typename T::ElementType, Mode>& J2U(FJunSharedRef* Ptr)
{
	check(Ptr != nullptr);
	return static_cast<TJunSharedRef<typename T::ElementType, Mode>*>(Ptr)->GetSharedRef();
}

template<typename ElementType, ESPMode Mode = ESPMode::Fast>
inline FJunSharedRef* U2J(TSharedPtr<ElementType, Mode> SharedPtr)
{
	if (SharedPtr.IsValid())
	{
		return U2J(SharedPtr.ToSharedRef());
	}
	else
	{
		return nullptr;
	}
}

template<typename T, ESPMode Mode = ESPMode::Fast, JUN_IF_SAME((T), (TSharedPtr<typename T::ElementType, Mode>)) = 0>
inline TSharedPtr<typename T::ElementType, Mode> J2U(FJunSharedRef* Ptr)
{
	if (Ptr != nullptr)
	{
		return J2U<TSharedRef<typename T::ElementType, Mode>>(Ptr);
	}
	else
	{
		return TSharedPtr<typename T::ElementType, Mode>();
	}
}