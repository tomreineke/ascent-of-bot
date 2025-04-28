package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.vanilla.actors.FirstBoss
import com.cerebrallychallenged.hypogean.vanilla.attributes.health
import com.cerebrallychallenged.hypogean.vanilla.events.fireturret.EmitFireFlares
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.FIRST_FIRE_FLARE_DIRECTION
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.SECOND_FIRE_FLARE_DIRECTION

object ActivateFireFlaresDialog : Dialog() {
    object User : Role<Actor>

    object Computer : Role<Item>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
        User playedBy activeActor,
        Computer playedBy (addressee as Item)
    )

    override val start: Node = node {
        terminalPrint(
            "╔═══════════════════╗",
            "║ FIRE FLARE SYSTEM ║",
            "╚═══════════════════╝"
        )
        main
    }

    val main: Node = node {
        User.select {
            act(activateFireFlares, "Activate Fire Flares.").availableIf {
                entityPlaying(User) is FirstBoss
            }
            act(deactivateFireFlares, "Deactivate Fire Flares.").availableIf {
                entityPlaying(User) is FirstBoss
            }
            act(end, "Exit.")
        }
    }

    val activateFireFlares = node {
        terminalPrint("Fire Flares activated.")
        val world = entityPlaying(User).world
        world.entities
            .filterIsInstance<EmitFireFlares>()
            .first { it.name == FIRST_FIRE_FLARE_DIRECTION }.apply {
                world.iniQueue.enqueueRelative(0, this)
                activate()
            }

        world.entities
            .filterIsInstance<EmitFireFlares>()
            .first { it.name == SECOND_FIRE_FLARE_DIRECTION }.apply {
                world.iniQueue.enqueueRelative(2, this)
                activate()
            }
        end(InitiativeCost.Delta(1))
    }

    val deactivateFireFlares = node {
        for (flare in world.entities.filterIsInstance<EmitFireFlares>()) {
            if (flare.isAlive) {
                flare.remove()
            }
        }
        terminalPrint("Fire Flares deactivated.")
        end(InitiativeCost.Delta(1))
    }
}
