package com.cerebrallychallenged.jun.stream

import java.io.DataInput
import java.io.DataOutput
import java.io.IOException

fun DataOutput.writeString(str: String) = writeUTF(str)

fun DataInput.readString(): String = readUTF()

fun <T : Enum<*>> DataOutput.writeEnum(value: T) = writeInt(value.ordinal)

inline fun <reified T : Enum<T>> DataInput.readEnum(): T {
    val values = enumValues<T>()
    val index = readInt()
    if (0 <= index && index < values.size) {
        return values[index]
    } else {
        throw IOException("Read int $index is no ordinal of enum ${T::class.simpleName}")
    }
}

fun DataOutput.writeByteArray(value: ByteArray) {
    writeInt(value.size)
    for (b in value) {
        writeByte(b.toInt())
    }
}

fun DataInput.readByteArray(): ByteArray = ByteArray(readInt()) { readByte() }

inline fun <T> DataOutput.writeOptional(value: T?, writeValue: DataOutput.(T) -> Unit) {
    if (value != null) {
        writeBoolean(true)
        writeValue(value)
    } else {
        writeBoolean(false)
    }
}

inline fun <T> DataInput.readOptional(readValue: DataInput.() -> T): T? {
    return if (readBoolean()) {
        readValue()
    } else {
        null
    }
}

inline fun <T> DataOutput.writeList(list: Collection<T>, writeElement: DataOutput.(T) -> Unit) {
    writeInt(list.size)
    for (element in list) {
        writeElement(element)
    }
}

inline fun <T> DataInput.readMutableList(readElement: DataInput.() -> T): MutableList<T>
        = (0 until readInt()).mapTo(mutableListOf()) { readElement() }

inline fun <T> DataInput.readList(readElement: DataInput.() -> T): List<T> = readMutableList(readElement)

inline fun <T, C : MutableCollection<in T>> DataInput.readListTo(ta: C, readElement: DataInput.() -> T): C
        = (0 until readInt()).mapTo(ta) { readElement() }

inline fun <T1, T2> DataOutput.writePair(
        pair: Pair<T1, T2>,
        write1: DataOutput.(T1) -> Unit,
        write2: DataOutput.(T2) -> Unit
) {
    write1(pair.first)
    write2(pair.second)
}

inline fun <T1, T2> DataInput.readPair(
        read1: DataInput.() -> T1,
        read2: DataInput.() -> T2
): Pair<T1, T2> = Pair(read1(), read2())

inline fun <K, V> DataOutput.writeMapEntry(
        entry: Map.Entry<K, V>,
        writeKey: DataOutput.(K) -> Unit,
        writeValue: DataOutput.(V) -> Unit
) {
    writeKey(entry.key)
    writeValue(entry.value)
}


inline fun <K, V> DataOutput.writeMap(
        map: Map<K, V>,
        writeKey: DataOutput.(K) -> Unit,
        writeValue: DataOutput.(V) -> Unit
) {
    writeList(map.entries) { writeMapEntry(it, writeKey, writeValue) }
}

inline fun <K, V> DataInput.readMap(
        readKey: DataInput.() -> K,
        readValue: DataInput.() -> V
): Map<K, V> = readMapTo(mutableMapOf(), readKey, readValue)

inline fun <K, V, M : MutableMap<in K, in V>> DataInput.readMapTo(
        destination: M,
        readKey: DataInput.() -> K,
        readValue: DataInput.() -> V
): M = (0 until readInt()).associateTo(destination) { readPair(readKey, readValue) }

fun DataOutput.writeIntRange(value: IntRange) {
    writeInt(value.first)
    writeInt(value.last)
}

fun DataInput.readIntRange(): IntRange = readInt()..readInt()

inline fun <T : Comparable<T>> DataOutput.writeClosedFloatingPointRange(
        value: ClosedFloatingPointRange<T>,
        writeElement: DataOutput.(T) -> Unit
) {
    writeElement(value.start)
    writeElement(value.endInclusive)
}

fun DataOutput.writeClosedFloatingPointRangeOfFloat(value: ClosedFloatingPointRange<Float>) {
    writeClosedFloatingPointRange(value) { writeFloat(it) }
}

fun DataInput.readClosedFloatingPointRangeOfFloat(): ClosedFloatingPointRange<Float> = readFloat()..readFloat()

fun DataOutput.writeFloatArray(value: FloatArray) {
    writeInt(value.size)
    for (v in value) {
        writeFloat(v)
    }
}

fun DataInput.readFloatArray(): FloatArray = FloatArray(readInt()) { readFloat() }
