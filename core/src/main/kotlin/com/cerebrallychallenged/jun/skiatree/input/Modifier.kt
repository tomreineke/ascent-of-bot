package com.cerebrallychallenged.jun.skiatree.input

enum class Modifier {
    Shift,
    Control,
    Alt,
    Command;

    internal val mask: Int
        get() = 1 shl ordinal
}

@JvmInline
value class ModifierSet private constructor(internal val value: Int) : Set<Modifier> {
    companion object {
        internal operator fun invoke(magic: Byte): ModifierSet = ModifierSet(magic.toInt())
    }

    override operator fun contains(element: Modifier): Boolean = value and element.mask != 0

    override val size: Int
        get() = value.countOneBits()

    override fun containsAll(elements: Collection<Modifier>): Boolean = elements.all { it in this }

    override fun isEmpty(): Boolean = value == 0

    private fun asSequence(): Sequence<Modifier> = Modifier.values().asSequence().filter { it in this }

    override fun iterator(): Iterator<Modifier> = asSequence().iterator()

    override fun toString(): String = asSequence().joinToString(prefix = "[", postfix = "]")
}
