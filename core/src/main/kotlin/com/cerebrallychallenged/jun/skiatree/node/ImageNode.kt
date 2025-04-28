package com.cerebrallychallenged.jun.skiatree.node

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.applyStyle
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.Background
import com.cerebrallychallenged.jun.skiatree.InputState
import com.cerebrallychallenged.jun.skiatree.SkiaImage
import com.cerebrallychallenged.jun.skiatree.geo.IRect
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

open class ImageNode(image: SkiaImage? = null) : Node() {
    operator fun get(inputState: InputState): SkiaImage? = (background[inputState] as? Background.Image)?.image

    operator fun set(inputState: InputState, image: SkiaImage?) {
        background[inputState] = if (image != null) {
            Background.Image(image, IRect.Empty)
        } else {
            Background.Empty
        }
    }

    var image: SkiaImage?
        get() = this[InputState.Empty]
        set(value) {
            this[InputState.Empty] = value
            val size = image?.size ?: Vec2i.ZERO
            minSize = size
            maxSize = size
        }

    init {
        if (image != null) {
            this.image = image
        }
    }
}

inline fun Node.imageNode(
        style: Styling<ImageNode, Unit>? = null,
        image: SkiaImage? = null,
        f: ImageNode.() -> Unit = {}
): ImageNode {
    contract {
        callsInPlace(f, InvocationKind.EXACTLY_ONCE)
    }
    return ImageNode(image).also {
        if (style != null) {
            it.applyStyle(style)
        }
        children.add(it)
        it.f()
    }
}
