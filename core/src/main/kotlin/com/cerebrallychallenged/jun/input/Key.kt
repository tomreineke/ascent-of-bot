package com.cerebrallychallenged.jun.input

class Key private constructor(val index: Int) {
    companion object {
        private val keyByIndex: List<Key> = (0 until keyCount()).map { Key(it) }

        private val keyByName = keyByIndex.associateBy { it.name }

        @JvmStatic
        operator fun get(keyIndex: Int): Key = keyByIndex[keyIndex]

        @JvmStatic
        operator fun get(name: String): Key? = keyByName[name]

        @JvmStatic
        val ANY_KEY: Key = Key[specialKeyIndex(0)]

        @JvmStatic
        val LEFT_MOUSE_BUTTON: Key = Key[specialKeyIndex(1)]

        @JvmStatic
        val MIDDLE_MOUSE_BUTTON: Key = Key[specialKeyIndex(2)]

        @JvmStatic
        val RIGHT_MOUSE_BUTTON: Key = Key[specialKeyIndex(3)]

        @JvmStatic
        val THUMB_MOUSE_BUTTON: Key = Key[specialKeyIndex(4)]

        @JvmStatic
        val THUMB_MOUSE_BUTTON2: Key = Key[specialKeyIndex(5)]

        @JvmStatic
        val MOUSE_SCROLL_UP: Key = Key[specialKeyIndex(6)]

        @JvmStatic
        val MOUSE_SCROLL_DOWN: Key = Key[specialKeyIndex(7)]

        private val keyByKeyCode = keyByIndex.associateBy { key ->
            key.keyCode.takeIf { it != 0 } ?: key.charCode
        }

        fun getByKeyCode(keyCode: Int): Key? = keyByKeyCode[keyCode]

        val allKeys: List<Key>
            get() = keyByIndex
    }

    val name = getName(index)

    val displayName = getDisplayName(index)

    val keyCode: Int

    val charCode: Int

    init {
        val codes = getCodes(index)
        keyCode = (codes shr 32).toInt()
        charCode = (codes and 0xFFFFFFFFL).toInt()
    }

    val isMouseButton: Boolean
        get() = this === LEFT_MOUSE_BUTTON
                || this === MIDDLE_MOUSE_BUTTON
                || this === RIGHT_MOUSE_BUTTON

    val isMouseWheel: Boolean
        get() = this === MOUSE_SCROLL_UP
                || this === MOUSE_SCROLL_DOWN

    override fun toString(): String = "Key(name=$name, index=$index)"
}

private external fun getName(index: Int): String

private external fun getDisplayName(index: Int): String

private external fun getCodes(index: Int): Long

private external fun keyCount(): Int

private external fun specialKeyIndex(specialIndex: Int): Int
