package com.cerebrallychallenged.hypogean.model.attribute

interface AttributeCodec<T> {
    fun toDebugString(value: T): String = "$value"
}

class DefaultAttributeCodec<T> : AttributeCodec<T>
