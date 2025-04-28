package com.cerebrallychallenged.hypogean.server

import com.cerebrallychallenged.hypogean.client.ClientConnector
import com.cerebrallychallenged.jun.log.log
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.net.BindException

internal val PORT_INTERVAL = 50190..52193

internal fun CoroutineScope.launchSocketServer(server: Server) {
    require(false) { "We need a whitelist for kryo deserializable classes first" }
    launch(Dispatchers.IO) {
        var socketServer: ServerSocket? = null
        for (port in PORT_INTERVAL) {
            try {
                socketServer = aSocket(ActorSelectorManager(Dispatchers.IO))
                    .tcp()
                    .bind(InetSocketAddress("127.0.0.1", port)) {}
                break
            } catch (_: BindException) {
            }
        }
        if (socketServer == null) {
            return@launch
        }
        socketServer.use {
            log.info { "Listening server socket at ${socketServer.localAddress}" }
            while (true) {
                val socket = socketServer.accept()
                supervisorScope {
                    // use supervisor scope so that crashing clients to not bring down server
                    log.info { "Socket accepted: ${socket.remoteAddress}" }
                    val input = socket.openReadChannel()
                    val output = socket.openWriteChannel(autoFlush = false)
                    val serverToClientChannel = Channel<ByteArray>(Channel.UNLIMITED)
                    launch {
                        serverToClientChannel.consumeAsFlow().collect {
                            output.writeInt(it.size)
                            output.writeFully(it)
                            output.flush()
                        }
                    }
                    val clientToServer = flow {
                        while (true) {
                            val byteArray = ByteArray(input.readInt())
                            input.readFully(byteArray)
                            emit(byteArray)
                        }
                    }
                    server.addClient(ClientConnector(serverToClientChannel::trySend, clientToServer), true)
                }
            }
        }
    }
}
