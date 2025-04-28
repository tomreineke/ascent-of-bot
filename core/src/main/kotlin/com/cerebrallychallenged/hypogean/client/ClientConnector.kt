package com.cerebrallychallenged.hypogean.client

import kotlinx.coroutines.flow.Flow

internal class ClientConnector(
    val sendToClient: (ByteArray) -> Unit,
    val clientToServerMessages: Flow<ByteArray>
)
