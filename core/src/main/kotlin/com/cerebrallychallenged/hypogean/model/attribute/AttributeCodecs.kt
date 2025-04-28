package com.cerebrallychallenged.hypogean.model.attribute

import com.cerebrallychallenged.hypogean.modding.ModContext
import com.cerebrallychallenged.hypogean.modding.Registry
import com.cerebrallychallenged.jun.util.reflect.CachingTypeClass
import kotlin.reflect.KType


class AttributeCodecs(modContext: ModContext) : Registry {
    @PublishedApi
    internal val codecs = CachingTypeClass(AttributeCodec::class).apply {
        addContextObject(modContext)
        addFactory { DefaultAttributeCodec<Any>() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(type: KType): AttributeCodec<T>? = codecs.get(type) as AttributeCodec<T>?

    @Suppress("unused") // ModContext is required to prevent registering after rulebook construction.
    inline fun <reified T : AttributeCodec<*>> ModContext.register() {
        codecs.add<T>()
    }

    @Suppress("unused") // ModContext is required to prevent registering after rulebook construction.
    fun ModContext.registerFactory(factory: (KType) -> AttributeCodec<*>?) {
        codecs.addFactory(factory)
    }
}
