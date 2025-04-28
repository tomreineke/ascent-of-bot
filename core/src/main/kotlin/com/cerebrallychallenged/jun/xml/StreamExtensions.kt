package com.cerebrallychallenged.jun.xml

import com.cerebrallychallenged.jun.stream.*
import java.io.DataInput
import java.io.DataOutput

fun DataOutput.writeXmlTag(xml: Xml.Tag) {
    writeString(xml.name)
    writeMap(xml.attributes, { writeString(it) }, { writeString(it) })
    writeList(xml.children) { writeXml(it) }
}

fun DataInput.readXmlTag(): Xml.Tag = Xml.Tag(
        readString(),
        readMapTo(mutableMapOf(), { readString() }, { readString() })
).apply {
    addAll(readList { readXml() })
}

fun DataOutput.writeXmlText(text: Xml.Text) {
    writeString(text.text)
}

fun DataInput.readXmlText(): Xml.Text = Xml.Text(readString())

fun DataOutput.writeXml(xml: Xml) {
    when (xml) {
        is Xml.Tag -> {
            writeBoolean(true)
            writeXmlTag(xml)
        }
        is Xml.Text -> {
            writeBoolean(false)
            writeXmlText(xml)
        }
    }
}

fun DataInput.readXml(): Xml {
    return if (readBoolean()) {
        readXmlTag()
    } else {
        readXmlText()
    }
}