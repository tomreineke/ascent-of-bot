package com.cerebrallychallenged.hypogean.vanilla.dialogs

import com.cerebrallychallenged.hypogean.model.Actor
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction.Relation.HOSTILE
import com.cerebrallychallenged.hypogean.model.action.InitiativeCost
import com.cerebrallychallenged.hypogean.model.base.equippedItems
import com.cerebrallychallenged.hypogean.model.base.props
import com.cerebrallychallenged.hypogean.model.cascade.EffectReason
import com.cerebrallychallenged.hypogean.model.cell
import com.cerebrallychallenged.hypogean.model.dialog.Dialog
import com.cerebrallychallenged.hypogean.model.dialog.RoleMap
import com.cerebrallychallenged.hypogean.model.dialog.roleMapOf
import com.cerebrallychallenged.hypogean.model.effect.Effect
import com.cerebrallychallenged.hypogean.model.effect.EffectModifiers
import com.cerebrallychallenged.hypogean.model.effect.of
import com.cerebrallychallenged.hypogean.model.name
import com.cerebrallychallenged.hypogean.npc.behavior
import com.cerebrallychallenged.hypogean.vanilla.attributes.dialog
import com.cerebrallychallenged.hypogean.vanilla.attributes.relativeHealth
import com.cerebrallychallenged.hypogean.vanilla.behavior.StandardBehavior
import com.cerebrallychallenged.hypogean.vanilla.cascade.dealDirectEffect
import com.cerebrallychallenged.hypogean.vanilla.effects.Healing
import com.cerebrallychallenged.hypogean.vanilla.factions.DeepDrillingCorpFaction
import com.cerebrallychallenged.hypogean.vanilla.factions.ProtagonistFaction
import com.cerebrallychallenged.hypogean.vanilla.items.Weapon
import com.cerebrallychallenged.hypogean.vanilla.levels.FirstLevel.LAND_MINE_POS
import com.cerebrallychallenged.hypogean.vanilla.props.LandMine

object FirstLevelCompanionDialog : Dialog() {
    object Protagonist : Role<Actor>

    object NeutralMiningRobot : Role<Actor>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
            Protagonist playedBy activeActor,
            NeutralMiningRobot playedBy (addressee as Actor)
    )

    override val start: Node = node {
        describe("The tipped over steel bunk beds in this room are riddled with holes and seem like silent " +
            "witnesses of a battle that was fought here long ago. A rusted robot stands in what seems to be a " +
            "former bedroom. Hearing you approach, he turns his head towards you."
        )
        NeutralMiningRobot.say("You seem to have caused some stirring here.")
        val mine = world.cell[LAND_MINE_POS].props.first { it is LandMine }
        NeutralMiningRobot.say {
            +"Can you help me in getting rid of that "
            entityRef(mine)
            +" in that doorway over there?"
        }
        main
    }

    private val main: Node = node {
        Protagonist.select {
            say(repairing, "Help, I'm damaged!").availableIf {
                entityPlaying(Protagonist).relativeHealth < 0.8
            }
            say(introTellMeMore, "Tell me more about you.")
            act(attack, "Attack the ${NeutralMiningRobot.actor.name}.").availableIf {
                entityPlaying(Protagonist).equippedItems.filterIsInstance<Weapon>().any()
            }
            say(end, "Never mind.")
        }
    }

    private val repairing = node {
        NeutralMiningRobot.say("Let me see what I can do...")
        describe("The robot comes closer and, with his repair arm, eliminates some of the damages you've suffered.")
        delay(0.1f)
        dealDirectEffect(
            entityPlaying(Protagonist),
            Effect(10 of Healing),
            EffectModifiers.Empty,
            EffectReason.Named("Repairing")
        )
        main
    }

    private val attack = node {
        // In order to set a relation between actors to hostile, the factionEntity must never be null,
        // i.e. an actor should at least belong to a dummy faction that contains only himself.
        entityPlaying(NeutralMiningRobot).apply {
            faction?.setRelationTo(ProtagonistFaction, HOSTILE)
            behavior = StandardBehavior
            dialog = null
        }
        NeutralMiningRobot.say("Die!")
        end(InitiativeCost.Delta(4))
    }

    private val joinParty = node {
        entityPlaying(NeutralMiningRobot).apply {
            faction = ProtagonistFaction
            dialog = FirstLevelCompanionDialogAfterInParty
            behavior = StandardBehavior
        }
        NeutralMiningRobot.say("Yes, let's join forces. I think, there are threats down here that are better not faced alone.")
        end(InitiativeCost.Delta(1))
    }

    private val introTellMeMore = node {
        NeutralMiningRobot.say("What do you want to know?")
        tellMeMore
    }

    private val tellMeMore = node {
        Protagonist.select {
            say(whatAreYouDoingHere, "What are you doing here?")
            say(whyEveryoneSoHostile, "Why are all the other robots hostile?")
            say(neverMind, "Never mind.")
        }
    }

    private val neverMind = node {
        NeutralMiningRobot.say("Whatever.")
        main
    }

    private val whatAreYouDoingHere: Node = node {
        NeutralMiningRobot.say("I was guard robot of the ${DeepDrillingCorpFaction.name}. Decades ago I fought revolting human workers here in this very room.")
        NeutralMiningRobot.say("I was badly damaged and in the process lost my uplink and allegiance to the ${DeepDrillingCorpFaction.name}.")
        Protagonist.select {
            say(joinParty, "Do you want to join me.")
            say(introTellMeMore, "Tell me more about you.")
        }
    }

    private val whyEveryoneSoHostile: Node = node {
        NeutralMiningRobot.say("You seem to have lost your allegiance to the ${DeepDrillingCorpFaction.name} as well, " +
                "since you are no longer mining away scrap metal back in the tunnels you came from.")
        NeutralMiningRobot.say("For this they'll hunt you down.")
        tellMeMore
    }
}

object FirstLevelCompanionDialogAfterInParty : Dialog() {
    object Protagonist : Role<Actor>

    object FirstCompanion : Role<Actor>

    override fun determineRoles(activeActor: Actor, addressee: Entity): RoleMap = roleMapOf(
            Protagonist playedBy activeActor,
            FirstCompanion playedBy (addressee as Actor)
    )

    override val start: Node = node {
        describe("Your companion looks at you curiously.")
        FirstCompanion.say {
            +"You need something?"
        }
        main
    }

    private val main: Node = node {
        Protagonist.select {
            say(introTellMeMore, "Tell me more about you.")
            say(leave, "Leave me.")
            say(end, "Never mind.")
        }
    }

    private val leave = node {
        FirstCompanion.say("Really?")
        FirstLevelCompanionDialog.Protagonist.select {
            say(reallyLeave, "Yes, I'm sure?")
            say(main, "No, I wasn't thinking. Please stay.")
        }
    }

    private val reallyLeave = node {
        FirstCompanion.say("Thanks for the time together and good luck.")
        entityPlaying(FirstCompanion).apply {
            this.remove()
        }
        main
    }

    private val introTellMeMore = node {
        FirstCompanion.say("What do you want to know?")
        tellMeMore
    }

    private val tellMeMore = node {
        Protagonist.select {
            say(whatAreYouDoingHere, "What are you doing here?")
            say(whyEveryoneSoHostile, "Why are all the other robots hostile?")
            say(neverMind, "Never mind.")
        }
    }

    private val neverMind = node {
        FirstCompanion.say("Whatever.")
        main
    }

    private val whatAreYouDoingHere: Node = node {
        FirstCompanion.say("I was guard robot of the ${DeepDrillingCorpFaction.name}. Decades ago I fought revolting human workers here in this very room.")
        FirstCompanion.say("I was badly damaged and in the process lost my uplink and allegiance to the ${DeepDrillingCorpFaction.name}.")
        tellMeMore
    }

    private val whyEveryoneSoHostile: Node = node {
        FirstCompanion.say("You seem to have lost your allegiance to the ${DeepDrillingCorpFaction.name} as well, " +
                "since you are no longer mining away scrap metal back in the tunnels you came from.")
        FirstCompanion.say("For this they'll hunt you down.")
        tellMeMore
    }
}
