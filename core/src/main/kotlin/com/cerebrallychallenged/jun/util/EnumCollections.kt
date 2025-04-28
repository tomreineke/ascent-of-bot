package com.cerebrallychallenged.jun.util

import java.util.EnumMap
import java.util.EnumSet

inline fun <reified E : Enum<E>> enumSetOf(): Set<E> = mutableEnumSetOf()

inline fun <reified E : Enum<E>> mutableEnumSetOf(): MutableSet<E> = EnumSet.noneOf(E::class.java)

inline fun <reified E: Enum<E>> enumSetOf(vararg elements: E): Set<E> = mutableEnumSetOf(*elements)

inline fun <reified E : Enum<E>> mutableEnumSetOf(vararg elements: E): MutableSet<E> =
    EnumSet.noneOf(E::class.java).apply {
        addAll(elements)
    }

inline fun <reified K : Enum<K>, V> enumMapOf(): Map<K, V> = mutableEnumMapOf()

inline fun <reified K : Enum<K>, V> mutableEnumMapOf(): MutableMap<K, V> = EnumMap(K::class.java)

inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>): Map<K, V> = mutableEnumMapOf(*pairs)

inline fun <reified K : Enum<K>, V> mutableEnumMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> =
    EnumMap<K, V>(K::class.java).apply {
        putAll(pairs)
    }
