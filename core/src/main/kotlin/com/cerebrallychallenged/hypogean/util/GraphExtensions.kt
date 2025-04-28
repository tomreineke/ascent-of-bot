@file:Suppress("UnstableApiUsage")

package com.cerebrallychallenged.hypogean.util

import com.google.common.graph.EndpointPair
import com.google.common.graph.Graph
import com.google.common.graph.Graphs
import com.google.common.graph.Traverser

operator fun <N : Any> EndpointPair<N>.component1(): N = source()

operator fun <N : Any> EndpointPair<N>.component2(): N = target()

/**
 * Returns a topological ordering of this if this is acyclic.
 *
 * @param checkAcyclicity determines the behavior if this graph is cyclic.
 * If `true`, an exception is thrown for cyclic graphs.
 * If `false` and this is cyclic, an order is returned which is no extension of this.
 */
fun <N> Graph<N>.topologicalOrdering(checkAcyclicity: Boolean = false): Iterable<N> {
    if (checkAcyclicity) require(!Graphs.hasCycle(this))
    return Traverser.forGraph(this::predecessors).depthFirstPostOrder(nodes())
}
