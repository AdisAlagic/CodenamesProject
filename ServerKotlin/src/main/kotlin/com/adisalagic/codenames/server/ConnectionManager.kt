package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.objects.EventConverter
import java.util.*

object ConnectionManager {


    private val logger = Logger.getLogger(this::class)
    private val serverThread = ServerThread(
        onAnyError = {
            logger.error(it.message)
        },
        onUserConnected = { socket ->
            connections.add(UserHandler(
                socket,
                connections.size,
                onMessage = { user, msg ->
                    logger.info("${socket.inetAddress}: $msg")
                    if (user.justConnected) {
                        val isJoin = eventConverter.isJoinRequest(msg)
                        if (!isJoin) {
                            user.disconnect()
                        } else {
                            eventConverter.provide(msg)
                        }
                    }
                },
                onDisconnect = { user, exception ->
                    logger.info("User disconnected: ${exception.message}")
                    connections.remove(user)
                }
            ))
        }
    )
    private val connections = Vector<UserHandler>()

    private val eventConverter = EventConverter(
        onRequestJoin = {
            GameManager.game.generatePlayer(connections.size, it.user.nickname)
        },
        onGamePlayerList = {

        }
    )

    fun start() {
        serverThread.start()
    }

    fun stop() {
        connections.forEach {
            logger.info("Disconnecting ${it.getAddress()}")
            it.disconnect()
            synchronized(this) {
                connections.remove(it)
            }
        }
        serverThread.stop()
    }

    fun sendMessage(packetable: Packetable) {
        connections.forEach {
            it.sendMessage(packetable)
        }
    }

}