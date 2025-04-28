package com.cerebrallychallenged.jun.util

import java.lang.foreign.Arena
import java.nio.ByteBuffer
import java.nio.ByteOrder

inline fun <R> confinedArena(f: Arena.() -> R): R = Arena.ofConfined().use(f)

//TODO Replace by slice(index, length) for JDK >= 13 but we still have to modify the byte order!
inline fun <reified T : ByteBuffer> T.asSlice(offset: Int, newSize: Int): T {
    position(offset)
    limit(offset + newSize)
    return slice().order(ByteOrder.LITTLE_ENDIAN) as T
}
