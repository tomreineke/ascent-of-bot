package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.model.cell
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.behavior.PursuitBehavior
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.AMBUSH_PARTY_POS
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.DOOR_POS
import com.cerebrallychallenged.hypogean.vanilla.props.BigDoor

object OpenGuardDoorDialog : Dialog() {
    object User : Role<Actor>

    object Computer : Role<Item>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
        User playedBy activeActor,
        Computer playedBy (addressee as Item)
    )

    override val start: Node = node {
        terminalPrint(
            "╔═══════════════════╗",
            "║ CENTRAL COMM ROOM ║",
            "╚═══════════════════╝"
        )
        main
    }

    val main: Node = node {
        User.select {
            act(openDoor, "Try to open the big door barring the way.").availableIf {
                true
            }
            act(end, "Leave.")
        }
    }

    private val openDoor = node {
        terminalPrint("Intruder detected. Sending out kill squad.")
        val door = world.cell[DOOR_POS].props.first { it is BigDoor }
        door.remove()
        for (position in AMBUSH_PARTY_POS) {
            world.cell[position].presentActor?.behavior = PursuitBehavior
        }
        end(InitiativeCost.Delta(1))
    }
}
