#include "JunLifeGuard.h"

FJunLifeGuard::FJunLifeGuard(FJunManager* Manager) : Manager(Manager), bOpen(true)
{
}

void FJunLifeGuard::Close()
{
	FRWScopeLock WriteLock(RWLock, FRWScopeLockType::SLT_Write);
	Manager = nullptr;
	bOpen = false;
}
