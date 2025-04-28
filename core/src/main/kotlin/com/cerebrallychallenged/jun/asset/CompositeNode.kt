package com.cerebrallychallenged.jun.asset

import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.math.Angle
import com.cerebrallychallenged.jun.math.geo.Quaternion
import com.cerebrallychallenged.jun.math.geo.Transform3f
import com.cerebrallychallenged.jun.math.geo.Vec3f
import com.cerebrallychallenged.jun.unreal.FAttachmentTransformRules
import com.cerebrallychallenged.jun.unreal.SceneComponentLike
import com.cerebrallychallenged.jun.unreal.UObject
import com.cerebrallychallenged.jun.unreal.USceneComponent
import java.util.function.BiFunction

@DslMarker
annotation class CompositeNodeDsl

abstract class CompositeNodeContainer {
    abstract val context: CompositeAssetDefinitionContext

    abstract fun attachChild(child: CompositeNode, socketName: String? = null)

    suspend inline fun <reified T : UObject> load(path: UnrealRef<T>): T = context.library.load(path)

    suspend fun link(asset: CompositeAsset, init: (CompositeNode.() -> Unit)? = null): CompositeNode {
        return asset.create(context).also {
            if (init != null) {
                it.init()
            }
            attachChild(it)
        }
    }
}

@JvmInline
value class CompositeNodeParameters(private val compositeNode: CompositeNode) {
    operator fun <T> set(parameter: CompositeParameter<T>, value: T) {
        compositeNode.context.setParameter(parameter, value)
    }
}

@CompositeNodeDsl
abstract class CompositeNode internal constructor(
        override val context: CompositeAssetDefinitionContext
): CompositeNodeContainer(), SceneComponentLike {
    @Suppress("LeakingThis")
    val parameters = CompositeNodeParameters(this)

    abstract val component: USceneComponent

    @PublishedApi
    internal val children = mutableListOf<CompositeNode>()

    val sockets: Map<String, USceneComponent>
        get() = context.sockets

    override fun attachChild(child: CompositeNode, socketName: String?) {
        children.add(child)
        val parentComponent = if (socketName == null) {
            component
        } else {
            findSocketParent(socketName) ?: error("""No component found with socket "$socketName"""")
        }
        child.component.attachToComponent(parentComponent, FAttachmentTransformRules.KeepRelativeTransform, socketName)
    }

    var transform: Transform3f
        get() = relativeTransform
        set(value) {
            relativeTransform = value
        }

    class Rotator {
        var quaternion: Quaternion = Quaternion.IDENTITY
            private set

        fun rotate(axis: Vec3f, angle: Angle) {
            quaternion *= Quaternion.fromAxisAngle(axis, angle)
        }
    }

    fun transform(translation: Vec3f? = null, scale: Vec3f? = null, rotations: (Rotator.() -> Unit)? = null) {
        val rotation = if (rotations != null) {
            Rotator().run {
                rotations()
                quaternion
            }
        } else {
            Quaternion.IDENTITY
        }
        transform = Transform3f(rotation, translation ?: Vec3f.ZERO, scale ?: Vec3f.ONE)
    }

    fun dispose() {
        for (child in children) {
            child.dispose()
        }
        component.destroyComponent()
    }

    open fun addInputListener(listener: (InputEvent) -> Unit) {
        for (child in children) {
            child.addInputListener(listener)
        }
    }

    inline fun <reified T : USceneComponent> walkComponents(crossinline visit: T.() -> Unit) {
        walkComponents<T, Unit> {
            visit()
            null
        }
    }

    inline fun <reified T : USceneComponent, R> walkComponents(crossinline visit: T.() -> R?): R? {
        val walk = BiFunction<BiFunction<Any, CompositeNode, R?>, CompositeNode, R?> { f, compositeNode ->
            val component = compositeNode.component
            if (component is T) {
                component.visit()?.let { return@BiFunction it }
            }
            for (child in compositeNode.children) {
                f.apply(f, child)?.let { return@BiFunction it }
            }
            null
        }
        @Suppress("UNCHECKED_CAST")
        return walk.apply(walk as BiFunction<Any, CompositeNode, R?>, this)
    }

    fun findSocketParent(socketName: String): USceneComponent? = walkComponents<USceneComponent, USceneComponent> {
        takeIf { doesSocketExist(socketName) }
    }
}

fun List<CompositeNode>.dispose() {
    for (node in this) {
        node.dispose()
    }
}

fun MutableList<CompositeNode>.disposeAndClear() {
    dispose()
    clear()
}
