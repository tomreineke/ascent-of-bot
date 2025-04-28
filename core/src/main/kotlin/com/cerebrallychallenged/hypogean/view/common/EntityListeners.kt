package com.cerebrallychallenged.hypogean.view.common

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.jun.input.InputReason
import com.cerebrallychallenged.jun.input.MouseEvent
import com.cerebrallychallenged.jun.skiatree.node.Node

fun Node.installEntityListeners(viewModel: ViewModel, entity: Entity) {
    hoverListeners += { hovered ->
        viewModel.onEntityMouseEvent(entity, if (hovered) MouseEvent.Kind.ENTER else MouseEvent.Kind.LEAVE, InputReason.GUI)
    }
    primaryPressedListeners += { _, _ ->
        viewModel.onEntityMouseEvent(entity, MouseEvent.Kind.CLICK, InputReason.GUI)
        true
    }
}
