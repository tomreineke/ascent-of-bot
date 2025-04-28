package com.cerebrallychallenged.hypogean.vanilla.behavior

import com.cerebrallychallenged.hypogean.model.action.skipActionInstance
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.npc.Behavior
import com.cerebrallychallenged.hypogean.npc.NpcContext
import com.cerebrallychallenged.hypogean.vanilla.actions.TalkAction
import com.cerebrallychallenged.hypogean.vanilla.actors.MiningRobot
import com.cerebrallychallenged.hypogean.vanilla.dialogs.GreatAIDialog
import com.cerebrallychallenged.hypogean.vanilla.dialogs.GreatAIDialog2
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction

object GreatAIBehavior : Behavior()  {

    override suspend fun NpcContext.run() {
        val talkingAction = availableActions.groupedByAction[TalkAction].instances.firstOrNull {
            val target = it.target
            target is MiningRobot && target.faction == ProtagonistFaction
        }
        if (talkingAction != null) {
            submit(talkingAction)
        } else {
            submit(availableActions.skipActionInstance)
        }
    }

    override suspend fun select(dialogSelection: Dialog.Select): Dialog.Select.Option = with(dialogSelection) {
        when (node) {
            GreatAIDialog.main -> optionLeadingTo(GreatAIDialog.endTurn)
            GreatAIDialog2.main -> optionLeadingTo(GreatAIDialog2.endTurn)
            else -> modelError("Unexpected dialog selection $dialogSelection")
        }
    }
}