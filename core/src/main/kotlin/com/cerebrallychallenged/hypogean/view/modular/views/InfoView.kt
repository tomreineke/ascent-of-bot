package com.cerebrallychallenged.hypogean.view.modular.views

import com.cerebrallychallenged.hypogean.gui.Styling
import com.cerebrallychallenged.hypogean.gui.Window
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.view.ViewActionExecuted
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.hypogean.view.modular.ModularView
import com.cerebrallychallenged.hypogean.view.modular.actions.InfoViewActionInstance

class InfoView(context: ViewFactory.Context) : ModularView<Entity>(context) {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        val DefaultStyle = Styling<Window, Unit> {
//            minWidth = 1500.scaled
//            minHeight = 1000.scaled
        }

        var style: Styling<Window, Unit> = DefaultStyle
    }

    class Factory : ModularView.Factory<Entity>(::InfoView)

    override val windowStyle: Styling<Window, Unit>
        get() = Style.style

    override suspend fun onViewModelChange(change: ViewModelChange) {
        super.onViewModelChange(change)
        if (change is ViewActionExecuted) {
            val instance = change.viewActionInstance
            if (instance is InfoViewActionInstance) {
                show(change.viewActionInstance.target)
            }
        }
    }
}
