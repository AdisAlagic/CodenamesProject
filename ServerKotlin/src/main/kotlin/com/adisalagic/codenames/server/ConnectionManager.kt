package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.objects.EventConverter
import kotlinx.serialization.json.Json
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
                connections.size,
                onMessage = { user, msg ->
                    logger.info("${socket.inetAddress}: $msg")
                    if (user.justConnected){
                        val obj = Json.decodeFromString(BaseAPI.serializer(), msg)
                        val isJoin = eventConverter.isJoinRequest(obj)
                        if (!isJoin){
                            user.disconnect()
                        }else{
                            eventConverter.provide(obj)
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

        }
    )

    fun start() {
        serverThread.start()
    }

    fun stop() {
        connections.forEach {
            logger.info("Disconnecting ${it.getAddress()}")
            it.disconnect()
            connections.remove(it)
        }
        serverThread.stop()
    }

    fun sendMessage(packetable: Packetable) {
        connections.forEach {
            it.sendMessage(packetable)
        }
    }

}