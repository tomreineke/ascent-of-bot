package com.cerebrallychallenged.hypogean.model.attribute

import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.linguistics.signedString
import com.cerebrallychallenged.hypogean.modding.SimpleObjectRegistry
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.jun.unreal.color.FLinearColor
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

abstract class IntProperty(
    open val current: KProperty1<out Any, Int>,
    val name: String,
    val symbol: String
)

object InitiativeProperty : IntProperty(
    InitiativeCost::rounds,
    "Initiative",
    "⌛"
)

abstract class SimpleIntAttribute<T : Entity>(
    val entityType: KClass<T>,
    override val current: KProperty1<T, Int>,
    val max: KProperty1<T, Int>,
    val icon: ImageResource,
    val headColor: FLinearColor,
    name: String,
    symbol: String
) : IntProperty(current, name, symbol) {
    val propertyName by lazy { current.name }

    val fillingColor: FLinearColor = headColor * 0.5f

    fun <U : Entity> asAttributeFor(entity: U): SimpleIntAttribute<U>? {
        return if (entityType.isInstance(entity)) {
            @Suppress("UNCHECKED_CAST")
            this as SimpleIntAttribute<U>
        } else {
            null
        }
    }

    fun formatValue(value: Int, usePlusSign: Boolean = false): String = "${value.signedString(usePlusSign)}$symbol"
}

class SimpleIntAttributes : SimpleObjectRegistry<SimpleIntAttribute<*>>()
