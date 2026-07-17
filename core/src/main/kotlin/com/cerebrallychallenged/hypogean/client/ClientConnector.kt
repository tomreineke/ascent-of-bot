package com.cerebrallychallenged.hypogean.client

import kotlinx.coroutines.flow.Flow

class ClientConnector(
    val sendToClient: (ByteArray) -> Unit,
    val clientToServerMessages: Flow<ByteArray>,
    private val closeCallback: () -> Unit = {}
) {
    var isClosed = false
        private set

    fun close() {
        isClosed = true
        closeCallback()
    }
}
