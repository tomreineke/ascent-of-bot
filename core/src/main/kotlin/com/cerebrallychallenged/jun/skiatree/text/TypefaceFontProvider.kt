package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.toNullableSegment
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class TypefaceFontProvider(resource: CloseableResource) : FontManager(resource) {
    companion object : CloseableResourceFactory<TypefaceFontProvider>(::TypefaceFontProvider, "skiatree_typeface_font_provider_delete") {
        @JvmStatic
        private val typefaceFontProviderNew = function("skiatree_typeface_font_provider_new", ADDRESS)

        @JvmStatic
        private val typefaceFontProviderRegisterTypeface = function(
            "skiatree_typeface_font_provider_register_typeface",
            VOID,
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        operator fun invoke(): TypefaceFontProvider =
            TypefaceFontProvider { typefaceFontProviderNew() as MemorySegment }
    }

    fun registerTypeface(typeface: Typeface, alias: String? = null) {
        confinedArena {
            typefaceFontProviderRegisterTypeface(
                address,
                typeface.address,
                alias.toNullableSegment()
            )
        }
    }
}
