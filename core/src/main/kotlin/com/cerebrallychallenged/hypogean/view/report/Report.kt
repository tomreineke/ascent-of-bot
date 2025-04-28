package com.cerebrallychallenged.hypogean.view.report

import com.cerebrallychallenged.hypogean.gui.HBox
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.entityPortrait
import com.cerebrallychallenged.hypogean.gui.paragraphNode
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.linguistics.pronoun
import com.cerebrallychallenged.hypogean.linguistics.signedString
import com.cerebrallychallenged.hypogean.linguistics.verb
import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Cell
import com.cerebrallychallenged.hypogean.model.DEFAULT_NAME
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Item
import com.cerebrallychallenged.hypogean.model.LocatedEntity
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.ViewEvent
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.model.base.Equipment
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.model.cascade.CausalChange
import com.cerebrallychallenged.hypogean.model.cellFilling
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.model.richtext.richText
import com.cerebrallychallenged.hypogean.rays.HitResult
import com.cerebrallychallenged.hypogean.vanilla.attributes.icon
import com.cerebrallychallenged.hypogean.vanilla.cascade.EffectResult
import com.cerebrallychallenged.hypogean.vanilla.cascade.EffectiveDelta
import com.cerebrallychallenged.hypogean.vanilla.cascade.showHealthChangesInDamageReport
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.view.effectinfo.DisplayEffectInfo
import com.cerebrallychallenged.hypogean.view.effectinfo.explanation
import com.cerebrallychallenged.hypogean.view.mouse.MouseView
import com.cerebrallychallenged.hypogean.view.util.CommonGuiImages
import com.cerebrallychallenged.hypogean.view.util.mouse.Info
import com.cerebrallychallenged.hypogean.view.util.mouse.MouseCursor
import com.cerebrallychallenged.hypogean.view.util.process
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.table.Table

data class Report(
    val affectedEntities: List<Entity>,
    val richText: RichText
) : ViewEvent

fun RichText.Builder.effectAmount(
    effectiveDelta: EffectiveDelta,
    attribute: SimpleIntAttribute<out Entity>,
    causalChange: CausalChange,
) {
    deferred { viewModel ->
        link {
            +attribute.formatValue(effectiveDelta.effectiveDelta, usePlusSign = true)
        }.apply {
            tooltip = Tooltip {
                when (causalChange) {
                    is EffectResult -> {
                        embed(Table(2).apply {
                            hgap = 10
                            columns[0].align = Align.Max
                            columns[1].align = Align.Min
                            for (table in causalChange.tables) {
                                addRow().apply {
                                    this[0].paragraphNode {
                                        +(table.sign * table.table.amount).signedString(usePlusSign = true)
                                    }
                                    this[1].paragraphNode {
                                        +"${table.table.kind}"
                                    }
                                }
                            }
                            if (causalChange.tables.size > 1) {
                                addRow().apply {
                                    topStrokeWidth = 1.0f
                                    this[0].paragraphNode {
                                        +effectiveDelta.originalDelta.signedString(usePlusSign = true)
                                    }
                                    this[1].paragraphNode {
                                        +"Total"
                                    }
                                }
                            }
                        })
                        effectiveDelta.explanation(attribute)?.let { explanation ->
                            newLine(onlyIfNotAtStartOfLine = false)
                            +explanation
                        }
                    }
                }
            }
            hoverListeners += { hovered ->
                if (hovered) {
                    MouseView.addMouseCursor(this, MouseCursor.Info)
                } else {
                    MouseView.removeMouseCursor(this)
                }
            }
            primaryPressedListeners += { _, _ ->
                viewModel.submitInfoDisplay(DisplayEffectInfo(attribute, causalChange, effectiveDelta))
                true
            }
        }
    }
}

context(WorldContext)
fun report(affectedEntities: List<Entity>, block: RichText.Builder.() -> Unit) {
    val richText = richText(block)
    world.notifyViewEvent(Report(affectedEntities, richText))
}

context(WorldContext)
fun report(vararg affectedEntities: Entity, block: RichText.Builder.() -> Unit) {
    report(affectedEntities.toList(), block)
}

context(WorldContext)
fun reportSpeech(affectedEntities: List<Entity>, speaker: Entity, block: RichText.Builder.() -> Unit) {
    //FIXME Separate Report class for direct speech.
    report(affectedEntities) {
        deferred { viewModel ->
            embed(HBox().apply {
                hgap = 5
                paragraphNode {
                    entityPortrait(speaker, speaker.icon ?: CommonGuiImages.MissingIcon, 400, 44, viewModel)
                }
                paragraphNode {
                    richText {
                        quoteItalic(block)
                    }.process(viewModel)
                }.apply {
                    maxWidth = 230
                }
            })
        }
//        entityPortrait(speaker, speaker.icon ?: CommonGuiImages.MissingIcon, 400, 44)
//        +" "
//        quoteItalic(block)
    }
}

context(WorldContext)
fun reportTerminalPrint(affectedEntities: List<Entity>, block: RichText.Builder.() -> Unit) {
    report(affectedEntities) {
        withStyle(RichText.Style.Monospace, block)
    }
}

context(WorldContext)
fun reportHit(
    activeActor: Actor,
    tool: Item,
    target: LocatedEntity,
    hitResult: HitResult
) {
    reportHit(activeActor, tool, target, hitResult.position, hitResult.hitEntities)
}

context(WorldContext)
fun reportHit(
    activeActor: Actor,
    tool: Item,
    target: LocatedEntity
) {
    reportHit(activeActor, tool, target, target.position, listOf(target))
}

context(WorldContext)
private fun reportHit(
    activeActor: Actor,
    tool: Item,
    target: LocatedEntity,
    hitPosition: Vec2i,
    hitEntities: List<LocatedEntity>
) {
    val (intendedTarget, isIntendedHit) = if (target is Cell) {
        val intendedTarget = target.props.firstOrNull { it.cellFilling } ?: target
        Pair(intendedTarget, target.position == hitPosition || intendedTarget in hitEntities)
    } else {
        Pair(target, target in hitEntities)
    }
    report(hitEntities + activeActor) {
        entityRefCapitalizeName(activeActor)
        if (isIntendedHit) {
            + " ${tool.verb.thirdPersonSingular} "
        } else {
            + " tries to ${tool.verb.infinitive} "
        }

        entityRef(intendedTarget, name = when {
            intendedTarget is Cell -> {
                val position = intendedTarget.position
                "position (${position.x}, ${position.y})"
            }
            intendedTarget.name != DEFAULT_NAME -> target.name
            else -> "something"
        })
        if (tool.name != DEFAULT_NAME) {
            + " with the "
            entityRef(tool)
        }
        if (isIntendedHit) {
            val remainingEntities = hitEntities - intendedTarget
            when (val remainingSize = remainingEntities.size) {
                0 -> {}
                1 -> {
                    +" and hits ${intendedTarget.pronoun.accusative} and "
                    entityRef(remainingEntities.first())
                }
                else -> {
                    + " and hits ${intendedTarget.pronoun.accusative}"
                    for (hitEntity in remainingEntities.subList(0, remainingSize - 1)) {
                        +", "
                        entityRef(hitEntity)
                    }
                    +", and "
                    entityRef(remainingEntities.last())
                }
            }
        } else {
            + ", but misses ${intendedTarget.pronoun.accusative} wildly"
            if (hitEntities.isNotEmpty()) {
                +" and hits "
                when (val size = hitEntities.size) {
                    1 -> entityRef(hitEntities.single())
                    2 -> {
                        entityRef(hitEntities[0])
                        +" and "
                        entityRef(hitEntities[1])
                    }
                    else -> {
                        for (hitEntity in hitEntities.subList(0, size - 1)) {
                            +", "
                            entityRef(hitEntity)
                        }
                        +", and "
                        entityRef(hitEntities.last())
                    }
                }
            }
        }
        +"."
    }
}

context(WorldContext)
fun reportHeal(activeActor: Actor, equipment: Equipment, target: LocatedEntity) {
    if (target.showHealthChangesInDamageReport
        && ProtagonistFaction.reconOf(target) == Recon.Visible
    ) {
        report(target) {
            entityRefCapitalizeName(activeActor)
            +" heals "
            if (target == activeActor) { +"" } else {
                if (target.name != DEFAULT_NAME) {
                    entityRef(target)
                } else {
                    +"position (${target.position.x}, ${target.position.y})"
                }
            }
            +" with ${equipment.name}."
        }
    } else {
        report(activeActor) {
            entityRefCapitalizeName(activeActor)
            +" heals something with ${equipment.name}."
        }
    }
}
