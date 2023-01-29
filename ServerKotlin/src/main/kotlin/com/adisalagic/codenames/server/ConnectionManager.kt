package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import java.util.Vector
import java.util.concurrent.ConcurrentSkipListSet

object ConnectionManager {
    private val logger = Logger.getLogger(this::class)
    private val serverThread = ServerThread(
        onAnyError = {
            logger.error(it.message)
        },
        onUserConnected = { socket ->
            connections.add(UserHandler(
                socket,
                onMessage = { logger.info("${socket.inetAddress}: $it") },
                onDisconnect = { user, exception ->
                    logger.info("User disconnected: ${exception.message}")
                    connections.remove(user)
                }
            ))
        }
    )
    private val connections = Vector<UserHandler>()

    fun start() {
        serverThread.start()
    }

    fun stop(){
        connections.forEach {
            logger.info("Disconnecting ${it.getAddress()}")
            it.disconnect()
            connections.remove(it)
        }
        serverThread.stop()
    }

}