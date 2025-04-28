package com.cerebrallychallenged.hypogean.view.util

import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultItalicStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultMonospaceStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.DefaultTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.MainTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.MediumTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.SmallTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.GuiConfig.TinyTitleTextStyle
import com.cerebrallychallenged.hypogean.gui.ParagraphNode
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.TextStyleResource
import com.cerebrallychallenged.hypogean.gui.Tooltip
import com.cerebrallychallenged.hypogean.gui.entityPortrait
import com.cerebrallychallenged.hypogean.gui.entityRef
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.gui.title
import com.cerebrallychallenged.hypogean.gui.tooltip
import com.cerebrallychallenged.hypogean.gui.withStyle
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.model.richtext.RichText.Style
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.hypogean.view.modular.modules.StatusEffectsModule
import com.cerebrallychallenged.jun.skiatree.node.ImageNode
import com.cerebrallychallenged.jun.skiatree.text.ParagraphBuilderContext

context(FactionContext)
fun RichText.toParagraphNode(
    viewModel: ViewModel,
    textStyle: TextStyleResource = DefaultTextStyle
): ParagraphNode = ParagraphNode {
    withStyle(textStyle) {
        process(viewModel)
    }
}

private fun Style.toTextStyleResource(): TextStyleResource = when (this) {
    Style.Regular -> DefaultTextStyle
    Style.Italic -> DefaultItalicStyle
    Style.Monospace -> DefaultMonospaceStyle
    Style.MainTitle -> MainTitleTextStyle
    Style.MediumTitle -> MediumTitleTextStyle
    Style.SmallTitle -> SmallTitleTextStyle
    Style.TinyTitle -> TinyTitleTextStyle
}

context(ParagraphBuilderContext, FactionContext)
fun RichText.process(viewModel: ViewModel) {
    for (part in parts) {
        when (part) {
            is RichText.Plain -> {
                +part.text
            }
            is RichText.NewLine -> {
                if (!part.onlyIfNotAtStartOfLine || !isAtLineStart) {
                    newLine()
                }
            }
            is RichText.Title -> {
                title(part.text, part.style.toTextStyleResource(), part.spaceBeforeTitle)
            }
            is RichText.EntityRef -> {
                entityRef(part.entity, part.name, viewModel)
            }
            is RichText.EntityPortrait -> {
                entityPortrait(part.entity, part.portrait, part.frameSize, part.borderSize, viewModel)
            }
            is RichText.Image -> {
                embed(ImageNode().apply {
                    // TODO image size should depend on font size
                    image = ResourceLibrary.imageWithWidth(part.image, StatusEffectsModule.Style.effectSize.scaled)
                })
            }
            is RichText.WithStyle -> {
                withStyle(part.style.toTextStyleResource()) {
                    part.richText.process(viewModel)
                }
            }
            is RichText.WithToolTip -> {
                link {
                    part.block.process(viewModel)
                }.apply {
                    tooltip = Tooltip {
                        part.tooltip.process(viewModel)
                    }
                }
            }
            is RichText.Deferred -> {
                part.action(this@ParagraphBuilderContext, this@FactionContext, viewModel)
            }
        }
    }
}
