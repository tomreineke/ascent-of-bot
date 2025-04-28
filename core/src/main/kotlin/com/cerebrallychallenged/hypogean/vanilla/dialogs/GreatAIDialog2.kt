package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf

object GreatAIDialog2 : Dialog() {
    object GreatAI : Role<Actor>

    object Protagonist : Role<Actor>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
        GreatAI playedBy activeActor,
        Protagonist playedBy (addressee as Actor)
    )

    override val start: Node = node {
        main
    }

    val main: Node = node {
        GreatAI.say(
            "Pick up the next plastic sack, move and drop it onto the conveyor belt."
        )
        endTurn
    }

    val endTurn = node {
        end(InitiativeCost.Delta(2))
    }
}
