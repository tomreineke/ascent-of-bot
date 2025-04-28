package com.cerebrallychallenged.hypogean.vanilla.actions

import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.model.action.ActionCategory
import com.cerebrallychallenged.hypogean.vanilla.refs.ActionIcons
import com.cerebrallychallenged.hypogean.view.actionbar.ActionButtonStylings

internal object MoveCategory : ActionCategory

val ActionCategory.Companion.Move: ActionCategory
    get() = MoveCategory


internal object UseCategory : ActionCategory

val ActionCategory.Companion.Use: ActionCategory
    get() = UseCategory

internal object UtilityCategory : ActionCategory

val ActionCategory.Companion.Utility: ActionCategory
    get() = UtilityCategory

internal object TalkCategory : ActionCategory

val ActionCategory.Companion.Talk: ActionCategory
    get() = TalkCategory

internal object PickUpCategory : ActionCategory

val ActionCategory.Companion.PickUp: ActionCategory
    get() = PickUpCategory

internal object HackingCategory : ActionCategory

val ActionCategory.Companion.Hacking : ActionCategory
    get() = HackingCategory

internal object AttackCategory : ActionCategory

val ActionCategory.Companion.Attack: ActionCategory
    get() = AttackCategory

internal object ItemSwapCategory : ActionCategory

val ActionCategory.Companion.ItemSwap: ActionCategory
    get() = ItemSwapCategory

object DefaultCategoryAppearance : ActionButtonStylings({
    defineStyling(category = ActionCategory.Move) {
        icon = ActionIcons.Walk
        tooltip = Tooltip { +"Move" }
    }
    defineStyling(category = ActionCategory.Use) {
        icon = ActionIcons.Spanner
        tooltip = Tooltip { +"Use" }
    }
    defineStyling(category = ActionCategory.Utility) {
        icon = ActionIcons.Joystick
        tooltip = Tooltip { +"Use equipped utility item" }
    }
    defineStyling(category = ActionCategory.Talk) {
        icon = ActionIcons.Talk
        tooltip = Tooltip { +"Talk" }
    }
    defineStyling(category = ActionCategory.PickUp) {
        icon = ActionIcons.PickUp
        tooltip = Tooltip { +"Pick up" }
    }
    defineStyling(category = ActionCategory.Hacking) {
        icon = ActionIcons.Circuitry
        tooltip = Tooltip { +"Hack" }
    }
    defineStyling(category = ActionCategory.Attack) {
        icon = ActionIcons.Crosshair
        tooltip = Tooltip { +"Attack" }
    }
    defineStyling(category = ActionCategory.Skip) {
        icon = ActionIcons.ClockwiseRotation
        tooltip = Tooltip { +"Skip" }
    }
})
