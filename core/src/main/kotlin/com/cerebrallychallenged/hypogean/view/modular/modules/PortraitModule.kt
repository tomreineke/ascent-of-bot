package com.cerebrallychallenged.hypogean.view.modular.modules

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.view.common.PortraitNode
import com.cerebrallychallenged.hypogean.view.modular.ModuleContext
import com.cerebrallychallenged.hypogean.view.modular.ViewModule

context(ModuleContext)
class PortraitModule(private val entity: Entity) : ViewModule() {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        var frameSize = 800

        var borderSize = 44
    }

    override val mainNode: PortraitNode = PortraitNode(Style.frameSize, Style.borderSize)

    private val changeListener = object : WorldChange.SimpleVisitor {
        override fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            change.ifOf(Entity::icon) { (entity, _, value, _) ->
                if (entity == this@PortraitModule.entity) {
                    mainNode.image = value
                }
            }
        }
    }

    override fun onChange(change: WorldChange) {
        change.accept(changeListener)
    }

    init {
        mainNode.image = entity.icon
    }
}
