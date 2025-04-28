package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.unreal.widget.SWidget

sealed class FInputMode

data class FInputModeGameAndUI(
        val widgetToFocus: TSharedPtr<SWidget>,
        val mouseLockMode: EMouseLockMode,
        val hideCursorDuringCapture: Boolean
) : FInputMode()

object FInputModeGameOnly : FInputMode()

data class FInputModeUIOnly(val widgetToFocus: TSharedPtr<SWidget>, val mouseLockMode: EMouseLockMode) : FInputMode()
