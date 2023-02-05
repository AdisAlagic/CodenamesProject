package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.objects.EventConverter
import com.adisalagic.codenames.server.objects.game.PlayerInfo
import com.adisalagic.codenames.utils.asNetGameState
import com.adisalagic.codenames.utils.isHost
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
                    logger.info("User ${user.getAddress()}:${user.getPort()} disconnected: ${exception.message} (${exception.cause})")
                    connections.remove(user)
                    GameManager.game.deleteUser(user.getId())
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
            conPlayer?.sendMessage(GameManager.game.getCurrentState().asNetGameState())
        },
        onRequestJoinTeam = {
            logger.debug("Move request of player ${it.request.user.id}")
            GameManager.game.changeTeamOrRole(
                id = it.request.user.id,
                role = it.request.user.role,
                team = it.request.user.team
            )
        },
        onRequestRestart = {
            logger.debug("Restart request of player ${it.user.id}")
            if (it.isHost()){
                GameManager.game.restartGame()
            }
        },
        onRequestShuffleTeams = {
            logger.debug("Shuffle teams request of player ${it.user.id}")
            if (it.isHost()){
                GameManager.game.shuffleTeams()
            }
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