package com.cerebrallychallenged.hypogean.view.modular.modules

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.view.common.StatusEffectsContainerNode
import com.cerebrallychallenged.hypogean.view.modular.ModuleContext
import com.cerebrallychallenged.hypogean.view.modular.ViewModule

context(ModuleContext)
class StatusEffectsModule(entity: Entity) : ViewModule() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        var containerWidth = 800

        var effectSize: Int = 128
    }

    override val mainNode: StatusEffectsContainerNode =
        StatusEffectsContainerNode(context.viewModel, entity, Style.containerWidth, Style.effectSize)


    override fun onChange(change: WorldChange) {
        mainNode.onChange(change)
    }
}
