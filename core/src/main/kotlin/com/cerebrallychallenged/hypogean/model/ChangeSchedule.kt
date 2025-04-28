package com.cerebrallychallenged.hypogean.model

import com.cerebrallychallenged.hypogean.util.kryo.WorldKryo
import com.cerebrallychallenged.jun.log.log
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import kotlinx.coroutines.CompletableDeferred
import kotlin.math.max

class ChangeSchedule(private val kryo: WorldKryo) {
    var currentTime: Float = 0.0f
        set(value) {
            if (value < field) {
                throw ModelException("Time must monotonically increase for ChangeSchedule")
            }
            if (currentList.isNotEmpty()) {
                lists.add(field to flushCurrentListToDto())
                currentList = mutableListOf()
            }
            field = value
        }

    var endTime: Float = 0.0f
        private set

    fun addChange(change: WorldChange) {
        endTime = max(endTime, currentTime + change.duration)
        currentList.add(change)
    }

    private var currentList: MutableList<WorldChange> = mutableListOf()

    private val lists: ArrayDeque<Pair<Float, List<ByteArray>>> = ArrayDeque()

    fun flushCurrentList(): List<WorldChange> = currentList.also { currentList = mutableListOf() }

    private fun flushCurrentListToDto(): List<ByteArray> =
        flushCurrentList().map { kryo.serializeToByteArray(it.toDto()) }

    internal fun toDto(): ChangeScheduleDto {
        if (currentList.isNotEmpty()) {
            lists.add(currentTime to flushCurrentListToDto())
        }
        return ChangeScheduleDto(lists, endTime)
    }
}

class ChangeScheduleDto internal constructor(
    internal val lists: ArrayDeque<Pair<Float, List<ByteArray>>>,
    val endTime: Float
) {
    fun isEmpty(): Boolean = lists.isEmpty()

    internal fun dequeueUpTo(time: Float): List<ByteArray>? {
        val (startTime, list) = lists.firstOrNull() ?: return null
        return if (startTime <= time) {
            lists.removeFirst()
            list
        } else {
            null
        }
    }

    fun isFinished(time: Float): Boolean = endTime <= time
}

class ChangeScheduleAnimation(
    val schedule: ChangeScheduleDto,
    private val world: World,
    private val onChanges: (List<WorldChange>) -> Unit
) {
    var animationTime: Float = 0.0f

    private val completed = CompletableDeferred<Unit>()

    private val pausingTokens: MutableSet<Any> = ObjectArraySet()

    private val animations: MutableList<Animation> = mutableListOf()

    fun addAnimation(animation: Animation) {
        animations.add(animation)
    }

    fun onTick(deltaSeconds: Float): Unit = with(world) {
        if (pausingTokens.isEmpty()) {
            val kryo = world.kryo
            animationTime += deltaSeconds
            for (changes in generateSequence { schedule.dequeueUpTo(animationTime) }) {
                for (change in changes) {
                    try {
                        kryo.deserializeFromByteArray<WorldChangeDto>(change).applyChange()
                    } catch (e: Exception) {
                        // World changes like WorldChange$ReconChanged and WorldChange$AttributeChanged
                        // seem to not be saved correctly. When loading a save game, we'll just ignore
                        // these issues in order to avoid a deep dive.
                        log.warn {
                            "Loading issue: Applying a change in ChangeScheduleAnimation.onTick() fails " +
                                    "with message: ${e.message}"
                        }
                    }
                }
                onChanges(world.changes.flushCurrentList())
            }
            animations.removeIf {
                it.tick(deltaSeconds)
            }
            if (schedule.isFinished(animationTime) && animations.isEmpty()) {
                completed.complete(Unit)
            }
        }
    }

    fun pauseAnimation(token: Any) {
        pausingTokens.add(token)
    }

    fun resumeAnimation(token: Any) {
        pausingTokens.remove(token)
    }

    suspend fun await() {
        completed.await()
    }
}

