package com.cerebrallychallenged.hypogean.model.richtext

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute

fun RichText.Builder.attributeRef(attribute: SimpleIntAttribute<Entity>) {
    +attribute.symbol
}
