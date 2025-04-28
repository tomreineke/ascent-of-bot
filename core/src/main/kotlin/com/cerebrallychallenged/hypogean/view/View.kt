package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.jun.input.InputEvent

interface View {
    /**
     *
     * Executed in Unreal thread.
     */
    suspend fun onViewModelChange(change: ViewModelChange) {}

    /**
     *
     * Executed in Unreal thread.
     */
    fun onTick(deltaSeconds: Float) {}

    /**
     *
     * Executed in Unreal thread.
     */
    fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {}
}
