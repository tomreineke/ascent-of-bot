package com.cerebrallychallenged.hypogean.view.modular

import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.jun.skiatree.node.Node

context(ModuleContext)
abstract class ViewModule: FactionContext by context {
    abstract val mainNode: Node

    open fun onChange(change: WorldChange) {}
}
