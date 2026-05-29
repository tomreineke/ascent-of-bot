package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction.Relation.HOSTILE
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.cell
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.relativeHealth
import com.cerebrallychallenged.hypogean.vanilla.behavior.StandardBehavior
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect
import com.cerebrallychallenged.hypogean.vanilla.effects.Healing
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.jun.math.geo.vec

object TestDialog : Dialog() {
    object Protagonist : Role<Actor>

    object Merchant : Role<Actor>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
            Protagonist playedBy activeActor,
            Merchant playedBy (addressee as Actor)
    )

    override val start: Node = node {
        describe("Happy little trees are swaying in the wind.")
        describe("The merchant seems nervous.")
        Merchant.say("Welcome, dear customer!")
        val cell = world.cell[vec(2, 2)]
        Merchant.say {
            +"OMG look at the mine "
            entityRef(cell, "over there")
            +"!"
        }
        main
    }

    private val main: Node = node {
        Protagonist.select {
            say(repairing, "Help, I'm damaged!").availableIf {
                entityPlaying(Protagonist).relativeHealth < 0.8
            }
            say(introTellMeMore, "Tell me more about you.")
            act(attackMerchant, "Attack the merchant.").availableIf {
                entityPlaying(Protagonist).equippedItems.filterIsInstance<Weapon>().any()
            }
            say(end, "Nevermind.")
        }
    }

    private val repairing = node {
        Merchant.say("Of course I help my fellow customer!")
        describe("The merchant pulls out an emergency repair kit.")
        dealDirectEffect(
            entityPlaying(Protagonist),
            Effect(10 of Healing),
            EffectModifiers.Empty,
            EffectReason.Named("Repairing")
        )
        main
    }

    private val attackMerchant = node {
        // In order to set a relation between actors to hostile, the factionEntity must never be null,
        // i.e. an actor should at least belong to a dummy faction that contains only himself.
        entityPlaying(Merchant).apply {
            faction?.setRelationTo(ProtagonistFaction, HOSTILE)
            behavior = StandardBehavior
            dialog = null
        }
        Merchant.say("Die!")
        end(InitiativeCost.Delta(4))
    }

    private val introTellMeMore = node {
        Merchant.say("What do you want to know?")
        tellMeMore
    }

    private val tellMeMore = node {
        Protagonist.select {
            say(meaningOfLife, "What is the meaning of life?")
            say(offeredProducts, "Which kind of goods do you offer?")
            say(neverMind, "Nevermind.")
        }
    }

    private val neverMind = node {
        Merchant.say("Whatever.")
        main
    }

    private val meaningOfLife: Node = node {
        Merchant.say("42, I guess.")
        tellMeMore
    }

    private val offeredProducts: Node = node {
        Merchant.say("None of your business.")
        Merchant.say {
            +"Besides that... "
            +"""Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut
                |labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores
                |et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.
                |Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut
                |labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores
                |et ea rebum. Stet clita kasd gubergren,
                |no sea takimata sanctus est Lorem ipsum dolor sit amet.""".trimMargin()
        }
        tellMeMore
    }
}
