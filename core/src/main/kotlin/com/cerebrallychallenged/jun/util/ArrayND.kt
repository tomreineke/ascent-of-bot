package com.cerebrallychallenged.jun.util

import com.cerebrallychallenged.jun.math.geo.*
import java.io.DataInput
import java.io.DataOutput

private class VectorIndexOutOfBoundsException(index: IntVector<*, *, *>, bounds: Bounds<*>)
        : IndexOutOfBoundsException("Index $index out of bounds $bounds")

abstract class IndexLinearizer<V : IntVector<V, *, *>>(val indexBounds: Bounds<V>) {

    /**
     * Returns the points (integer coordinates) within the specified bounds.
     * Is needed because `Bounds.points` is no virtual method but realized by extensions.
     */
    abstract fun pointsOf(bounds: Bounds<V>): Sequence<V>

    val indices: Sequence<V>
        get() = pointsOf(indexBounds)

    abstract val arraySize: Int

    /**
     * Converts vector index to linear array index.
     */
    fun vectorToArray(vectorIndex: V): Int {
        if (indexBounds.contains(vectorIndex)) {
            return uncheckedVectorToArray(vectorIndex)
        } else {
            throw VectorIndexOutOfBoundsException(vectorIndex, indexBounds)
        }
    }

    /**
     * Converts vector index to linear array index without bounds check.
     */
    abstract fun uncheckedVectorToArray(vectorIndex: V): Int

    /**
     * Converts linear array index to vector index without bounds check.
     */
    abstract fun uncheckedVectorFromArray(arrayIndex: Int): V
}

class IndexLinearizer2(indexBounds: Bounds<Vec2i>) : IndexLinearizer<Vec2i>(indexBounds) {
    override fun pointsOf(bounds: Bounds<Vec2i>): Sequence<Vec2i> = bounds.points

    override val arraySize: Int

    private val min: Vec2i

    private val stride: Int

    private val offset: Int

    init {
        if (indexBounds.isEmpty) {
            min = Vec2i.ZERO
            arraySize = 0
            stride = 0
            offset = 0
        } else {
            min = indexBounds.min
            val size = indexBounds.size
            arraySize = size.x * size.y
            stride = size.y
            offset = -min.x * stride - min.y
        }
    }

    override fun uncheckedVectorToArray(vectorIndex: Vec2i): Int {
        // (x - min.x) * stride + (y - min.y)
        // Multiplying out...
        // x * stride - min.x * stride + y - min.y
        // x * stride + y + (-min.x * side - min.y)
        //                  \_____________________/
        //                          offset

        return vectorIndex.x * stride + vectorIndex.y + offset
    }

    override fun uncheckedVectorFromArray(arrayIndex: Int): Vec2i {
        return vec(arrayIndex / stride + min.x, arrayIndex % stride + min.y)
    }
}

class ArrayND<V: IntVector<V, *, *>, T>(private val linearizer: IndexLinearizer<V>, private val data: Array<T>) {
    companion object {
        inline fun <reified T> create(shape: Vec2i, init: (Vec2i) -> T): ArrayND<Vec2i, T>
                = create(Bounds.byMinSize(Vec2i.ZERO, shape), init)

        inline fun <reified T> create(indexBounds: Bounds<Vec2i>, init: (Vec2i) -> T): ArrayND<Vec2i, T> {
            val linearizer = IndexLinearizer2(indexBounds)
            return ArrayND(
                    linearizer,
                    Array(linearizer.arraySize) { arrayIndex -> init(linearizer.uncheckedVectorFromArray(arrayIndex)) }
            )
        }
    }

    operator fun get(index: V): T = data[linearizer.vectorToArray(index)]

    operator fun set(index: V, value: T) {
        data[linearizer.vectorToArray(index)] = value
    }

    val indexBounds: Bounds<V>
        get() = linearizer.indexBounds

    val indices: Sequence<V>
        get() = linearizer.indices

    fun withIndex(): Sequence<Pair<V, T>>
            = data.indices.asSequence().map { Pair(linearizer.uncheckedVectorFromArray(it), data[it]) }

    val values: Sequence<T>
        get() = data.asSequence()
}

operator fun <T> ArrayND<Vec2i, T>.get(x: Int, y: Int): T = this[vec(x, y)]

operator fun <T> ArrayND<Vec3i, T>.get(x: Int, y: Int, z: Int): T = this[vec(x, y, z)]

operator fun <T> ArrayND<Vec4i, T>.get(x: Int, y: Int, z: Int, w: Int): T = this[vec(x, y, z, w)]

fun <T> DataOutput.writeArray2(array: ArrayND<Vec2i, T>, writeElement: DataOutput.(T) -> Unit) {
    writeBounds2i(array.indexBounds)
    for (value in array.values) {
        writeElement(value)
    }
}

inline fun <reified T> DataInput.readArray2(crossinline readElement: DataInput.() -> T): ArrayND<Vec2i, T>
        = ArrayND.create(readBounds2i()) { readElement() }