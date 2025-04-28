package com.cerebrallychallenged.hypogean.util.kryo

import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import kotlin.reflect.KClass

abstract class KryoCodec<T : Any>(private val baseClass: KClass<T>) : AttributeCodec<T>
