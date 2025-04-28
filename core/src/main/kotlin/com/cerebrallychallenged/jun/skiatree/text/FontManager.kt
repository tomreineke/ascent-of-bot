package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.guardedPointerArena
import com.cerebrallychallenged.jun.skiatree.toSegment
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.nio.file.Path
import kotlin.io.path.absolutePathString

open class FontManager protected constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<FontManager>(::FontManager, "skiatree_font_manager_delete") {
        @JvmStatic
        private val fontManagerNew = function("skiatree_font_manager_new", ADDRESS)

        @JvmStatic
        private val fontManagerLoad = function(
            "skiatree_font_manager_load",
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val fontManagerFamilyNames = function(
            "skiatree_font_manager_family_names",
            ADDRESS,
            ADDRESS
        )

        operator fun invoke(): FontManager = FontManager { fontManagerNew() as MemorySegment }
    }

    fun load(path: Path): Typeface = Typeface {
        guardedPointerArena {
            fontManagerLoad(address, path.absolutePathString().toSegment()) as MemorySegment
        }
    }

    val familyNames: StringList
        get() = StringList { fontManagerFamilyNames(address) as MemorySegment }
}
