package com.cerebrallychallenged.hypogean.client

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal fun CoroutineScope.launchSocketClient(clientConnector: ClientConnector, address: SocketAddress) {
    launch(Dispatchers.IO) {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(address) {}
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = false)
        launch {
            clientConnector.clientToServerMessages.collect {
                output.writeInt(it.size)
                output.writeFully(it)
                output.flush()
            }
        }
        while (true) {
            val byteArray = ByteArray(input.readInt())
            input.readFully(byteArray)
            clientConnector.sendToClient(byteArray)
        }
    }
}
