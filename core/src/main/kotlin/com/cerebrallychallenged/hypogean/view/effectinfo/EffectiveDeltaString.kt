package com.cerebrallychallenged.hypogean.view.effectinfo

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.hypogean.vanilla.cascade.EffectiveDelta

fun EffectiveDelta.explanation(attribute: SimpleIntAttribute<out Entity>): String? =
    if (this is EffectiveDelta.Regular) null else buildString {
        append(attribute.formatValue(originalDelta, usePlusSign = true))
        append(" reduced to ")
        append(attribute.formatValue(effectiveDelta, usePlusSign = true))
        if (this@explanation is EffectiveDelta.Capped) {
            append(" because ${attribute.name} is capped at ")
            append(attribute.formatValue(maxAttributeValue, usePlusSign = true))
        } else {
            require(this@explanation is EffectiveDelta.NonNegative)
            append(" because ${attribute.name} is down at ${this@explanation.oldAttributeValue}")
        }
        append('.')
    }
