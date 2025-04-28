package com.cerebrallychallenged.jun.xml

import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.ext.LexicalHandler
import org.xml.sax.helpers.DefaultHandler
import java.io.*
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.SAXParserFactory

private class XmlParser : DefaultHandler(), LexicalHandler {
    lateinit var root: Xml.Tag

    private val stack: Deque<Xml.Tag> = ArrayDeque()

    private val textBuffer = StringBuilder()

    private var lastComment: String? = null

    private fun flushText() {
        if (textBuffer.isNotEmpty()) {
            val text = textBuffer.trim()
            if (text.isNotEmpty()) {
                stack.last.add(Xml.Text(text.toString()))
            }
            textBuffer.clear()
        }
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        flushText()
        val attributesLength = attributes.length
        val attrs = if (attributesLength > 0) {
            (0 until attributesLength).associateTo(mutableMapOf<String, String>()) {
                attributes.getQName(it) to attributes.getValue(it)
            }
        } else {
            null
        }
        val tag = Xml.Tag(qName, attrs)
        tag.comment = lastComment
        lastComment = null
        if (stack.isEmpty()) {
            root = tag
        } else {
            stack.last.add(tag)
        }
        stack.addLast(tag)
    }

    override fun endElement(uri: String?, localName: String?, qName: String?) {
        flushText()
        stack.removeLast()
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        textBuffer.appendRange(ch, start, start + length)
    }

    override fun comment(ch: CharArray, start: Int, length: Int) {
        lastComment = String(ch, start, length).trim()
    }

    override fun startEntity(name: String) {}

    override fun endEntity(name: String) {}

    override fun startDTD(name: String, publicId: String, systemId: String) {}

    override fun endDTD() {}

    override fun startCDATA() {}

    override fun endCDATA() {}
}

private val PARSER_FACTORY = SAXParserFactory.newInstance().apply {
    isValidating = false
    isNamespaceAware = false
}

fun InputSource.readXml(): Xml.Tag {
    val saxParser = PARSER_FACTORY.newSAXParser()
    val parser = XmlParser()
    saxParser.setProperty("http://xml.org/sax/properties/lexical-handler", parser)
    saxParser.parse(this, parser)
    return parser.root
}

fun InputStream.readXml(): Xml.Tag = InputStreamReader(this).readXml()

fun Reader.readXml(): Xml.Tag  = InputSource(this).readXml()

fun Path.readXml(): Xml.Tag = toFile().readXml()

fun BufferedReader.skipBOM() {
    mark(1)
    val ch = read()
    if (ch != 0xFEFF) {
        reset()
    }
}

fun File.readXml(): Xml.Tag = FileReader(this).use {
    BufferedReader(it).use { reader ->
        reader.skipBOM()
        reader.readXml()
    }
}

fun String.parseXml(): Xml.Tag = StringReader(this).readXml()

