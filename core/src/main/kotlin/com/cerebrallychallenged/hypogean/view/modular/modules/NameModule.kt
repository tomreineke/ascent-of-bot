package com.cerebrallychallenged.hypogean.view.modular.modules

import com.cerebrallychallenged.hypogean.gui.GuiConfig.MainTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.view.modular.ModuleContext
import com.cerebrallychallenged.hypogean.view.modular.ViewModule
import com.cerebrallychallenged.jun.skiatree.node.Node

context(ModuleContext)
class NameModule(val entity: Entity) : ViewModule() {
    override val mainNode: Node = ParagraphNode(MainTitleTextStyle) {
        +entity.name
    }
}
