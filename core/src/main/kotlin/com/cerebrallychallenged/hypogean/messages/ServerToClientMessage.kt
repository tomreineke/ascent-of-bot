package com.cerebrallychallenged.hypogean.messages

import com.cerebrallychallenged.hypogean.model.ChangeScheduleDto
import com.cerebrallychallenged.hypogean.model.action.ActionInstanceId
import com.cerebrallychallenged.hypogean.model.action.ActionTable

sealed class ServerToClientMessage

internal data class HelloClient(val clientId: Long) : ServerToClientMessage()

class WorldUpdate internal constructor(
    val updateId: Long,
    internal val changeSchedule: ChangeScheduleDto
) : ServerToClientMessage()

internal class ErrorMessage(val errorMessage: String) : ServerToClientMessage()

internal class ExpandedAction(
    val actionInstanceId: ActionInstanceId,
    val children: ActionTable
) : ServerToClientMessage()
