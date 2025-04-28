package com.cerebrallychallenged.hypogean.view.inibar

import com.cerebrallychallenged.hypogean.gui.GuiConfig.guiScale
import com.cerebrallychallenged.hypogean.gui.GuiLayer
import com.cerebrallychallenged.hypogean.gui.ImageResource
import com.cerebrallychallenged.hypogean.gui.ResourceLibrary
import com.cerebrallychallenged.hypogean.gui.get
import com.cerebrallychallenged.hypogean.gui.node
import com.cerebrallychallenged.hypogean.gui.scaled
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.IniHolder
import com.cerebrallychallenged.hypogean.model.Recon
import com.cerebrallychallenged.hypogean.model.StatusEffect
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.activeIniHolder
import com.cerebrallychallenged.hypogean.model.isAlive
import com.cerebrallychallenged.hypogean.model.maps.Entity2ObjectMap
import com.cerebrallychallenged.hypogean.view.ActionInputStateChanged
import com.cerebrallychallenged.hypogean.view.EntityHovered
import com.cerebrallychallenged.hypogean.view.ModelChange
import com.cerebrallychallenged.hypogean.view.View
import com.cerebrallychallenged.hypogean.view.ViewModelChange
import com.cerebrallychallenged.hypogean.view.conf.ViewFactory
import com.cerebrallychallenged.jun.math.geo.vec
import com.cerebrallychallenged.jun.skiatree.layout.Align
import com.cerebrallychallenged.jun.skiatree.layout.Visibility.Companion.visibleIf
import kotlinx.coroutines.CompletableDeferred
import kotlin.math.max

class IniBarView(context: ViewFactory.Context) : View, FactionContext by context {
    @Suppress("MemberVisibilityCanBePrivate") // Members shall be accessible from mods.
    object Style {
        var top = 40

        var complexMovementY = 800

        var activeLeft = 40

        var slotLeft = 640

        var spacing = 28

        var iniHolderWidth: Int = 400

        var dividerWidth: Int = 40

        var movementSpeed = 6000

        var opacitySpeed: Float = 1.0f

        val DefaultTimeSlotDividerImage = ImageResource("Images/time-slot-divider.png")

        var timeSlotDividerImage: ImageResource = DefaultTimeSlotDividerImage
    }

    class Factory : ViewFactory {
        override suspend fun create(context: ViewFactory.Context): View = IniBarView(context)
    }

    inner class ChangeVisitor : WorldChange.SimpleSuspendVisitor {
        var needsLayoutUpdate = false
            private set

        private fun iniHolder(entity: Entity): IniHolder? =
            entity as? IniHolder ?: (entity as? StatusEffect)?.bearer as? IniHolder

        override suspend fun visit(change: WorldChange.Clear) {
            needsLayoutUpdate = true
        }

        override suspend fun visit(change: WorldChange.Removed) {
            when (val entity = change.entity) {
                is IniHolder -> {
                    visIniHolders.remove(entity)?.let {
                        it.isDeathCandidate = true
                        it.targetOpacity = 0.0f
                        deathCandidates.add(it)
                    }
                    needsLayoutUpdate = true
                }
                is StatusEffect -> {
                    if (entity.bearer is IniHolder) {
                        visIniHolders[entity.bearer]?.onChange(change)
                    }
                }
            }
        }

        override suspend fun visit(change: WorldChange.StatusEffectCreated) {
            val (statusEffect) = change
            (statusEffect.bearer as? IniHolder)?.let {
                visIniHolders[it]?.onChange(change)
            }
        }

        override suspend fun visit(change: WorldChange.ActorCreated) {
            val (actor) = change
            if (actor.isAlive()) {
                iniHolderCreated(actor)
                needsLayoutUpdate = true
            }
        }

        override suspend fun visit(change: WorldChange.EventCreated) {
            val (event) = change
            if (event.isAlive()) {
                iniHolderCreated(event)
                needsLayoutUpdate = true
            }
        }

        override suspend fun visit(change: WorldChange.FactionMembershipChanged) {
            val (entity) = change
            iniHolder(entity)?.let {
                visIniHolders[it]?.onChange(change)
                needsLayoutUpdate = true
            }
        }

        override suspend fun visit(change: WorldChange.ReconChanged) {
            val (entity, _) = change
            iniHolder(entity)?.let {
                visIniHolders[it]?.onChange(change)
                needsLayoutUpdate = true
            }
        }

        override suspend fun visit(change: WorldChange.IniEnqueue) {
            val (_, iniHolder) = change
            if (visIniHolders[iniHolder] == null && iniHolder.isAlive()) {
                iniHolderEnqueued(iniHolder)
            }
            needsLayoutUpdate = true
        }

        override suspend fun visit(change: WorldChange.IniDequeue) {
            needsLayoutUpdate = true
        }

        override suspend fun visit(change: WorldChange.IniRemove) {
            needsLayoutUpdate = true
        }

        override suspend fun visit(change: WorldChange.IniIncTime) {
            needsLayoutUpdate = true
        }

        override suspend fun visit(change: WorldChange.ActiveStateChanged) {
            needsLayoutUpdate = true
        }

        override suspend fun <T> visit(change: WorldChange.AttributeChanged<T>) {
            val entity = change.entity
            iniHolder(entity)?.let {
                visIniHolders[it]?.onChange(change)
            }
            if (entity is FactionEntity) {
                for (actor in entity.actors) {
                    visIniHolders[actor]?.onChange(change)
                }
            }
        }
    }

    internal val timeSlotDividerImage =
        ResourceLibrary[Style.timeSlotDividerImage].resize(vec(Style.dividerWidth.scaled, Style.iniHolderWidth.scaled))

    private val world = context.world

    internal val viewModel = context.viewModel

    internal val movementSpeed = Style.movementSpeed * guiScale

    private val visIniHolders = Entity2ObjectMap<IniHolder, VisIniHolder>(world)

    private val deathCandidates: MutableList<VisElement> = mutableListOf()

    internal val timeSlotDividers = ArrayDeque<VisTimeSlotDivider>()

    private val mainNode = context.widget.layers[GuiLayer.Base].node {
        horizontalAlign = Align.Stretch
        verticalAlign = Align.Stretch
        consumesHover = false
    }

    private val iniArrowNode = IniArrowNode(context.widget, this)

    private var animationCompleted: CompletableDeferred<Unit>? = null

    internal var currentActiveVisIniHolder: VisIniHolder? = null
        private set

    private var currentHoveredIniHolder: IniHolder? = null

    private var latestOccupiedDividerIndex = 0

    private suspend fun onChanges(changes: Iterable<WorldChange>) {
        animationCompleted?.let {
            it.complete(Unit)
            animationCompleted = null
        }

        val changeVisitor = ChangeVisitor()
        for (change in changes) {
            change.accept(changeVisitor)
        }
        if (changeVisitor.needsLayoutUpdate) {
            updateLayout()
            CompletableDeferred<Unit>().also {
                animationCompleted = it
                it.await()
            }
        }
    }

    private fun iniHolderCreated(iniHolder: IniHolder) {
        visIniHolders[iniHolder] = VisIniHolder(this, iniHolder).also {
            mainNode.children.add(it)
        }
    }

    /**
     * This method is for example for events that have been created at level start but are only
     * now getting activated. Cf. ActivateFireFlaresDialog.
     */
    private fun iniHolderEnqueued(iniHolder: IniHolder) {
        iniHolderCreated(iniHolder)
        iniHolder.world.updateRecon()
    }

    override suspend fun onViewModelChange(change: ViewModelChange) {
        when (change) {
            is ModelChange -> onChanges(change.changes)
            is ActionInputStateChanged -> {
                if (change.hasHoverChanged) {
                    val actionTable = change.newState.hovered
                    val estimatedConsequences = actionTable.instances.singleOrNull()?.estimatedConsequences
                    if (estimatedConsequences == null) {
                        for (element in visIniHolders.values) {
                            element.updateEstimatedConsequences(null)
                        }
                    } else {
                        for ((iniHolder, element) in visIniHolders) {
                            element.updateEstimatedConsequences(estimatedConsequences[iniHolder])
                        }
                    }
                    iniArrowNode.updateArrow(actionTable)
                }
            }
            is EntityHovered -> {
                val hoveredEntity = change.entity
                if (hoveredEntity != currentHoveredIniHolder) {
                    visIniHolders[currentHoveredIniHolder]?.isHovered = false
                    currentHoveredIniHolder = null
                }
                if (hoveredEntity is IniHolder) {
                    visIniHolders[hoveredEntity]?.isHovered = true
                    currentHoveredIniHolder = hoveredEntity
                }
            }
            else -> {}
        }
    }

    private fun updateLayout() {
        val currentIniTime = world.currentIniTime
        for (iniTime in (timeSlotDividers.lastOrNull()?.iniTime ?: currentIniTime) + 1..currentIniTime + 20) {
            timeSlotDividers.add(VisTimeSlotDivider(this, iniTime).also {
                mainNode.children.add(it)
            })
        }

        while (timeSlotDividers.firstOrNull()?.let { it.iniTime <= currentIniTime } == true) {
            timeSlotDividers.removeFirst().also {
                it.isDeathCandidate = true
                it.targetLeft = -100.0f
                deathCandidates.add(it)
            }
        }

        val activeIniHolder = world.activeIniHolder
        currentActiveVisIniHolder = null
        if (activeIniHolder != null) {
            visIniHolders[activeIniHolder]?.let { element ->
                element.updateLayout(Style.activeLeft * guiScale)
                element.isActive = true
                currentActiveVisIniHolder = element
            }
        }
        var cursor = Style.slotLeft * guiScale
        val iniHolderDeltaX = (Style.iniHolderWidth + Style.spacing).scaled
        val dividerDeltaX = (Style.dividerWidth + Style.spacing).scaled
        val slots = world.iniQueue.slots.iterator()
        latestOccupiedDividerIndex = -1
        for ((dividerIndex, divider) in timeSlotDividers.withIndex()) {
            val slot = if (slots.hasNext()) slots.next() else listOf()
            for (holder in slot) {
                visIniHolders[holder]?.let { element ->
                    element.isActive = false
                    if (holder.recon == Recon.Visible) {
                        element.targetOpacity = 1.0f
                        element.updateLayout(cursor)
                        cursor += iniHolderDeltaX
                        latestOccupiedDividerIndex = dividerIndex
                    } else {
                        element.targetOpacity = 0.0f
                    }
                }
            }
            divider.updateLayout(cursor)
            cursor += dividerDeltaX
        }
        updateDividerVisibility(-1)
    }

    internal fun updateDividerVisibility(hoveredTime: Int) {
        val firstInvisibleIndex = max(latestOccupiedDividerIndex, hoveredTime)
        for ((dividerIndex, divider) in timeSlotDividers.withIndex()) {
            divider.visibility = visibleIf(dividerIndex < firstInvisibleIndex)
        }
    }

    private fun animate(elements: MutableIterable<VisElement>, deltaSeconds: Float): Boolean {
        val iterator = elements.iterator()
        var anyChange = false
        while (iterator.hasNext()) {
            val element = iterator.next()
            if (element.animate(deltaSeconds)) {
                anyChange = true
            } else if (element.isDeathCandidate) {
                iterator.remove()
                element.detach()
            }
        }
        return anyChange
    }

    override fun onTick(deltaSeconds: Float) {
        animationCompleted?.let {
            val anyChange = (
                animate(visIniHolders.values, deltaSeconds)
                or animate(timeSlotDividers, deltaSeconds)
                or animate(deathCandidates, deltaSeconds)
            )
            if (!anyChange) {
                animationCompleted = null
                it.complete(Unit)
            }
        }
    }
}
