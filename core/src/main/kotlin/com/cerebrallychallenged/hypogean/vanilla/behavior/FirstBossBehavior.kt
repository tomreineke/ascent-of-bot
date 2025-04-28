package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.action.adjacentLocations
import com.cerebrallychallenged.hypogean.model.base.inventory
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.npc.Behavior
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.npc.findItemInInventory
import com.cerebrallychallenged.hypogean.npc.submitBestActionUsing
import com.cerebrallychallenged.hypogean.npc.swapItemToContainer
import com.cerebrallychallenged.hypogean.vanilla.actions.HackingAction
import com.cerebrallychallenged.hypogean.vanilla.actions.hackingDialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.relativeHealth
import com.cerebrallychallenged.hypogean.vanilla.dialogs.ActivateFireFlaresDialog
import com.cerebrallychallenged.hypogean.vanilla.items.chassis.PowerMoveChassis
import com.cerebrallychallenged.hypogean.vanilla.items.melee.EnrageArm
import com.cerebrallychallenged.hypogean.vanilla.items.ranged.missile.ReuseRocketLauncher
import com.cerebrallychallenged.hypogean.vanilla.props.FirstBossTerminal
import com.cerebrallychallenged.hypogean.view.report.report

private fun World.bossTerminalLocations(boss: Actor) : Sequence<Cell> {
    return items.asSequence()
            .filterIsInstance<FirstBossTerminal>()
            .filter { it.hackingDialog == ActivateFireFlaresDialog }
            .mapNotNull { it.anchor as Cell? }
            .flatMap { it.adjacentLocations(boss) }
}


object FirstBossBehavior : Behavior() {
    private suspend fun NpcContext.initialPhase() {
        while (activeActor.relativeHealth > 0.75) {
            submitBestActionUsing(StandardBehavior)
        }
    }

    private suspend fun NpcContext.rocketLauncherPhase() {
        report(listOf(activeActor)) {
            +"The Cold Reaver equips a rocket launcher with a seemingly endless supply of rockets."
        }
        val rocketLauncher = findItemInInventory<ReuseRocketLauncher>()
        swapItemToContainer(rocketLauncher, activeActor.slot("left_shoulder"))
        while (activeActor.relativeHealth > 0.5) {
            submitBestActionUsing(StandardBehavior)
        }
        swapItemToContainer(rocketLauncher, activeActor.factionEntity!!.inventory())
    }

    private suspend fun NpcContext.fireFlarePhase() {
        val bossTerminalLocations = world.bossTerminalLocations(activeActor).toList()
        if (bossTerminalLocations.isNotEmpty()) { // could be empty, if player destroyed the terminal
            report(listOf(activeActor)) {
                +"The Cold Reaver runs to the terminal in them middle of the room and presses some buttons."
            }
            val bossChassis = findItemInInventory<PowerMoveChassis>()
            val oldChassis = swapItemToContainer(bossChassis, activeActor.slot("chassis"))

            moveTo(bossTerminalLocations, MineAvoidingGroundMovement(activeActor))
            oldChassis?.let { swapItemToContainer(it, activeActor.slot("chassis")) }
            val hackingAction = availableActions.groupedByAction[HackingAction].instances.firstOrNull {
                it.target is FirstBossTerminal
            } ?: modelError("$this: Standing next to terminal but no dialog available")
            submit(hackingAction)
        } else {
            report(listOf(activeActor)) {
                +"The Cold Reaver scans the room as if searching for something, then returns to attacking you again."
            }
        }
        while (activeActor.relativeHealth > 0.25) {
            submitBestActionUsing(StandardBehavior)
        }
    }

    private suspend fun NpcContext.enragePhase() {
        report(listOf(activeActor)) {
            +"The Cold Reaver prepares for a last stand equipping an enrage arm."
        }
        val enrageArm = findItemInInventory<EnrageArm>()
        swapItemToContainer(enrageArm, activeActor.slot("right_arm"))
        while (true) {
            submitBestActionUsing(StandardBehavior)
        }
    }

    override suspend fun NpcContext.run() {
        initialPhase()
        rocketLauncherPhase()
        fireFlarePhase()
        enragePhase()
    }

    override suspend fun select(dialogSelection: Dialog.Select): Dialog.Select.Option = with(dialogSelection) {
        when (node) {
            ActivateFireFlaresDialog.main -> optionLeadingTo(ActivateFireFlaresDialog.activateFireFlares)
            else -> modelError("Unexpected dialog selection $dialogSelection")
        }
    }
}
