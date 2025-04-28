package com.cerebrallychallenged.jun.xml

import com.google.common.xml.XmlEscapers
import java.io.File
import java.io.FileWriter
import java.nio.file.Path

private fun Appendable.appendIndentation(level: Int, indentation: String, separator: String) {
    if (level > 0) {
        append(separator)
        if (indentation.isNotEmpty()) {
            repeat(level) {
                append(indentation)
            }
        }
    }
}

@Suppress("UnstableApiUsage")
private fun String.escapeAttribute() = XmlEscapers.xmlAttributeEscaper().escape(this)

@Suppress("UnstableApiUsage")
private fun String.escape() = XmlEscapers.xmlContentEscaper().escape(this)

private fun <T : Appendable> T.appendXml(xml: Xml, level: Int, indentation: String, separator: String): T {
    appendIndentation(level, indentation, separator)
    val result = when (xml) {
        is Xml.Tag -> {
            append('<').append(xml.name)
            for ((key, value) in xml.attributes) {
                append(' ').append(key).append('=').append('"').append(value.escapeAttribute()).append('"')
            }
            val children = xml.children
            if (children.isEmpty()) {
                append('/').append('>')
            } else {
                append('>')
                if (children.size == 1 && children[0] is Xml.Text) {
                    appendXml(children[0], 0, indentation, separator)
                } else {
                    for (child in children) {
                        appendXml(child, level + 1, indentation, separator)
                    }
                    if (level == 0) {
                        append(separator)
                    }
                    appendIndentation(level, indentation, separator)
                }
                append('<').append('/').append(xml.name).append('>')
            }
        }
        is Xml.Text -> {
            append(xml.text.escape())
        }
    }
    @Suppress("UNCHECKED_CAST")
    return result as T
}

fun <T : Appendable> T.appendXml(xml: Xml, indentation: String = "\t", separator: String = "\n"): T
        = appendXml(xml, 0, indentation, separator)

fun File.writeXml(xml: Xml, indentation: String = "\t", separator: String = "\n") {
    FileWriter(this).use { it.appendXml(xml, indentation, separator) }
}

fun Path.writeXml(xml: Xml, indentation: String = "\t", separator: String = "\n") {
    toFile().writeXml(xml, indentation, separator)
}