package com.cerebrallychallenged.jun.xml

import org.w3c.dom.Document
import org.w3c.dom.Node

fun Xml.toDom(document: Document): Node {
    return when (this) {
        is Xml.Tag -> {
            val element = document.createElement(name)
            for ((name, value) in attributes) {
                element.setAttribute(name, value)
            }
            for (child in children) {
                element.appendChild(child.toDom(document))
            }
            element
        }
        is Xml.Text -> {
            document.createTextNode(text)
        }
    }
}

fun Node.appendChild(xml: Xml): Node
        = appendChild(xml.toDom(ownerDocument ?: error("Cannot append xml to node without ownerDocument set")))

fun Node.appendChild(block: Xml.XmlFactory.() -> Xml.Tag): Node = appendChild(Xml.create(block))
