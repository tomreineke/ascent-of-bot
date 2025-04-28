package com.cerebrallychallenged.hypogean.modding

import com.cerebrallychallenged.hypogean.model.attribute.Attributes
import com.cerebrallychallenged.hypogean.model.rulebookSpecificationError
import com.cerebrallychallenged.hypogean.util.topologicalOrdering
import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class ModContext {
    val features = Features().apply {
        register(this)
    }

    inline fun <reified F : Feature> feature(): F = features.itemByClass()

    inline fun <reified F : Feature> configure(f: F.() -> Unit) {
        with(features.itemByClass<F>()) {
            f()
        }
    }
}

interface Mod {
    fun ModContext.setupFeatureDiscovery() {}

    fun ModContext.installCodecsAndStreaming() {}

    fun ModContext.install() {}

    fun ModContext.postInstall() {}

    val id: String
        get() = this::class.jvmName
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ModDependencies(vararg val dependencies: KClass<out Mod>)

@Suppress("UnstableApiUsage")
internal fun List<Mod>.install(): Features {
    val modsByClass = associateBy { it::class }
    val modGraph: MutableGraph<Mod> = GraphBuilder.directed().expectedNodeCount(size).build()
    for (mod in this) {
        modGraph.addNode(mod)
    }
    for (mod in this) {
        mod::class.annotations.filterIsInstance<ModDependencies>().firstOrNull()?.let { dependencies ->
            for (requiredModClass in dependencies.dependencies) {
                val requiredMod = modsByClass[requiredModClass]
                        ?: rulebookSpecificationError("Mod ${mod.id} depends on missing mod ${requiredModClass.jvmName}")
                modGraph.putEdge(requiredMod, mod)
            }
        }
    }
    return with(ModContext()) {
        val orderedMods = modGraph.topologicalOrdering(checkAcyclicity = true).toList()
        fun withEachMod(f: Mod.() -> Unit) {
            for (mod in orderedMods) {
                mod.f()
            }
        }
        withEachMod { setupFeatureDiscovery() }
        withEachMod { installCodecsAndStreaming() }
        withEachMod { install() }
        withEachMod { postInstall() }
        features.itemByClass<Attributes>().triggerDelegateProviders()
        features
    }
}
