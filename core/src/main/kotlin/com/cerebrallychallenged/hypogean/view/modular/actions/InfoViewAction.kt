package com.cerebrallychallenged.hypogean.view.modular.actions

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.action.MutableActionTable
import com.cerebrallychallenged.hypogean.model.action.ViewAction
import com.cerebrallychallenged.hypogean.model.action.ViewActionInstance
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.vanilla.attributes.description
import com.cerebrallychallenged.hypogean.vanilla.props.InventoryProp

fun Entity.noInfoViewActionAvailable() = this.description == null

object InfoViewAction : ViewAction() {
    override fun MutableActionTable.createInstances(activeActor: Actor, assumedActiveActorLocation: Cell) {
        val world = activeActor.world
        with (activeActor.factionEntity as FactionContext) {
            for (actor in world.actors) {
                if (actor.recon == Recon.Visible && actor.factionEntity != ownFactionEntity) {
                    addInstance(InfoViewActionInstance(activeActor, actor, false))
                }
            }
            for (cell in world.cells) {
                for (prop in cell.props) {
                    if (prop.recon == Recon.Visible && prop !is InventoryProp) {
                        addInstance(InfoViewActionInstance(activeActor, prop, prop.noInfoViewActionAvailable()))
                    }
                }
            }
        }
    }
}

class InfoViewActionInstance(
    activeActor: Actor,
    override val target: Entity,
    isDebugOnly: Boolean
) : ViewActionInstance(InfoViewAction, activeActor, isDebugOnly)
