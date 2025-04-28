package com.cerebrallychallenged.jun.coroutine

import com.cerebrallychallenged.jun.JunManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

/**
 * Dispatches execution onto Unreal game thread.
 */
@Suppress("unused")
val Dispatchers.Unreal: UnrealDispatcher
    get() = com.cerebrallychallenged.jun.coroutine.Unreal

sealed class UnrealDispatcher : MainCoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) = JunManager.runLater(block)
}

private object ImmediateUnrealDispatcher : UnrealDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = this

    @ExperimentalCoroutinesApi
    override fun isDispatchNeeded(context: CoroutineContext): Boolean = !JunManager.isUnrealGameThread()

    override fun toString() = "Unreal [immediate]"
}

/**
 * Dispatches execution onto Unreal game thread.
 */
internal object Unreal : UnrealDispatcher() {
    override val immediate: MainCoroutineDispatcher
        get() = ImmediateUnrealDispatcher

    override fun toString() = "Unreal"
}