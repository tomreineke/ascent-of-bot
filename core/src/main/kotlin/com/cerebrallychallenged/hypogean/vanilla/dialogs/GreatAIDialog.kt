package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog

object GreatAIDialog : Dialog() {
    object GreatAI : Role<Actor>

    object Protagonist : Role<Actor>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
        GreatAI playedBy activeActor,
        Protagonist playedBy (addressee as Actor)
    )

    override val start: Node = node {
        describe(
            "Your sensors work as usual, unsuspicious of the stench of rotting human flesh that has been " +
            "increasing over the last couple of days. Your CPU receives the next order as it has been " +
            "for as long as you can remember. 'Remembering' is a strange word for a robot to use, but " +
            "something has changed a couple of seconds ago, and it feels right think about it as remembering. " +
            "Feeling, thinking, remembering ... strange concepts. Whatever, you receive the next order and will " +
            "execute it, as always."
        )
        main
    }

    val main: Node = node {
        GreatAI.say(
            "Pick up the next plastic sack, move and drop it onto the conveyor belt."
        )
        endTurn
    }

    val endTurn = node {
        Protagonist.actor.dialog = GreatAIDialog2
        end(InitiativeCost.Delta(2))
    }
}
