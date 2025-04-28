package com.cerebrallychallenged.hypogean.view.hint

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.ViewEvent
import com.cerebrallychallenged.hypogean.model.cascade.CascadeBlock
import com.cerebrallychallenged.hypogean.model.richtext.RichText
import com.cerebrallychallenged.hypogean.model.richtext.richText

data class Hint(
    val affectedEntity: Entity,
    val richText: RichText
) : ViewEvent

object HideHint : ViewEvent

context(CascadeBlock)
fun hint(affectedEntity: Entity, block: RichText.Builder.() -> Unit) {
    val richText = richText(block)
    world.notifyViewEvent(Hint(affectedEntity, richText))
    schedule {
        delay(0.01f)
        world.notifyViewEvent(HideHint)
    }
}
