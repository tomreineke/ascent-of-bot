package com.cerebrallychallenged.jun

import com.cerebrallychallenged.jun.coroutine.Unreal
import com.cerebrallychallenged.jun.input.InputManager
import com.cerebrallychallenged.jun.input.Key
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.unreal.AActor
import com.cerebrallychallenged.jun.unreal.TSharedPtr
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.UClass
import com.cerebrallychallenged.jun.unreal.UEngine
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.UWorld
import com.cerebrallychallenged.jun.unreal.getClass
import com.cerebrallychallenged.jun.unreal.getName
import com.cerebrallychallenged.jun.unreal.getSuperClass
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.CPointer2CPointerOpenHashMap
import com.cerebrallychallenged.jun.util.CPointer2ObjectOpenHashMap
import com.cerebrallychallenged.jun.util.CPointerOpenHashSet
import com.cerebrallychallenged.jun.util.isNotNull
import com.cerebrallychallenged.jun.util.isNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.jctools.queues.MpscLinkedQueue8
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.net.URLClassLoader
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

private const val RUNNABLE_FRAME_LIMIT_NANOSECONDS = 10_000_000

private const val RUNNABLE_BATCH_SIZE = 4096

object JunManager {
    private val extensions: List<JunManagerExtension> = extensionClasses.mapNotNull { it.objectInstance }

    private val unrealGameThread = Thread.currentThread()

    private val unrealClasses: List<UClass>

    // UObject* to instance of wrapper class.
    private val unrealObjects: CPointer2ObjectOpenHashMap<WeakReference<UObject>>

    // UClass* to constructor of wrapper class.
    private val unrealWrapperFactories: CPointer2ObjectOpenHashMap<(CPointer) -> UObject>

    private val sharedRefPointers = CPointerOpenHashSet()

    internal val garbageCollectedObjects = ReferenceQueue<Any>()

    private val pendingRunnables = MpscLinkedQueue8<Runnable>()

    init {
        val wrapperIncubator = WrapperIncubator(extensions)
        registerClasses(wrapperIncubator)
        wrapperIncubator.resolve()
        unrealClasses = wrapperIncubator.unrealClasses
        unrealObjects = wrapperIncubator.unrealObjects
        unrealWrapperFactories = wrapperIncubator.unrealWrapperFactories

        Thread.setDefaultUncaughtExceptionHandler(::logException)
    }

    private val scopeGuards = CPointer2CPointerOpenHashMap()

    private fun findFactory(ptr: CPointer): (CPointer) -> UObject {
        var classPtr = getClass(ptr)
        while (classPtr.isNotNull()) {
            unrealWrapperFactories[classPtr]?.let { return it }
            classPtr = getSuperClass(classPtr)
        }
        throw RuntimeException("Cannot determine wrapper factory for object ${getName(ptr)}")
    }

    /**
     * Find or create wrapper for the specified `UObject*`.
     */
    @PublishedApi
    internal fun wrapUObject(ptr: CPointer): UObject? {
        if (ptr.isNull()) return null
        return unrealObjects[ptr]?.get() ?: run {
            log.trace { "wrapUObject(${getName(ptr)})" }
            findFactory(ptr)(ptr).also {
                unrealObjects[ptr] = WeakReference(it)
                scopeGuards[ptr] = newScopeGuard(ptr)
                JunWeakReference(it) { closeUObject(ptr) }
            }
        }
    }

    internal fun closeUObject(ptr: CPointer) {
        log.trace { "closeUObject(${getName(ptr)})" }
        unrealObjects.remove(ptr)
        val scopeGuardPtr = scopeGuards.remove(ptr)
        if (scopeGuardPtr.isNotNull()) {
            deleteScopeGuard(scopeGuardPtr)
        }
    }

    internal fun <T> wrapSharedPtr(sharedPtrPtr: CPointer): TSharedPtr<T> {
        log.trace { "wrapSharedPtr($sharedPtrPtr)" }
        if (sharedPtrPtr.isNull()) return null
        val ephemeral = LifeTime()
        return TSharedRef<T>(sharedPtrPtr, ephemeral).also {
            JunWeakReference(it) {
                ephemeral.close()
                closeSharedRef(sharedPtrPtr)
            }
        }
    }

    internal fun closeSharedRef(sharedRefPtr: CPointer) {
        log.trace { "closeSharedRef($sharedRefPtr)" }
        if (sharedRefPointers.remove(sharedRefPtr)) {
            deleteSharedRef(sharedRefPtr)
        }
    }

    private fun clearGarbageCollectedObjects() {
        for (reference in generateSequence(garbageCollectedObjects::poll)) {
            (reference as JunWeakReference).close()
        }
    }

    private fun runPendingRunnables() {
        // At this point, we could throttle the execution,
        // e.g., execute in a batch of 10000 runnables and measure the time.
        val startTime = System.nanoTime()
        var actualCount: Int
        do {
            actualCount = pendingRunnables.drain({ it.run() }, RUNNABLE_BATCH_SIZE)
        } while (actualCount == RUNNABLE_BATCH_SIZE && System.nanoTime() - startTime < RUNNABLE_FRAME_LIMIT_NANOSECONDS)
    }

    val GWorld: UWorld = getWorld().wrapUObject()

    val defaultActor: AActor = getDefaultActor().wrapUObject()

    val GEngine: UEngine = getEngine().wrapUObject()

    lateinit var inputManager: InputManager
        private set

    private var application: Job? = null

    @Suppress("unused") // Called from JNI.
    @JvmStatic
    fun onBeginPlay(applicationFactoryClassName: String, classLoader: URLClassLoader) {
        JunClassLoader.classLoader = classLoader
        if (applicationFactoryClassName.isEmpty()) return
        val clazz = Class.forName(applicationFactoryClassName, true, classLoader).kotlin
        require(clazz.isSubclassOf(JunApplicationFactory::class))
        val factory
                = clazz.objectInstance as? JunApplicationFactory
                ?: error("Application factory class must be singleton object.")
        application = GlobalScope.launch(Dispatchers.Unreal) {
            inputManager = InputManager()
            factory.create()
            log.info("About to quit")
            quitGame()
        }
    }

    @Suppress("unused") // Called from JNI.
    @JvmStatic
    fun onTick(deltaSeconds: Float) {
        clearGarbageCollectedObjects()
        if (::inputManager.isInitialized) {
            inputManager.onTick()
        }
        TickerManager.runTickers(deltaSeconds)
        runPendingRunnables()
    }

    @Suppress("unused") // Called from JNI.
    @JvmStatic
    fun onEndPlay() {
        for (extension in extensions) {
            extension.onEndPlay()
        }
        application?.let {
            it.cancel("onEndPlay")
            application = null
        }
        scopeGuards.forEachValue {
            deleteScopeGuard(it)
        }
        scopeGuards.clear()
        sharedRefPointers.forEachValue {
            deleteSharedRef(it)
        }
        sharedRefPointers.clear()
        pendingRunnables.clear()
    }

    @Suppress("unused") // Called from JNI.
    @JvmStatic
    private fun onKeyPressed(keyIndex: Int) {
        if (::inputManager.isInitialized) {
            inputManager.onKeyPressed(Key[keyIndex])
        }
    }

    @Suppress("unused") // Called from JNI.
    @JvmStatic
    private fun onKeyReleased(keyIndex: Int) {
        if (::inputManager.isInitialized) {
            inputManager.onKeyReleased(Key[keyIndex])
        }
    }

    fun runLater(runnable: Runnable) {
        pendingRunnables.add(runnable)
    }

    inline fun runLater(crossinline block: () -> Unit) {
        runLater(Runnable(block))
    }

    fun isUnrealGameThread(): Boolean = Thread.currentThread() === unrealGameThread

    private fun logException(thread: Thread, throwable: Throwable) {
        val stackTrace = StringWriter()
        throwable.printStackTrace(PrintWriter(stackTrace))
        log.error("Exception occurred in thread $thread: $throwable\n$stackTrace")
    }

    external fun quitGame()
}

inline fun <reified T : UObject> CPointer.wrapUObject(): T
        = (
            JunManager.wrapUObject(this)
            ?: throw JunException("Unexpected nullptr of ${T::class.simpleName}*")
        ) as T

inline fun <reified T : UObject> CPointer.wrapNullableUObject(): T?
        = JunManager.wrapUObject(this)?.let { it as T }

fun <T> CPointer.wrapSharedRef(): TSharedRef<T>
        = JunManager.wrapSharedPtr(this) ?: throw JunException("Unexpected nullptr of TSharedRef<>")

fun <T> CPointer.wrapSharedPtr(): TSharedPtr<T>
        = JunManager.wrapSharedPtr(this)

private external fun newScopeGuard(unrealObjectPtr: CPointer): CPointer

private external fun deleteScopeGuard(scopeGuardPtr: CPointer)

private external fun deleteSharedRef(sharedRefPtr: CPointer)

/**
 * Calls [WrapperIncubator.registerClass] for each UClass present in the Unreal Engine.
 */
private external fun registerClasses(wrapperIncubator: WrapperIncubator)

private external fun getWorld(): CPointer

private external fun getDefaultActor(): CPointer

private external fun getEngine(): CPointer

private val extensionClasses = mutableListOf<KClass<out JunManagerExtension>>()

fun registerExtensionClass(extensionClass: Class<out JunManagerExtension>) {
    extensionClasses.add(extensionClass.kotlin)
}
