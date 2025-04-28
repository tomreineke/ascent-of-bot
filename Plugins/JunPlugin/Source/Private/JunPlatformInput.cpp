#include "JunPlatformInput.h"
#include "Windows/AllowWindowsPlatformTypes.h"
#include "Windows/PreWindowsApi.h"
#include "Windows/MinWindows.h"
#include "Windows/PostWindowsApi.h"
#include "Windows/HideWindowsPlatformTypes.h"

bool JunIsLeftButtonDown()
{
	return (GetAsyncKeyState(VK_LBUTTON) & 0x8000) != 0;
}
