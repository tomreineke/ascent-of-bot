package com.cerebrallychallenged.jun.unreal.widget

import com.cerebrallychallenged.jun.math.geo.Vec4f
import com.cerebrallychallenged.jun.unreal.AnyRef
import com.cerebrallychallenged.jun.unreal.TSharedRef
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import com.cerebrallychallenged.jun.unreal.font.FSlateFontInfo
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapSharedRef

interface STextBlock : SLeafWidget {
    companion object {
        fun createBySNew(): TSharedRef<STextBlock> = createBySNewImpl().wrapSharedRef()
    }
}

var AnyRef<STextBlock>.colorAndOpacity: FLinearColor
    @Deprecated("Property is write only")
    get() = error("Property is write only")
    set(value) {
        setColorAndOpacity(directPtr, value.rgba)
    }

var AnyRef<STextBlock>.font: TSharedRef<FSlateFontInfo>
    @Deprecated("Property is write only")
    get() = error("Property is write only")
    set(value) {
        setFont(directPtr, value.sharedPtrPtr)
    }

var AnyRef<STextBlock>.justification: ETextJustify
    @Deprecated("Property is write only")
    get() = error("Property is write only")
    set(value) {
        setJustification(directPtr, value.ordinal)
    }

var AnyRef<STextBlock>.lineHeightPercentage: Double
    @Deprecated("Property is write only")
    get() = error("Property is write only")
    set(value) {
        setLineHeightPercentage(directPtr, value.toFloat())
    }

var AnyRef<STextBlock>.text: String
    get() = getText(directPtr)
    set(value) {
        setText(directPtr, value)
    }

private external fun createBySNewImpl(): CPointer

private external fun getText(directPtr: CPointer): String

private external fun setColorAndOpacity(directPtr: CPointer, color: Vec4f)

private external fun setFont(directPtr: CPointer, fontPtr: CPointer)

private external fun setJustification(directPtr: CPointer, justification: Int)

private external fun setLineHeightPercentage(directPtr: CPointer, lineHeightPercentage: Float)

private external fun setText(directPtr: CPointer, text: String)