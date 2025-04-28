#pragma once

#include "CoreMinimal.h"
#include "jni.h"

class JUNPLUGIN_API FJunClassLoader {
public:
	FJunClassLoader(jclass ClassLoaderClass, jobject ClassLoader);
	~FJunClassLoader();

	// returns nullptr if class is not found, is local ref
	jclass LoadClassNullableLocalRef(const char* ClassName) const;

	// logs fatal if class is not found, is local ref
	jclass LoadClassLocalRef(const char* ClassName) const;

	// logs fatal if class is not found, is global ref
	jclass LoadClassGlobalRef(const char* ClassName) const;

	jobject GetClassLoader() const;
private:
	jobject ClassLoader;
	jmethodID LoadClassID;
};