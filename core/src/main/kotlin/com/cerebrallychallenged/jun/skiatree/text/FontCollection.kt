package com.cerebrallychallenged.jun.skiatree.text

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.nullableAddress
import com.cerebrallychallenged.jun.skiatree.CloseableResource
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.VOID
import com.cerebrallychallenged.jun.skiatree.guardedResource
import com.cerebrallychallenged.jun.skiatree.toNullableSegment
import com.cerebrallychallenged.jun.util.confinedArena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS

class FontCollection private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object {
        @JvmStatic
        private val fontCollectionNew = function("skiatree_font_collection_new", ADDRESS)

        @JvmStatic
        private val fontCollectionDelete = function(
            "skiatree_font_collection_delete",
            VOID,
            ADDRESS
        )

        @JvmStatic
        private val fontCollectionSetDefaultFontManager = function(
            "skiatree_font_collection_set_default_font_manager",
            VOID,
            ADDRESS,
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val fontCollectionSetAssetFontManager = function(
            "skiatree_font_collection_set_asset_font_manager",
            VOID,
            ADDRESS,
            ADDRESS
        )

        operator fun invoke(): FontCollection = FontCollection(
            guardedResource(fontCollectionDelete) {
                fontCollectionNew() as MemorySegment
            }
        )
    }

    var defaultFontManager: FontManager? = null
        private set

    var defaultFamilyName: String? = null
        private set

    fun setDefaultFontManager(fontManager: FontManager?, defaultFamilyName: String? = null) {
        this@FontCollection.defaultFontManager = fontManager
        this@FontCollection.defaultFamilyName = defaultFamilyName
        confinedArena {
            fontCollectionSetDefaultFontManager(
                address,
                fontManager.nullableAddress,
                defaultFamilyName.toNullableSegment()
            )
        }
    }

    var assetFontManager: FontManager? = null
        set(value) {
            field = value
            fontCollectionSetAssetFontManager(address, value.nullableAddress)
        }
}
