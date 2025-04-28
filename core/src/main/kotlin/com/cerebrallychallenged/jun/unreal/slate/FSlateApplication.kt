package com.cerebrallychallenged.jun.unreal.slate

import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.widget.SWidget
import com.cerebrallychallenged.jun.util.CPointer

object FSlateApplication {
    fun setUserFocus(
        userIndex: Int,
        widgetToFocusPtr: TSharedRef<SWidget>,
        reasonFocusIsChanging: EFocusCause
    ): Boolean = setUserFocus(userIndex, widgetToFocusPtr.sharedPtrPtr, reasonFocusIsChanging.ordinal.toByte())

    fun setUserFocusToGameViewport(userIndex: Int, reasonFocusIsChanging: EFocusCause) {
        setUserFocusToGameViewport(userIndex, reasonFocusIsChanging.ordinal.toByte())
    }
}

private external fun setUserFocus(userIndex: Int, widgetToFocusPtr: CPointer, reasonFocusIsChanging: Byte): Boolean

private external fun setUserFocusToGameViewport(userIndex: Int, reasonFocusIsChanging: Byte)
