package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.Convenience
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.wrapUObject
import java.nio.ByteBuffer

open class UTexture2D(ptr: CPointer) : UTexture(ptr) {
    companion object : UObjectCompanion {
        override lateinit var staticClass: UClass

        @Convenience
        fun createTransient(size: Vec2i, pixelFormat: EPixelFormat): UTexture2D
                = createTransient(size.x, size.y, pixelFormat)

        fun createTransient(width: Int, height: Int, pixelFormat: EPixelFormat): UTexture2D
                = createTransient(width, height, pixelFormat.ordinal.toByte()).wrapUObject()
    }

    val importedSize: Vec2i
        get() = getImportedSize(ptr)

    val sizeX: Int
        get() = getSizeX(ptr)

    val sizeY: Int
        get() = getSizeY(ptr)

    val size: Vec2i
        get() = vec(sizeX, sizeY)

    @Convenience
    fun updateTexture(size: Vec2i, srcPitch: Int, srcDataBuffer: ByteBuffer, callback: Runnable) {
        updateTexture(ptr, size.x, size.y, srcPitch, srcDataBuffer, callback)
    }
}

private external fun createTransient(width: Int, height: Int, pixelFormat: Byte): CPointer

private external fun getImportedSize(ptr: CPointer): Vec2i

private external fun getSizeX(ptr: CPointer): Int

private external fun getSizeY(ptr: CPointer): Int

private external fun updateTexture(
        ptr: CPointer,
        width: Int,
        height: Int,
        srcPitch: Int,
        srcDataBuffer: ByteBuffer,
        callback: Runnable
)
