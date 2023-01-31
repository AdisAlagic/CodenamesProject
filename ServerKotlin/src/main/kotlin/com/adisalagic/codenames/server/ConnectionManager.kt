package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.objects.EventConverter
import com.adisalagic.codenames.server.objects.game.PlayerInfo
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
                        }
                    }
                    eventConverter.provide(msg)
                },
                onDisconnect = { user, exception ->
                    logger.info("User ${user.getAddress()}:${user.getPort()} disconnected: ${exception.message}")
                    connections.remove(user)
                    GameManager.removeUser(user.getId())
                }
            ))
        }
    )
    private val connections = Vector<UserHandler>()

    private val eventConverter = EventConverter(
        onRequestJoin = {
            val player = GameManager.game.generatePlayer(connections.size - 1, it.user.nickname)
            val conPlayer = connections.find { user -> user.getId() == player.id }
            conPlayer?.sendMessage(PlayerInfo(PlayerInfo.User(
                color = player.color,
                id = player.id,
                isHost = player.isHost,
                nickname = player.nickname,
                role = player.role.name.lowercase(),
                team = player.team.name.lowercase()
            )))
        },
        onGamePlayerList = {

        },
        onRequestJoinTeam = {
            logger.info("Move request of player ${it.request.user.id}")
            GameManager.game.changeTeamOrRole(
                id = it.request.user.id,
                role = it.request.user.role,
                team = it.request.user.team
            )
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