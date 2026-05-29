package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.modding.Feature
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.cascade.EffectConsequence
import com.cerebrallychallenged.hypogean.model.cascade.EffectResult
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import kotlin.reflect.KClass

class EffectConsequences : Feature {
    private val listByKind: MutableMap<EffectKind, Object2IntMap<EffectConsequenceFactory>> = Object2ObjectOpenHashMap()

    inline fun <reified EC : EffectConsequence> register(
        kind: EffectKind,
        noinline factory: (Entity, EffectResult) -> EC,
        sign: Int
    ) {
        internalRegister(kind, EffectConsequenceFactory(factory, EC::class, Unit), sign)
    }

    @PublishedApi
    internal fun internalRegister(kind: EffectKind, consequentPhase: EffectConsequenceFactory, sign: Int) {
        listByKind.computeIfAbsent(kind) { Object2IntArrayMap() }[consequentPhase] = sign
    }

    operator fun get(kind: EffectKind): Sequence<Object2IntMap.Entry<EffectConsequenceFactory>> =
        listByKind[kind]?.object2IntEntrySet()?.asSequence() ?: sequenceOf()
}

class EffectConsequenceFactory(
    private val factory: (Entity, EffectResult) -> EffectConsequence,
    val phaseClass: KClass<out EffectConsequence>,
    private val additionalParameter: Any?
) {
    fun create(entity: Entity, effectResult: EffectResult): EffectConsequence = factory(entity, effectResult)

    override fun hashCode(): Int = 31 * phaseClass.hashCode() + additionalParameter.hashCode()

    override fun equals(other: Any?): Boolean =
        other is EffectConsequenceFactory
                && phaseClass == other.phaseClass
                && additionalParameter == other.additionalParameter
}
