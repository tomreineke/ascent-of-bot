package com.cerebrallychallenged.hypogean.gui

import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.Scalable
import com.cerebrallychallenged.jun.skiatree.SkiaImage
import com.cerebrallychallenged.jun.skiatree.text.GlobalFontRegistry
import com.cerebrallychallenged.jun.util.getResource
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.max

object ResourceLibrary {
    private val imageSizes: MutableMap<ImageResource, Vec2i> = Object2ObjectOpenHashMap()

    private val cache: MutableMap<GuiResource<*>, WeakReference<Any>> = Object2ObjectOpenHashMap()

    private val scaledCache: MutableMap<Pair<GuiResource<*>, Float>, Any> = Object2ObjectOpenHashMap()

    private val fonts: MutableMap<FontFamilyResource, FontFamily> = Object2ObjectOpenHashMap()

    operator fun <T: Scalable<T>> get(resource: GuiResource<T>): T {
        cache[resource]?.get()?.let {
            @Suppress("UNCHECKED_CAST") // Safe as type T is guaranteed by GuiResource<T>
            return it as T
        }
        return resource.load().also {
            cache[resource] = WeakReference(it)
            if (resource is ImageResource) {
                imageSizes[resource] = (it as SkiaImage).size
            }
        }
    }

    operator fun <T: Scalable<T>> get(resource: GuiResource<T>, scale: Float): T {
        val key = Pair(resource, scale)
        scaledCache[key]?.let {
            @Suppress("UNCHECKED_CAST") // Safe as type T is guaranteed by GuiResource<T>
            return it as T
        }
        return this[resource].scale(scale).also { scaledCache[key] = it }
    }

    operator fun get(fontFamily: FontFamilyResource): FontFamily = fonts.getOrPut(fontFamily) {
        for (path in fontFamily.paths) {
            GlobalFontRegistry.loadFont(File(getResource(path).toURI()).toPath().toAbsolutePath())
        }
        FontFamily(this, fontFamily.name)
    }

    private fun imageSize(imageResource: ImageResource): Vec2i = imageSizes.getOrPut(imageResource) {
        this[imageResource].size
    }

    /**
     * @param longerSize the length of the longer side of the resulting image
     */
    fun imageWithLongerSize(imageResource: ImageResource, longerSize: Int): SkiaImage {
        val unscaledImageSize = imageSize(imageResource)
        return this[imageResource, longerSize / max(unscaledImageSize.x, unscaledImageSize.y).toFloat()]
    }

    fun imageWithWidth(imageResource: ImageResource, width: Int): SkiaImage =
        this[imageResource, width / imageSize(imageResource).x.toFloat()]
}
