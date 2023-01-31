package com.adisalagic.codenames.server.gamelogic

import com.adisalagic.codenames.utils.generateColor

class Game(private val listener: GameListener) {
    private val playerList = ArrayList<Player>()
    private var host: String = ""

    enum class Role {
        SPECTATOR,
        MASTER,
        PLAYER;

        companion object {
            val values = Role.values()
        }
    }

    enum class Team {
        RED,
        BLUE,
        NONE;

        companion object {
            val values = Team.values()
        }
    }

    fun setUpHost(nick: String) {
        host = nick
        if (playerList.isNotEmpty()) {
            val player = playerList.find { it.nickname == nick }
            if (player != null) {
                playerList[playerList.indexOf(player)] = player.copy(isHost = true)
            }
        }
    }

    fun deleteUser(id: Int){
        playerList.remove(playerList.find { it.id == id })
        listener.onPlayerListChanged(playerList)
    }

    private fun editPlayer(player: Player){
        playerList[playerList.indexOf(
            playerList.find { it.id == player.id }
        )] = player
        listener.onPlayerListChanged(playerList)
    }

    fun changeTeamOrRole(id: Int, role: String, team: String){
        val player = playerList.find { it.id == id }
        editPlayer(player!!.copy(role = Role.valueOf(role.uppercase()), team = Team.valueOf(team.uppercase())))
    }

    fun generatePlayer(id: Int, nick: String): Player {
        val player = Player(
            color = generateColor(id),
            id = id,
            isHost = nick == host,
            nickname = nick,
            role = Role.SPECTATOR,
            team = Team.NONE
        )
        playerList.add(player)
        listener.onPlayerListChanged(playerList)
        return player
    }
}