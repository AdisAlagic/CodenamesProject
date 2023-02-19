package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.objects.EventConverter
import com.adisalagic.codenames.server.objects.game.PlayerInfo
import com.adisalagic.codenames.server.objects.game.TimerInfo
import com.adisalagic.codenames.utils.asNetGameState
import com.adisalagic.codenames.utils.isHost
import java.time.LocalDateTime
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
                    /* Checks if user with the same socket trying to send another join request */
                    if (!user.justConnected){
                        if (eventConverter.isJoinRequest(msg)){
                            return@UserHandler
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
    private val connections = Collections.synchronizedList(mutableListOf<UserHandler>())

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
        },
        onRequestTimer = {
            val time = TimerHandler.getTimer()
            logger.debug("Timer sent: $time")
            val timeInfo = TimerInfo(time.toString(), LocalDateTime.now().toString())
            logger.debug(timeInfo.toString())
            sendMessage(timeInfo)
        },
        onRequestPressWord = {
            logger.debug("Press word request of player ${it.user.id}")
            GameManager.game.pressWord(
                wordId = it.word.id,
                playerId = it.user.id,
                pressed = it.word.pressed
            )
        },
        onRequestSendLog = {
            logger.debug("Log request from player ${it.user.id}")
            GameManager.game.provideLog(it.user.id, it.log)
        },
        onRequestPauseResume = {
            logger.debug("Pause/resume request from player ${it.user.id}")
            if (it.isHost()){
                GameManager.game.pauseResume()
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