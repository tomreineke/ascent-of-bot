package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapNullableUObject
import com.cerebrallychallenged.jun.wrapSharedRef

interface FSoftObjectPtr {
    companion object {
        fun makeShared(path: String): TSharedRef<FSoftObjectPtr> = makeSharedOfFSoftObjectPtr(path).wrapSharedRef()
    }
}

fun AnyRef<FSoftObjectPtr>.get(): UObject? = get(directPtr).wrapNullableUObject()

@Convenience
fun AnyRef<FSoftObjectPtr>.requestAsyncLoad(runnable: Runnable) {
    requestAsyncLoad(directPtr, runnable)
}

private external fun makeSharedOfFSoftObjectPtr(path: String): CPointer

private external fun get(directPtr: CPointer): CPointer

private external fun requestAsyncLoad(directPtr: CPointer, runnable: Runnable)