package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.unreal.widget.SWidget
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedPtr

open class UGameViewportClient(ptr: CPointer) : UObject(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass
    }

    fun addViewportWidgetContent(viewportContent: TSharedRef<SWidget>, zOrder: Int = 0) {
        addViewportWidgetContent(ptr, viewportContent.sharedPtrPtr, zOrder)
    }

    fun rebuildCursors() {
        rebuildCursors(ptr)
    }

    fun removeViewportWidgetContent(viewportContent: TSharedRef<SWidget>) {
        removeViewportWidgetContent(ptr, viewportContent.sharedPtrPtr)
    }

    fun setHardwareCursor(cursorShape: EMouseCursor, cursorName: String, hotspot: Vec2f) {
        setHardwareCursor(ptr, cursorShape.ordinal, cursorName, hotspot)
    }

    val window: TSharedPtr<SWindow>
        get() = getWindow(ptr).wrapSharedPtr()
}

private external fun addViewportWidgetContent(ptr: CPointer, viewportContentPtr: CPointer, zOrder: Int)

private external fun getWindow(ptr: CPointer): CPointer

private external fun rebuildCursors(ptr: CPointer)

private external fun removeViewportWidgetContent(ptr: CPointer, viewportContentPtr: CPointer)

private external fun setHardwareCursor(ptr: CPointer, cursorShape: Int, cursorName: String, hotspot: Vec2f)
