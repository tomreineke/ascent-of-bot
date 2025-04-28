package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionMember
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.common.PortraitNode
import com.cerebrallychallenged.hypogean.view.common.installEntityListeners
import com.cerebrallychallenged.hypogean.view.modular.actions.noInfoViewActionAvailable
import com.cerebrallychallenged.jun.skiatree.text.Paragraph
import com.cerebrallychallenged.jun.skiatree.text.ParagraphBuilderContext
import com.cerebrallychallenged.jun.skiatree.text.ParagraphStyle
import com.cerebrallychallenged.jun.skiatree.text.buildParagraph

fun ParagraphBuilderContext.withStyle(textStyle: TextStyleResource, f: ParagraphBuilderContext.() -> Unit) {
    withStyle(ResourceLibrary[textStyle, guiScale], f)
}

fun ParagraphBuilderContext.title(text: String, titleTextStyle: TextStyleResource, spaceBeforeTitle: Boolean = true) {
    val small = titleTextStyle.copy(fontSize = titleTextStyle.fontSize * 0.125f)
    val isAtParagraphStart = isAtParagraphStart
    if (!isAtParagraphStart && !isAtLineStart) {
        // Finish the last line.
        newLine()
    }
    withStyle(titleTextStyle) {
        if (spaceBeforeTitle && !isAtParagraphStart) {
            // Ample spacing before the title.
            newLine(onlyIfNotAtStartOfLine = false)
        }
        +text
        newLine()
    }
    withStyle(small) {
        +"\u2800" // Forces the following newLine to respect the smaller font size
        newLine()
    }
}

context(FactionContext)
fun ParagraphBuilderContext.entityRef(entity: Entity, name: String = entity.name, viewModel: ViewModel) {
    val relation = (entity as? FactionMember)?.factionRelation
    val style = if (relation == null && !entity.noInfoViewActionAvailable()) {
        // If relation is null, e.g. for props like a mine, and if there are InfoViewActions available for that entity,
        // we want that text created using entityRef() has the color of Faction.Relation.NEUTRAL in the ReportView.
        // This way the user knows it is hoverable, because the non-hoverable text in the ReportView is white.
        GuiConfig.TextStyleByFactionRelation.getValue(Faction.Relation.NEUTRAL)
    } else {
        GuiConfig.TextStyleByFactionRelation.getValue(relation)
    }
    link(style) {
        +name
    }.apply {
        installEntityListeners(viewModel, entity)
    }
}

fun ParagraphBuilderContext.entityPortrait(
    entity: Entity,
    portrait: ImageResource,
    frameSize: Int,
    borderSize: Int,
    viewModel: ViewModel
) {
    embed(
        PortraitNode(frameSize, borderSize).apply {
            image = portrait
        }.apply {
            installEntityListeners(viewModel, entity)
        }
    )
}

fun buildParagraph(
    paragraphStyle: ParagraphStyle = ParagraphBuilderContext.DefaultStyle,
    textStyle: TextStyleResource,
    block: ParagraphBuilderContext.() -> Unit
): Paragraph = buildParagraph(paragraphStyle, ResourceLibrary[textStyle, guiScale], block)
