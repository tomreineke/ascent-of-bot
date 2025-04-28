package com.cerebrallychallenged.hypogean.activestate

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.vanilla.attributes.selectedDialogOptions
import com.cerebrallychallenged.hypogean.view.report.report
import com.cerebrallychallenged.hypogean.view.report.reportSpeech
import com.cerebrallychallenged.hypogean.view.report.reportTerminalPrint

class DialogContext(private val activeDialog: ActiveDialog, cascadeBlock: CascadeBlock) : CascadeBlock by cascadeBlock {
    val roles: RoleMap by activeDialog::roles

    fun <T : Entity> entityPlaying(role: Dialog.Role<T>): T = roles[role]

    fun say(entity: Entity, block: RichText.Builder.() -> Unit) {
        reportSpeech(roles.participatingEntities, entity) {
            block()
        }
    }

    fun Dialog.Role<*>.say(text: String) {
        say {
            +text
        }
    }

    fun Dialog.Role<*>.say(block: RichText.Builder.() -> Unit) {
        say(entityPlaying(this), block)
    }

    fun terminalPrint(block: RichText.Builder.() -> Unit) {
        reportTerminalPrint(roles.participatingEntities, block)
    }

    fun terminalPrint(vararg lines: String) {
        terminalPrint(lines.toList())
    }

    fun terminalPrint(lines: List<String>) {
        val iterator = lines.iterator()
        if (iterator.hasNext()) {
            terminalPrint {
                +iterator.next()
                for (line in iterator) {
                    newLine()
                    +line
                }
            }
        }
    }

    fun describe(text: String) {
        describe {
            +text
        }
    }

    fun describe(block: RichText.Builder.() -> Unit) {
        report(roles.participatingEntities) {
            block()
        }
    }

    fun Dialog.Role<Actor>.select(block: Dialog.Select.() -> Unit): Dialog.Continuation =
        activeDialog.dialog.Select(entityPlaying(this), activeDialog.continuation as Dialog.Node).apply { block() }

    val Dialog.Role<Actor>.actor: Actor
        get() = roles[this]
}

data class ActiveDialog(
    val world: World,
    val dialog: Dialog,
    val roles: RoleMap,
    //FIXME initiatingEntity: Entity   can be activeActor (by TalkAction) or event
    val activeActor: Actor?,
    val continuation: Dialog.Continuation
) : ActiveWorldState() {
    context(WorldContext)
    internal fun select(selectingActor: Actor, option: Dialog.Select.Option): ActiveDialog {
        if (continuation is Dialog.Select && option.then is Dialog.Node) {
            selectingActor.selectedDialogOptions += Pair(continuation, option.then)
        }
        if (option.isSpeech) {
            reportSpeech(roles.participatingEntities, selectingActor) {
                +option.text
            }
        }
        return copy(continuation = option.then)
    }
}
