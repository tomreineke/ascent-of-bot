package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.CloseableResourceFactory
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.consumeFfiString
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class Typeface private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<Typeface>(::Typeface, "skiatree_typeface_delete") {
        @JvmStatic
        private val typefaceGetFamilyName = function(
            "skiatree_typeface_get_family_name",
            ADDRESS,
            ADDRESS
        )
    }

    val familyName: String by lazy {
        confinedArena {
            (typefaceGetFamilyName(address) as MemorySegment).consumeFfiString()
        }
    }
}
