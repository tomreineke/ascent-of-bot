package com.cerebrallychallenged.hypogean.view.map

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.attribute.AttributeCodec
import com.cerebrallychallenged.jun.asset.CompositeNode
import com.cerebrallychallenged.jun.asset.CompositeParameter
import kotlin.reflect.KMutableProperty1

/**
 * Determine how [Entity] properties influence the parameters of [CompositeNode]s.
 */
class AssetParameterBindings internal constructor(internal val bindings: List<Binding<*, *>>) {
    companion object {
        val Empty = AssetParameterBindings(listOf())
    }

    class Binding<T : Entity, R> internal constructor(val parameter: CompositeParameter<R>, val property: KMutableProperty1<T, R>) {
        internal fun apply(entity: T, node: CompositeNode) {
            node.parameters[parameter] = property.get(entity)
        }

        internal fun apply(change: WorldChange.AttributeChanged<*>, node: CompositeNode) {
            change.ifOf(property) {
                node.parameters[parameter] = it.value
            }
        }
    }

    operator fun plus(rhs: AssetParameterBindings): AssetParameterBindings =
            AssetParameterBindings(bindings + rhs.bindings)

    fun apply(entity: Entity, node: CompositeNode) {
        for (binding in bindings) {
            @Suppress("UNCHECKED_CAST")
            (binding as Binding<Entity, *>).apply(entity, node)
        }
    }

    fun <R> apply(change: WorldChange.AttributeChanged<R>, node: CompositeNode) {
        for (binding in bindings) {
            binding.apply(change, node)
        }
    }
}

infix fun <T : Entity, R> CompositeParameter<R>.boundTo(property: KMutableProperty1<T, R>) =
        AssetParameterBindings.Binding(this, property)

@Suppress("unused")
fun <T : Entity> T.bindAssetParameters(vararg bindings: AssetParameterBindings.Binding<T, *>) =
        AssetParameterBindings(bindings.toList())

internal object AssetParameterBindingsAttributeCodec : AttributeCodec<AssetParameterBindings>
