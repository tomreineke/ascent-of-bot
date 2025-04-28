package com.cerebrallychallenged.jun.skiatree

@JvmInline
value class InputState private constructor(internal val value: Int) {
    companion object {
        operator fun invoke(): InputState = Empty

        operator fun invoke(v0: InputState): InputState = v0

        operator fun invoke(v0: InputState, v1: InputState): InputState = InputState(v0.value or v1.value)

        operator fun invoke(v0: InputState, v1: InputState, v2: InputState): InputState =
            InputState(v0.value or v1.value or v2.value)

        val Empty = InputState(0)
        val Hovered = InputState(1)
        val Pressed = InputState(2)
        val Selected = InputState(4)

        val values = listOf(Hovered, Pressed, Selected)
    }

    operator fun contains(flag: InputState): Boolean = (value and flag.value) == flag.value

    operator fun plus(other: InputState): InputState = InputState(value or other.value)

    operator fun minus(other: InputState): InputState = InputState(value and other.value.inv())
}

private inline fun <T> Array<T>.orOf(f: (T) -> Int): Int {
    var result = 0
    for (value in this) {
        result = result or f(value)
    }
    return result
}

private val Array<out InputState>.aggregated: Int
    get() = orOf(InputState::value)

class InputStateMap<T> @PublishedApi internal constructor(
    private val array: Array<T?>,
    private val listener: ((InputState, T?) -> Unit)? = null
) {
    companion object {
        inline operator fun <reified T> invoke(
            noinline listener: ((InputState, T?) -> Unit)? = null
        ): InputStateMap<T> = InputStateMap(arrayOfNulls<T>(8), listener)
    }

    operator fun get(v0: InputState): T? = array[v0.value]

    operator fun get(v0: InputState, v1: InputState): T? = array[v0.value or v1.value]

    operator fun get(v0: InputState, v1: InputState, v2: InputState): T? = array[v0.value or v1.value or v2.value]

    operator fun set(v0: InputState, value: T?) {
        array[v0.value] = value
        listener?.invoke(v0, value)
    }

    operator fun set(v0: InputState, v1: InputState, value: T?) {
        this[v0 + v1] = value
    }

    operator fun set(v0: InputState, v1: InputState, v2: InputState, value: T?) {
        this[v0 + v1 + v2] = value
    }

    fun getFallback(inputState: InputState): T? {
        var state = inputState
        this[state]?.let { return it }
        for (value in InputState.values) {
            state -= value
            this[state]?.let { return it }
        }
        return null
    }
}
