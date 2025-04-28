package com.cerebrallychallenged.hypogean.model.effect

import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.attribute.SimpleIntAttribute
import com.cerebrallychallenged.jun.math.truncateToInt
import kotlin.random.Random
import kotlin.random.nextInt

interface EffectValueExpression {
    val kind: EffectKind

    fun asEffectValue(): EffectValue
}

sealed class EffectValue(override val kind: EffectKind) : EffectValueExpression {
    class Absolute internal constructor(val range: IntRange, kind: EffectKind) : EffectValue(kind) {
        override fun sample(entity: Entity, random: Random): Int = random.nextInt(range)
    }

    sealed class Relative(val percent: Int, val attribute: SimpleIntAttribute<Entity>, kind: EffectKind) : EffectValue(kind) {
        abstract fun getBaseFor(entity: Entity): Int

        override fun sample(entity: Entity, random: Random): Int =
            (getBaseFor(entity) * percent * 0.01f).truncateToInt()
    }

    class RelativeOfCurrent internal constructor(
        percent: Int,
        attribute: SimpleIntAttribute<Entity>,
        kind: EffectKind
    ) : Relative(percent, attribute, kind) {
        override fun getBaseFor(entity: Entity): Int = attribute.current.get(entity)
    }

    class RelativeOfMax internal constructor(
        percent: Int,
        attribute: SimpleIntAttribute<Entity>,
        kind: EffectKind
    ) : Relative(percent, attribute, kind) {
        override fun getBaseFor(entity: Entity): Int = attribute.max.get(entity)
    }

    override fun asEffectValue(): EffectValue = this

    abstract fun sample(entity: Entity, random: Random): Int
}

class PercentageExpression internal constructor(
    private val create: (EffectKind) -> EffectValue.Relative
) {
    infix fun of(kind: EffectKind): EffectValue = create(kind)
}

infix fun IntRange.of(kind: EffectKind): EffectValue = EffectValue.Absolute(this, kind)

infix fun Int.percentOfCurrent(attribute: SimpleIntAttribute<Entity>): PercentageExpression = PercentageExpression {
    EffectValue.RelativeOfCurrent(this, attribute, it)
}

infix fun Int.percentOfMax(attribute: SimpleIntAttribute<Entity>): PercentageExpression = PercentageExpression {
    EffectValue.RelativeOfMax(this, attribute, it)
}
