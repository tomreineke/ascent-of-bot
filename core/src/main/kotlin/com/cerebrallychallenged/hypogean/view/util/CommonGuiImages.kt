package com.cerebrallychallenged.hypogean.view.util

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.NinePatchResource
import com.cerebrallychallenged.jun.skiatree.geo.IRect

object CommonGuiImages {
    val ViewFrameNinePatch = NinePatchResource(
        ImageResource("Images/gui/view_frame.png"),
        IRect(10, 5, 5, 8),
        IRect(560, 425, 561, 426)
    )

    val BoxFrameNinePatch = NinePatchResource(
        ImageResource("Images/gui/item_frame.png"),
        IRect(10, 5, 5, 7),
        IRect(474, 444, 475, 445)
    )

    val MissingIcon = ImageResource("Images/portrait-missing.png")
}
