package com.cerebrallychallenged.jun.xml

sealed class Xml {
    companion object {
        inline fun create(block: XmlFactory.() -> Tag): Tag = XmlFactory.block()
    }

    class Tag(val name: String, initialAttributes: MutableMap<String, String>? = null) : Xml() {
        @PublishedApi
        internal constructor(name: String, attributes: Array<out Pair<String, String>>)
                : this(name, if (attributes.isNotEmpty()) mutableMapOf(*attributes) else null)

        private var _children: MutableList<Xml>? = null

        private var _attributes: MutableMap<String, String>? = initialAttributes

        var comment: String? = null

        val children: List<Xml>
            get() = _children ?: listOf()

        private val mutableChildren: MutableList<Xml>
            get() = _children ?: mutableListOf<Xml>().also { _children = it }

        val childTags: Sequence<Tag>
            get() = children.asSequence().filterIsInstance<Tag>()

        fun add(child: Xml) {
            mutableChildren.add(child)
        }

        fun addAll(children: Collection<Xml>) {
            mutableChildren.addAll(children)
        }

        val attributes: Map<String, String>
            get() = _attributes ?: mapOf()

        private val mutableAttributes: MutableMap<String, String>
            get() = _attributes ?: linkedMapOf<String, String>().also { _attributes = it }

        operator fun get(attributeKey: String): String? = _attributes?.let { it[attributeKey] }

        operator fun set(key: String, value: String) {
            mutableAttributes[key] = value
        }

        operator fun String.invoke(attributes: Map<String, String>): Tag =
                Tag(this, attributes.toMutableMap()).also(::add)

        inline operator fun String.invoke(attributes: Map<String, String>, block: Tag.() -> Unit): Tag =
                Tag(this, attributes.toMutableMap()).also(::add).also(block)

        operator fun String.invoke(vararg attributes: Pair<String, String>): Tag =
                Tag(this, attributes).also(::add)

        inline operator fun String.invoke(vararg attributes: Pair<String, String>, block: Tag.() -> Unit): Tag =
                Tag(this, attributes).also(::add).also(block)

        operator fun String.unaryMinus() {
            add(Text(this))
        }
    }

    data class Text(val text: String) : Xml()

    object XmlFactory {
        operator fun String.invoke(attributes: Map<String, String>): Tag =
                Tag(this, attributes.toMutableMap())

        inline operator fun String.invoke(attributes: Map<String, String>, block: Tag.() -> Unit): Tag =
                Tag(this, attributes.toMutableMap()).also(block)

        operator fun String.invoke(vararg attributes: Pair<String, String>): Tag =
                Tag(this, attributes)

        inline operator fun String.invoke(vararg attributes: Pair<String, String>, block: Tag.() -> Unit): Tag =
                Tag(this, attributes).also(block)
    }

    override fun toString(): String = StringBuilder().also { it.appendXml(this) }.toString()
}