package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.consumeFfiString
import com.cerebrallychallenged.jun.skiatree.guardedResource
import com.cerebrallychallenged.jun.skiatree.toSegment
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_LONG

open class StringList protected constructor(
    internal val resource: CloseableResource,
) : AbstractList<String>(), AutoCloseable by resource {
    companion object {
        @JvmStatic
        private val stringListNew = function("skiatree_string_list_new", ADDRESS)

        @JvmStatic
        private val stringListDelete = function(
            "skiatree_string_list_delete",
            VOID,
            ADDRESS
        )

        @JvmStatic
        private val stringListLen = function(
            "skiatree_string_list_len",
            JAVA_LONG,
            ADDRESS
        )

        @JvmStatic
        private val stringListGet = function(
            "skiatree_string_list_get",
            ADDRESS,
            ADDRESS,
            JAVA_LONG
        )

        operator fun invoke(block: () -> MemorySegment): StringList = invoke(::StringList, block)

        operator fun <T : StringList> invoke(
            create: (CloseableResource) -> T,
            block: () -> MemorySegment = { stringListNew() as MemorySegment }
        ): T = create(guardedResource(stringListDelete, block))

        val Empty: StringList by lazy { MutableStringList() }
    }

    override val size: Int
        get() = (stringListLen(resource.address) as Long).toInt()

    override fun get(index: Int): String =
        (stringListGet(resource.address, index.toLong()) as MemorySegment).consumeFfiString()
}

class MutableStringList private constructor(resource: CloseableResource) : StringList(resource) {
    companion object {
        @JvmStatic
        private val stringListAdd = function(
            "skiatree_string_list_add",
            VOID,
            ADDRESS,
            ADDRESS
        )

        operator fun invoke(): MutableStringList = StringList(::MutableStringList)
    }

    fun add(string: String) = confinedArena {
        stringListAdd(resource.address, string.toSegment())
    }
}

fun buildSkiaStrings(f: MutableStringList.() -> Unit): StringList = MutableStringList().apply(f)

fun skiaStringsOf(vararg strings: String): StringList = buildSkiaStrings {
    for (string in strings) {
        add(string)
    }
}
