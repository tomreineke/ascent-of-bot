#pragma once

#include "EngineMinimal.h"
#include "Misc/ScopeRWLock.h"

class FJunManager;

class JUNPLUGIN_API FJunLifeGuard {
public:
	FJunLifeGuard(FJunManager* Manager);
	void Close();
	template<typename T> void ExecuteIfOpen(T&& callable)
	{
		FRWScopeLock ReadLock(RWLock, FRWScopeLockType::SLT_ReadOnly);
		if (bOpen)
		{
			callable(Manager);
		}
	}
private:
	FJunManager* Manager;
	FThreadSafeBool bOpen;
	FRWLock RWLock;
};
