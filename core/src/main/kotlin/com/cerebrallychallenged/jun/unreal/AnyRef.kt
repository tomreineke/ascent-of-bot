package com.cerebrallychallenged.jun.unreal

import com.cerebrallychallenged.jun.JunManager
import com.cerebrallychallenged.jun.LifeTime
import com.cerebrallychallenged.jun.util.CPointer
import com.cerebrallychallenged.jun.util.isNull

abstract class AnyRef<out T>(val lifeTime: LifeTime) {
    abstract val directPtr: CPointer

    fun <T> CPointer.wrapDirectRef(): DirectRef<T> = DirectRef(this, lifeTime)

    fun <T> CPointer.wrapDirectPtr(): DirectPtr<T> = if (isNull()) null else wrapDirectRef()
}

class TSharedRef<out T> internal constructor(
        val sharedPtrPtr: CPointer,
        lifeTime: LifeTime
) : AnyRef<T>(lifeTime), AutoCloseable {
    val directRef: DirectRef<T> by lazy(LazyThreadSafetyMode.NONE) {
        lifeTime.check()
        DirectRef(getDirect(sharedPtrPtr), lifeTime)
    }

    override val directPtr: CPointer
        get() = directRef.directPtr

    override fun close() {
        lifeTime.close()
        JunManager.closeSharedRef(sharedPtrPtr)
    }
}

typealias TSharedPtr<T> = TSharedRef<T>?

class DirectRef<out T>(private val _directPtr: CPointer, lifeTime: LifeTime) : AnyRef<T>(lifeTime) {
    override val directPtr: CPointer
        get() {
            lifeTime.check()
            return _directPtr
        }
}

typealias DirectPtr<T> = DirectRef<T>?

val TSharedPtr<*>.nullableSharedPtrPtr: CPointer
    get() = this?.sharedPtrPtr ?: 0L

private external fun getDirect(sharedPtrPtr: CPointer): CPointer


val AnyRef<Int>.value: Int
    get() = getInt(directPtr)

private external fun getInt(directPtr: CPointer): Int
