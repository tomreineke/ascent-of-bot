@file:Suppress("UnstableApiUsage")

package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.coroutine.Unreal
import com.cerebrallychallenged.jun.unreal.FSoftObjectPtr
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.get
import com.cerebrallychallenged.jun.unreal.requestAsyncLoad
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AssetLibrary(val scope: CoroutineScope) {
    private val unrealObjects: MutableMap<UnrealRef<*>, UObject> = Object2ObjectOpenHashMap()

    private val unrealRequests: MutableMap<UnrealRef<*>, Deferred<UObject>> = Object2ObjectOpenHashMap()

    @PublishedApi
    internal suspend fun loadUnrealObject(path: UnrealRef<*>): UObject = withContext(Dispatchers.Unreal) {
        unrealObjects[path]?.let { return@withContext it }
        unrealRequests.computeIfAbsent(path) {
            async {
                suspendCancellableCoroutine { cont ->
                    val softObjectPtr = FSoftObjectPtr.makeShared(path.path)
                    softObjectPtr.requestAsyncLoad {
                        val obj = softObjectPtr.get()
                        if (obj != null) {
                            unrealObjects[path] = obj
                            // The result is actually returned by computeIfAbsent and subsequently used by the caller.
                            @Suppress("DeferredResultUnused")
                            unrealRequests.remove(path)
                            cont.resume(obj)
                        } else {
                            @Suppress("ThrowableNotThrown")
                            cont.resumeWithException(IOException("UObject is null after load"))
                        }
                    }
                }
            }
        }.await()
    }

    suspend inline fun <reified T : UObject> load(path: UnrealRef<T>): T = loadUnrealObject(path) as T
}
