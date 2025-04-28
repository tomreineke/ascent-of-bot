package com.cerebrallychallenged.hypogean.model.richtext

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.WorldContext
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.view.ViewModel
import com.cerebrallychallenged.jun.skiatree.text.ParagraphBuilderContext
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

data class RichText(val parts: List<Part>) {
    sealed class Part

    data class Plain(val text: String) : Part()

    data class NewLine(val onlyIfNotAtStartOfLine: Boolean) : Part()

    data class Title(val text: String, val style: Style, val spaceBeforeTitle: Boolean) : Part()

    data class EntityRef(val entity: Entity, val name: String = entity.name) : Part()

    data class EntityPortrait(
        val entity: Entity,
        val portrait: ImageResource,
        val frameSize: Int,
        val borderSize: Int
    ) : Part()

    data class Image(val image: ImageResource) : Part()

    data class WithToolTip(val block: RichText, val tooltip: RichText) : Part()

    enum class Style {
        Regular,
        Italic,
        Monospace,
        MainTitle,
        MediumTitle,
        SmallTitle,
        TinyTitle
    }

    data class WithStyle(val style: Style, val richText: RichText) : Part()

    data class Deferred(val action: (ParagraphBuilderContext, FactionContext, ViewModel) -> Unit) : Part()

    class Builder {
        private val parts = mutableListOf<Part>()

        operator fun String.unaryPlus() {
            if (isNotEmpty()) {
                parts.add(Plain(this))
            }
        }

        operator fun RichText.unaryPlus() {
            this@Builder.parts.addAll(parts)
        }

        fun newLine(onlyIfNotAtStartOfLine: Boolean = true) {
            parts.add(NewLine(onlyIfNotAtStartOfLine))
        }

        fun title(text: String, style: Style, spaceBeforeTitle: Boolean = true) {
            parts.add(Title(text, style, spaceBeforeTitle))
        }

        fun entityRef(entity: Entity, name: String = entity.name) {
            parts.add(EntityRef(entity, name))
        }

        fun entityRefCapitalizeName(entity: Entity) {
            val name = entity.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString()
            }
            entityRef(entity, name)
        }

        fun entityPortrait(entity: Entity, portrait: ImageResource, frameSize: Int, borderSize: Int) {
            parts.add(EntityPortrait(entity, portrait, frameSize, borderSize))
        }

        fun image(image: ImageResource) {
            parts.add(Image(image))
        }

        fun <R> withStyle(style: Style, block: Builder.() -> R): R {
            val result: R
            parts.add(WithStyle(style, richText {
                result = block()
            }))
            return result
        }

        fun <R> quoteItalic(block: Builder.() -> R): R {
            +"“"
            val result = withStyle(Style.Italic, block)
            +"”"
            return result
        }

        fun withTooltip(block: RichText, tooltip: RichText) {
            parts.add(WithToolTip(block, tooltip))
        }

        fun deferred(action: context(ParagraphBuilderContext, FactionContext) (ViewModel) -> Unit) {
            parts.add(Deferred(action))
        }

        fun build(): RichText = RichText(parts)
    }
}

fun richText(block: RichText.Builder.() -> Unit): RichText {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return RichText.Builder().apply(block).build()
}

fun String.toRichText(): RichText = RichText(listOf(RichText.Plain(this)))

private fun <C : MutableCollection<Entity>> RichText.collectEntitiesTo(collection: C) {
    for (part in parts) {
        when (part) {
            is RichText.EntityRef -> collection.add(part.entity)
            is RichText.EntityPortrait -> collection.add(part.entity)
            is RichText.WithStyle -> part.richText.collectEntitiesTo(collection)
            else -> {}
        }
    }
}

context(WorldContext)
fun RichText.collectEntities(): List<Entity> = mutableListOf<Entity>().also { collectEntitiesTo(it) }
