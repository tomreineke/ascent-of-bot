package com.cerebrallychallenged.jun.xml

/**
 * Tries to find a child tag matching the specified predicate.
 * If no such child is found, a new child is created by the specified factory and added to this.
 */
fun Xml.Tag.findOrAddChild(predicate: (Xml.Tag) -> Boolean, factory: Xml.XmlFactory.() -> Xml.Tag): Xml.Tag =
        childTags.firstOrNull(predicate) ?: Xml.create(factory).also { add(it) }