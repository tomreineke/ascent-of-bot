package com.cerebrallychallenged.hypogean.graphics.gimp

import com.cerebrallychallenged.jun.math.geo.Bounds
import com.cerebrallychallenged.jun.math.geo.Vec2i

enum class MergeType(val code: String) {
    ExpandAsNecessary("EXPAND_AS_NECESSARY"),
    ClipToImage("CLIP_TO_IMAGE"),
    ClipToBottomLayer("CLIP_TO_BOTTOM_LAYER");

    override fun toString(): String = code
}

class GimpImage(gimp: Gimp) : GimpValue(gimp) {
    val layers: GimpList<GimpLayer>
        get() = GimpList(gimp, ::GimpLayer).also { append("$it = $this.layers") }

    val width: GimpInt
        get() = GimpInt(gimp).also { append("$it = $this.width") }

    val height: GimpInt
        get() = GimpInt(gimp).also { append("$it = $this.height") }

    fun insertChannel(channel: GimpChannel) {
        append("$this.insert_channel($channel)")
    }

    fun insertLayer(layer: GimpLayer) {
        append("$this.insert_layer($layer)")
    }

    fun removeLayer(layer: GimpLayer) {
        append("$this.remove_layer($layer)")
    }

    fun mergeVisibleLayers(mergeType: MergeType) {
        append("$this.merge_visible_layers($mergeType)")
    }
}

fun GimpImage.selectItem(channelOp: GimpChannelOp, item: GimpDrawable) {
    append("pdb.gimp_image_select_item($this, $channelOp, $item)")
}

fun GimpImage.selectionInvert() {
    append("pdb.gimp_selection_invert($this)")
}

fun GimpImage.selectionTranslate(x: GimpInt, y: GimpInt) {
    append("pdb.gimp_selection_translate($this, $x, $y)")
}

fun GimpImage.selectionTranslate(x: Int, y: Int) {
    selectionTranslate(x.g, y.g)
}

fun GimpImage.selectEllipse(channelOp: GimpChannelOp, x: GimpFloat, y: GimpFloat, width: GimpFloat, height: GimpFloat) {
    append("pdb.gimp_image_select_ellipse($this, $channelOp, $x, $y, $width, $height)")
}

fun GimpImage.selectEllipse(channelOp: GimpChannelOp, x: Double, y: Double, width: Double, height: Double) {
    selectEllipse(channelOp, x.g, y.g, width.g, height.g)
}

fun GimpImage.selectionNone() {
    append("pdb.gimp_selection_none($this)")
}

fun GimpImage.selectRectangle(channelOp: GimpChannelOp, x: GimpFloat, y: GimpFloat, width: GimpFloat, height: GimpFloat) {
    append("pdb.gimp_image_select_rectangle($this, $channelOp, $x, $y, $width, $height)")
}

fun GimpImage.selectRectangle(channelOp: GimpChannelOp, x: Double, y: Double, width: Double, height: Double) {
    selectRectangle(channelOp, x.g, y.g, width.g, height.g)
}

fun GimpImage.duplicate(): GimpImage = GimpImage(gimp).also { append("$it = pdb.gimp_image_duplicate($this)") }

enum class ScaleInterpolation(private val code: String) {
    None("INTERPOLATION_NONE"),
    Linear("INTERPOLATION_LINEAR"),
    Cubic("INTERPOLATION_CUBIC"),
    NoHalo("INTERPOLATION_NOHALO"),
    Lo("INTERPOLATION_LO");

    override fun toString(): String = code
}

fun GimpImage.scaleFull(newWidth: GimpInt, newHeight: GimpInt, interpolation: ScaleInterpolation) {
    append("pdb.gimp_image_scale_full($this, $newWidth, $newHeight, $interpolation)")
}

fun GimpImage.scaleFull(newWidth: Int, newHeight: Int, interpolation: ScaleInterpolation) {
    scaleFull(newWidth.g, newHeight.g, interpolation)
}

fun GimpImage.crop(newWidth: GimpInt, newHeight: GimpInt, offsetX: GimpInt, offsetY: GimpInt) {
    append("pdb.gimp_image_crop($this, $newWidth, $newHeight, $offsetX, $offsetY)")
}

fun GimpImage.crop(newWidth: Int, newHeight: Int, offsetX: Int, offsetY: Int) {
    crop(newWidth.g, newHeight.g, offsetX.g, offsetY.g)
}

fun GimpImage.crop(bounds: Bounds<Vec2i>) {
    val size = bounds.size
    val min = bounds.min
    crop(size.x, size.y, min.x, min.y)
}

fun GimpImage.lowerItem(item: GimpLayer) {
    append("pdb.gimp_image_lower_item($this, $item)")
}

fun GimpImage.raiseItem(item: GimpLayer) {
    append("pdb.gimp_image_raise_item($this, $item)")
}
