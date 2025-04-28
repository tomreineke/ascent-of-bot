package com.cerebrallychallenged.hypogean.view

import com.cerebrallychallenged.hypogean.activestate.ActiveActorState
import com.cerebrallychallenged.hypogean.client.Client
import com.cerebrallychallenged.hypogean.messages.SetAttribute
import com.cerebrallychallenged.hypogean.messages.SubmitAction
import com.cerebrallychallenged.hypogean.messages.SubmitDialogOption
import com.cerebrallychallenged.hypogean.model.Animation
import com.cerebrallychallenged.hypogean.model.ChangeScheduleAnimation
import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.Entity
import com.cerebrallychallenged.hypogean.model.Faction
import com.cerebrallychallenged.hypogean.model.FactionContext
import com.cerebrallychallenged.hypogean.model.FactionEntity
import com.cerebrallychallenged.hypogean.model.World
import com.cerebrallychallenged.hypogean.model.WorldChange
import com.cerebrallychallenged.hypogean.model.action.ActionInstance
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceCompleteness
import com.cerebrallychallenged.hypogean.model.action.ActionTable
import com.cerebrallychallenged.hypogean.model.action.ViewActionInstance
import com.cerebrallychallenged.hypogean.model.action.completeInstances
import com.cerebrallychallenged.hypogean.model.action.partialInstances
import com.cerebrallychallenged.hypogean.model.action.viewActions
import com.cerebrallychallenged.hypogean.model.attribute.Attribute
import com.cerebrallychallenged.hypogean.model.modelError
import com.cerebrallychallenged.hypogean.vanilla.actions.PickupActionInstance
import com.cerebrallychallenged.hypogean.view.input.InputCommand
import com.cerebrallychallenged.jun.input.InputEvent
import com.cerebrallychallenged.jun.input.InputReason
import com.cerebrallychallenged.jun.input.Key
import com.cerebrallychallenged.jun.input.KeyEvent
import com.cerebrallychallenged.jun.input.MouseEvent
import com.cerebrallychallenged.jun.log.log
import com.cerebrallychallenged.jun.math.geo.Vec2f
import com.cerebrallychallenged.jun.math.geo.Vec2i
import com.cerebrallychallenged.jun.unreal.impactPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

object ShiftDebugMode : InputCommand("I")

object ShiftPartialMode : InputCommand("LeftShift")

class ViewModel internal constructor(
    internal val client: Client,
    internal val world: World,
    val clientFaction: Faction,
    private val sessionScope: CoroutineScope
) : FactionContext {
    override val ownFactionEntity: FactionEntity = with(world) { clientFaction.entity }

    /**
     * We put all changes of the ViewModel through a channel such that each change is synchronously processed by all
     * views and there is no nesting problem when a reaction to ViewModel changes causes other changes of the ViewModel.
     */
    internal val sendChannel: SendChannel<ViewModelChange>

    internal val changes: Flow<ViewModelChange>

    init {
        sendChannel = Channel(Channel.UNLIMITED)
        changes = sendChannel.consumeAsFlow()
    }

    internal var actionInputState: ActionInputState = ActionInputState.Empty
        set(value) {
            val prev = field
            if (prev != value) {
                field = value
                sendChannel.trySend(ActionInputStateChanged(prev, value))
            }
        }

    private val pressedCommands: MutableSet<InputCommand> = mutableSetOf()

    private val visibleModalViews: MutableSet<View> = mutableSetOf()

    private var changeScheduleAnimation: ChangeScheduleAnimation? = null

    fun onTick(deltaSeconds: Float) {
        changeScheduleAnimation?.onTick(deltaSeconds)
    }

    fun addAnimation(animatable: Animation) {
        val changeScheduleAnimation = changeScheduleAnimation
        if (changeScheduleAnimation != null) {
            changeScheduleAnimation.addAnimation(animatable)
            animatable.onStart()
        } else {
            log.warn { "Trying to add animatable while there is no changeScheduleAnimation." }
        }
    }

    internal suspend fun onChangeSchedule(schedule: ChangeScheduleDto) {
        val animation = ChangeScheduleAnimation(schedule, world) {
            for (change in it) {
                onChange(change)
            }
            sendChannel.trySend(ModelChange(it))
        }
        changeScheduleAnimation = animation
        animation.await()
        changeScheduleAnimation = null
    }

    internal fun onChange(change: WorldChange) {
        when (change) {
            is WorldChange.Clear -> {
                hoverManager.clear()
                actionInputState = ActionInputState.Empty
            }
            is WorldChange.ActiveStateChanged -> {
                selectViewActions()
            }
            else -> {}
        }
    }

    fun hoverActions(newHovered: ActionTable, reason: InputReason) {
        hoverManager.updateActions(newHovered, reason)
    }

    fun selectActions(newSelected: ActionTable) {
        if (actionInputState.selected != newSelected) {
            actionInputState = actionInputState.copy(
                selected = newSelected,
                prefix = null,
                originalPrefix = null,
                hovered = ActionTable.Empty,
            )
        }
    }

    fun selectViewActions() {
        selectActions(world.viewActions(clientFaction))
    }

    val isDebugMode: Boolean
        get() = ShiftDebugMode in pressedCommands

    private fun setActionCompleteness(newCompleteness: ActionInstanceCompleteness) {
        if (actionInputState.completeness != newCompleteness) {
            actionInputState = actionInputState.copy(completeness = newCompleteness)
        }
    }

    fun submitAction(actionInstance: ActionInstance) {
        when {
            !actionInstance.canBeComplete -> modelError("Submitted action must be complete")
            actionInstance is ViewActionInstance -> {
                if (!actionInstance.isDebugOnly || isDebugMode) {
                    sendChannel.trySend(ViewActionExecuted(actionInstance))
                }
            }
            else -> {
                client.sendToServer(SubmitAction(
                    actionInstance,
                    world.activeState as? ActiveActorState
                        ?: modelError("SubmitAction requires ActiveActorState")
                ))
                actionInputState = ActionInputState.Empty
                sendChannel.trySend(ActionSubmitted(actionInstance))
            }
        }
    }

    private fun setActionPrefix(prefix: ActionInstance) {
        sessionScope.launch {
            val children = client.getChildrenOf(world, prefix)
            actionInputState = actionInputState.copy(
                selected = children,
                prefix = prefix,
                originalPrefix = actionInputState.originalPrefix ?: prefix,
                hovered = ActionTable.Empty
            )
        }
    }

    fun submitDialogOption(optionUuid: String) {
        client.sendToServer(SubmitDialogOption(optionUuid))
    }

    fun submitInfoDisplay(displayInfo: DisplayInfo) {
        sendChannel.trySend(displayInfo)
    }

    fun <T> setAttribute(entity: Entity, attribute: Attribute<T>, value: T) {
        // FIXME[T] leads to an ActivateActor event, which makes the action bar forget that an action has been selected.
        client.sendToServer(SetAttribute(entity, attribute, value))
    }

    fun setGuiScale(scale: Double) {
        sendChannel.trySend(GuiScaled(scale))
    }

    fun createInputListenerFor(entity: Entity): (InputEvent) -> Unit = { inputEvent: InputEvent ->
        onInputEvent(inputEvent, entity, InputReason.Unreal3D)
        inputEvent.consume()
    }

    private val hoverManager = HoverManager(this)

    /**
     * Called from views for mouse events on UI elements representing entities.
     * Clicks are assumed to be performed by the left mouse button.
     */
    fun onEntityMouseEvent(entity: Entity, kind: MouseEvent.Kind, reason: InputReason) {
        onInputEvent(
            MouseEvent(kind, Vec2i.ZERO, Key.LEFT_MOUSE_BUTTON.takeIf { kind == MouseEvent.Kind.CLICK }),
            entity,
            reason
        )
    }

    /**
     * Called from [ViewManager.onInput]. Since the general input listener is informed only after individual components,
     * and clicks on entities are generally consumed, an unconsumed mouse click event indicates that the click was
     * performed in the `nowhere`, i.e., not on any entity.
     */
    internal fun onInput(inputEvent: InputEvent, commands: Collection<InputCommand>) {
        if (inputEvent is KeyEvent) {
            when (inputEvent.kind) {
                KeyEvent.Kind.PRESS -> {
                    for (command in commands) {
                        if (pressedCommands.add(command)) {
                            onInputCommandChanged(command, true)
                        }
                    }
                }
                KeyEvent.Kind.RELEASE -> {
                    for (command in commands) {
                        if (pressedCommands.remove(command)) {
                            onInputCommandChanged(command, false)
                        }
                    }
                }
            }
        }
        if (!inputEvent.isConsumed) {
            onInputEvent(inputEvent, null, InputReason.Unreal3D)
        }
    }

    private fun onInputCommandChanged(inputCommand: InputCommand, pressed: Boolean) {
        if (inputCommand == ShiftPartialMode) {
            setActionCompleteness(
                    if (pressed) ActionInstanceCompleteness.Partial else ActionInstanceCompleteness.Complete
            )
        }
    }

    private var lastRoundedMouseGroundPosition: Vec2i = Vec2i.ZERO

    private fun onInputEvent(inputEvent: InputEvent, entity: Entity?, reason: InputReason) {
        when (inputEvent) {
            is MouseEvent -> {
                val mouseGroundPosition = inputEvent.hitResult?.let { it.impactPoint.xy / 100.0f }
                val roundedMouseGroundPosition = mouseGroundPosition?.round()
                when (inputEvent.kind) {
                    MouseEvent.Kind.CLICK -> {
                        when (inputEvent.button) {
                            Key.LEFT_MOUSE_BUTTON -> {
                                onLeftClick(inputEvent, mouseGroundPosition, entity)
                            }
                            Key.RIGHT_MOUSE_BUTTON -> {
                                selectViewActions()
                            }
                        }
                    }
                    MouseEvent.Kind.ENTER -> {
                        hoverManager.updateEntity(mouseGroundPosition, entity, reason)
                    }
                    MouseEvent.Kind.MOVE -> {
                        if (entity != null && roundedMouseGroundPosition != lastRoundedMouseGroundPosition) {
                            hoverManager.updateEntity(mouseGroundPosition, entity, reason)
                        }
                    }
                    MouseEvent.Kind.LEAVE -> {
                        hoverManager.updateEntity(mouseGroundPosition, null, reason)
                    }
                    else -> {}
                }
                if (roundedMouseGroundPosition != null) {
                    lastRoundedMouseGroundPosition = roundedMouseGroundPosition
                }
            }
            else -> {}
        }
    }

    private fun onLeftClick(inputEvent: MouseEvent, mouseGroundPosition: Vec2f?, entity: Entity?) {
        val actionInputState = actionInputState
        if (actionInputState.hovered.hasInstances()) {
            val hoveredActions = actionInputState.hovered.findHoveredActions(mouseGroundPosition, entity, isDebugMode)
            when (actionInputState.completeness) {
                ActionInstanceCompleteness.Complete -> {
                    submit(hoveredActions.completeInstances.toList(), ::submitAction)
                }
                ActionInstanceCompleteness.Partial -> {
                    submit(hoveredActions.partialInstances.toList(), ::setActionPrefix)
                }
            }
        } else {
            inputEvent.consume()
        }
    }

    private fun submit(
        instances: List<ActionInstance>,
        submit: (ActionInstance) -> Unit
    ) {
        when {
            instances.size == 1 -> {
                submit(instances.single())
            }
            instances.size > 1 -> {
                val pickupActions = instances.filterIsInstance<PickupActionInstance>()
                val onlyPickupActions = pickupActions.size == instances.size
                if (onlyPickupActions) {
                    sendChannel.trySend(PickupRadialViewDisplay(pickupActions, submit))
                } else {
                    val nonPickupAction = instances.first { it !is PickupActionInstance }
                    error("Need to implement radial menu for $nonPickupAction")
                }

            }
        }
    }

    private var isAnyModalViewVisible: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                sendChannel.trySend(ModalViewVisibilityChanged(value))
            }
        }

    /**
     * If isVisible is true, all the evading views are hidden, except for the specified view.
     *
     * @see ModalViewVisibilityChanged
     */
    fun updateModalViewVisibility(view: View, isVisible: Boolean) {
        if (isVisible) {
            visibleModalViews.add(view)
        } else {
            visibleModalViews.remove(view)
        }
        isAnyModalViewVisible = visibleModalViews.isNotEmpty()
    }

    fun pauseAnimation(token: Any) {
        changeScheduleAnimation?.pauseAnimation(token)
    }

    fun resumeAnimation(token: Any) {
        changeScheduleAnimation?.resumeAnimation(token)
    }
}
