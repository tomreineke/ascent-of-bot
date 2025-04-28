#pragma once

#include "IJunPlugin.h"

class FJunManager;

class JUNPLUGIN_API FJunManagerExtension {
protected:
	FJunManagerExtension(FJunManager& Manager);
public:
	virtual ~FJunManagerExtension() = default;
	virtual void RegisterNatives() = 0;
protected:
	template<auto fn>
	void RegisterNative(const char* ClassName, char* MethodName, char* Signature)
	{
		Manager.RegisterNative<fn>(ClassName, MethodName, Signature);
	}

	template<auto fn>
	void RegisterNativeWithEnv(const char* ClassName, char* MethodName, char* Signature)
	{
		Manager.RegisterNativeWithEnv<fn>(ClassName, MethodName, Signature);
	}

	FJunManager& Manager;
};

using FJunManagerExtensionProvider = TUniquePtr<FJunManagerExtension>(*)(FJunManager&);