@file:Suppress("UnstableApiUsage")

package com.cerebrallychallenged.hypogean.util

import com.cerebrallychallenged.hypogean.modding.Feature
import com.cerebrallychallenged.hypogean.model.Rulebook
import com.cerebrallychallenged.hypogean.model.feature
import com.google.common.graph.GraphBuilder

abstract class Precedences<T>(@PublishedApi internal val block: PrecedenceContext<T>.() -> Unit) {
    companion object {
        inline fun <reified T : Any, reified P : Precedences<T>, reified R> orderedImplementors(
                rulebook: Rulebook
        ): List<T> where R: Iterable<P>, R: Feature {
            val graph = GraphBuilder.directed().build<T>()
            val context = PrecedenceContext<T> { (source, target) -> graph.putEdge(source, target) }
            for (precedences in rulebook.feature<R>()) {
                precedences.apply {
                    context.block()
                }
            }
            return graph.topologicalOrdering(checkAcyclicity = true).toList()
        }
    }

    class PrecedenceContext<T>(private val edgeConsumer: (Pair<T, T>) -> Unit) {
        infix fun T.precedes(other: T) {
            edgeConsumer(this to other)
        }
    }
}