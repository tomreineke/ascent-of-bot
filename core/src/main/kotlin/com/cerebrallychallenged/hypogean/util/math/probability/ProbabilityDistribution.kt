package com.cerebrallychallenged.hypogean.util.math.probability

import kotlin.random.Random

typealias ProbabilityDistribution<T> = (Random) -> T

fun <T> ProbabilityDistribution<T>.filter(predicate: (T) -> Boolean): ProbabilityDistribution<T> {
    return { random ->
        var result: T
        do {
            result = this(random)
        } while (!predicate(result))
        result
    }
}

fun <T, U> ProbabilityDistribution<T>.map(mapping: (T) -> U): ProbabilityDistribution<U> {
    return { random ->
        mapping(this(random))
    }
}

fun <T, U> ProbabilityDistribution<T>.flatMap(mapping: (T) -> (Random) -> U): ProbabilityDistribution<U> {
    return { random ->
        mapping(this(random))(random)
    }
}

fun <T> constantDistribution(value: T): ProbabilityDistribution<T> = { _ -> value }

fun <T> uniformDistribution(list: List<T>): ProbabilityDistribution<T> = { random -> list[random.nextInt(list.size)] }

fun <T1, T2, R> jointDistribution(
        distribution1: ProbabilityDistribution<T1>,
        distribution2: ProbabilityDistribution<T2>,
        joiner: (T1, T2) -> R
): ProbabilityDistribution<R> {
    return { random ->
        joiner(distribution1(random), distribution2(random))
    }
}