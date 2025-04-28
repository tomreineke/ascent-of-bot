#pragma once

#include <jni.h>

#include "IJunPlugin.h"
#include "JunClassLoader.h"

extern JUNPLUGIN_API JNIEnv* GlobalEnv;

#ifdef WITH_EDITOR
inline JNIEnv* GetGlobalEnv()
{
    if (GlobalEnv == nullptr) {
        GlobalEnv = IJunPlugin::Get().GetEnv();
    }
    return GlobalEnv;
}
#define GEnv GetGlobalEnv()
#else
#define GEnv GlobalEnv
#endif // WITH_EDITOR

template<auto fn>
struct AugmentJNIParameters;

template<typename R, typename... Args, R(*fn)(Args...)>
struct AugmentJNIParameters<fn>
{
    static R augmented(JNIEnv*, jclass, Args... args)
    {
        return fn(args...);
    }
};

template<auto fn>
struct AugmentJNIParametersWithEnv;

template<typename R, typename... Args, R(*fn)(JNIEnv*, Args...)>
struct AugmentJNIParametersWithEnv<fn>
{
    static R augmented(JNIEnv* Env, jclass, Args... args)
    {
        return fn(Env, args...);
    }
};

template<typename T>
T JunThrow(const char* Message)
{
    jclass ExceptionClass = GEnv->FindClass("java/lang/Exception");
    GEnv->ThrowNew(ExceptionClass, Message);
    return T();
}
