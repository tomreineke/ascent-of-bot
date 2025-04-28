package com.cerebrallychallenged.jun.skiatree

import com.cerebrallychallenged.jun.CloseableResourceBearer
import com.cerebrallychallenged.jun.address
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.function
import com.cerebrallychallenged.jun.skiatree.SkiaTreeApi.libraryPointer
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.ADDRESS
import java.lang.foreign.ValueLayout.JAVA_BYTE
import java.lang.foreign.ValueLayout.JAVA_INT
import java.lang.foreign.ValueLayout.JAVA_LONG

class Surface private constructor(resource: CloseableResource) : CloseableResourceBearer(resource) {
    companion object : CloseableResourceFactory<Surface>(::Surface, "skiatree_surface_delete") {
        @JvmStatic
        private val surfaceNew = function(
            "skiatree_surface_new",
            ADDRESS,
            ADDRESS,
            JAVA_INT,
            JAVA_INT
        )

        @JvmStatic
        private val surfaceGetCanvas = function(
            "skiatree_surface_get_canvas",
            ADDRESS,
            ADDRESS
        )

        @JvmStatic
        private val surfaceFlushAndSubmit = function(
            "skiatree_surface_flush_and_submit",
            VOID,
            ADDRESS
        )

        @JvmStatic
        private val surfaceReadPixels = function(
            "skiatree_surface_read_pixels",
            JAVA_BYTE,
            ADDRESS,
            ADDRESS,
            JAVA_LONG
        )

        operator fun invoke(size: Vec2i): Surface =
            Surface { surfaceNew(libraryPointer, size.x, size.y) as MemorySegment }
    }

    init {
        createWeakReference(resource)
    }

    val canvas: Canvas
        get() = Canvas(surfaceGetCanvas(address) as MemorySegment)

    fun flushAndSubmit() {
        surfaceFlushAndSubmit(address) as Unit
    }

    fun readPixels(address: MemorySegment, byteSize: Long) {
        guardedUnit {
            surfaceReadPixels(resource.address, address, byteSize) as Byte
        }
    }
}
