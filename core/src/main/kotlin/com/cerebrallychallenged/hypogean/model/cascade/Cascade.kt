package com.cerebrallychallenged.hypogean.model.cascade

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.attribute.IntProperty
import com.cerebrallychallenged.hypogean.util.collections.WorldStatisticRecorder
import it.unimi.dsi.fastutil.objects.ObjectArrayPriorityQueue
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.CoroutineContext

interface CascadeContext : WorldContext {
    fun launchSchedule(f: suspend context(CascadeContext) () -> Unit): Job

    suspend fun delay(time: Float)

    val isReal: Boolean
        get() = statisticRecorder == null

    val statisticRecorder: WorldStatisticRecorder<IntProperty>?
}

private class Cascade(
    override val world: World,
    override val statisticRecorder: WorldStatisticRecorder<IntProperty>?
) : CascadeContext {
    inner class Dispatcher : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            queue.enqueue(Phase(currentTime, block))
        }
    }

    private class Phase(val startTime: Float, val block: Runnable) : Comparable<Phase> {
        override fun compareTo(other: Phase): Int = startTime.compareTo(other.startTime)
    }

    private val scope = CoroutineScope(Dispatcher())

    private val queue = ObjectArrayPriorityQueue<Phase>()

    private var currentTime: Float = 0.0f
        set(value) {
            field = value
            if (isReal) {
                world.changes.currentTime = value
            }
        }

    override fun launchSchedule(f: suspend context(CascadeContext) () -> Unit): Job = scope.launch { f(this@Cascade) }

    override suspend fun delay(time: Float) {
        suspendCancellableCoroutine { cont ->
            queue.enqueue(Phase(currentTime + time) {
                cont.resumeWith(Result.success(Unit))
            })
        }
    }

    fun execute() {
        while (!queue.isEmpty) {
            val phase = queue.dequeue()
            val time = phase.startTime
            if (currentTime < time) {
                currentTime = time
            }
            phase.block.run()
        }
    }
}

interface CascadeBlock : CascadeContext {
    fun schedule(f: suspend context(CascadeBlock) () -> Unit)
}

private class CascadeBlockImplementation(context: CascadeContext): CascadeBlock, CascadeContext by context {
    private var jobs = mutableListOf<Job>()

    suspend fun join() {
        do {
            jobs.also { jobs = mutableListOf() }.joinAll()
        } while (jobs.isNotEmpty())
    }

    override fun schedule(f: suspend context(CascadeBlock) () -> Unit) {
        jobs.add(launchSchedule {
            f(this@CascadeBlockImplementation)
        })
    }
}

context(CascadeContext)
suspend fun <R> cascadeBlock(f: suspend CascadeBlock.() -> R): R {
    val block = CascadeBlockImplementation(this@CascadeContext)
    return block.f().also {
        block.join()
    }
}

abstract class EffectConsequence(
    val target: Entity,
    val causalChange: CausalChange
) : suspend context(CascadeBlock) () -> Unit {
    final override suspend fun invoke(block: CascadeBlock) {
        val statisticRecorder = block.statisticRecorder
        if (statisticRecorder != null) {
            intProperty?.let {
                statisticRecorder.record(target, it, causalChange.delta)
            }
        } else {
            with(block) {
                execute()
            }
        }
    }

    context(CascadeBlock)
    abstract suspend fun execute()

    open val intProperty: IntProperty?
        get() = null
}

context(WorldContext)
fun executeCascade(
    recorder: WorldStatisticRecorder<IntProperty>? = null,
    f: suspend context(CascadeContext) () -> Unit
) {
    Cascade(world, recorder).apply {
        launchSchedule(f)
        execute()
    }
}
