package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.unreal.SceneComponentLike
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.USceneComponent
import com.cerebrallychallenged.jun.unreal.newObject
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
import kotlinx.coroutines.launch
import kotlin.reflect.KMutableProperty0

interface CompositeParameter<T>

class CompositeAssetDefinitionContext(val library: AssetLibrary) : CompositeNodeContainer() {
    private val bindings: MutableMap<CompositeParameter<*>, MutableList<(Any?) -> Unit>>
            by lazy { Object2ObjectArrayMap() }

    internal val sockets: MutableMap<String, USceneComponent> = mutableMapOf()

    override val context: CompositeAssetDefinitionContext
        get() = this

    override fun attachChild(child: CompositeNode, socketName: String?) {}

    fun <T> CompositeParameter<T>.bind(action: suspend (T) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val actions =
                bindings.computeIfAbsent(this) { mutableListOf() } as MutableList<suspend (T) -> Unit>
        actions.add(action)
    }

    fun <T> CompositeParameter<T>.bind(property: KMutableProperty0<T>) {
        bind {
            property.set(it)
        }
    }

    internal fun <T> setParameter(parameter: CompositeParameter<T>, value: T) {
        @Suppress("UNCHECKED_CAST")
        val actions = (bindings[parameter] ?: return) as MutableList<suspend (T) -> Unit>
        library.scope.launch {
            for (action in actions) {
                action(value)
            }
        }
    }

    fun CompositeNode.declareSocket(socketName: String) {
        this@CompositeAssetDefinitionContext.sockets[socketName] = component
    }
}

abstract class CompositeAsset(private val factory: suspend CompositeAssetDefinitionContext.() -> CompositeNode) {
    suspend fun create(library: AssetLibrary): CompositeNode = create(CompositeAssetDefinitionContext(library))

    internal suspend fun create(context: CompositeAssetDefinitionContext): CompositeNode {
        val component = newObject<USceneComponent>()
        return object : SceneNode<USceneComponent>(
                context,
                component
        ), SceneComponentLike by component {}.apply {
            attachChild(context.factory())
        }
    }

    open fun collectAssetRefs(refs: MutableList<UnrealRef<UObject>>) {}
}
