package com.cerebrallychallenged.jun.math.geo

import com.cerebrallychallenged.jun.stream.readList
import com.cerebrallychallenged.jun.stream.writeList
import java.io.DataInput
import java.io.DataOutput
import java.util.TreeMap

inline fun <R> DataOutput.writeInterpolatingFunction(
        function: InterpolatingFunction<R>,
        writePoint: DataOutput.(R) -> Unit) {
    writeList(function.points.entries) {
        writeFloat(it.key)
        writePoint(it.value)
    }
}

inline fun <R> DataInput.readInterpolatingFunction(
        noinline interpolator: LinearInterpolator<R>,
        readPoint: DataInput.() -> R
): InterpolatingFunction<R> = InterpolatingFunction(
        readList { Pair(readFloat(), readPoint()) }.associateTo(TreeMap()) { it },
        interpolator
)
