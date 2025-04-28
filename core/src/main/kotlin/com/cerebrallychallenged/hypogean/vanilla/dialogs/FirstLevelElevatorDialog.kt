package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.activestate.GameOverReason
import com.cerebrallychallenged.hypogean.activestate.GameOverState
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf

object FirstLevelElevatorDialog : Dialog() {
    object User : Role<Actor>

    object ElevatorButton : Role<Item>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
        User playedBy activeActor,
        ElevatorButton playedBy (addressee as Item)
    )

    override val start: Node = node {
        terminalPrint(
            "╔════════════════════╗",
            "║      ELEVATOR      ║",
            "║authorized personnel║",
            "║        only!       ║",
            "╚════════════════════╝"
        )
        main
    }

    val main: Node = node {
        User.select {
            act(nextLevel, "Press the button to reach the next level.").availableIf {
                true
            }
            act(end, "Leave.")
        }
    }

    private val nextLevel = node {
        terminalPrint("Moving to next level.")
        world.activeState = GameOverState(GameOverReason.Victory, world.currentIniTime)
        end(InitiativeCost.Delta(1))
    }
}
