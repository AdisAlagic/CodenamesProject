package com.adisalagic.codenames.server.gamelogic

import com.adisalagic.codenames.server.configuration.ConfigurationManager
import com.adisalagic.codenames.utils.generateColor
import com.adisalagic.codenames.utils.shouldPlayerBecomeMaster
import java.util.Collections
import kotlin.math.ceil
import kotlin.math.round
import kotlin.random.Random
import kotlin.random.nextInt

class Game(private val listener: GameListener) {
    private val playerList = ArrayList<Player>()
    private var host: String = ConfigurationManager.config.host
    private var gameState = GameState.reset()

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

            fun getRandom(): Team {
                return values[Random.nextInt(0, 2)]
            }
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

    fun deleteUser(id: Int) {
        checkIfAnotherWordIsClicked(playerId = id, -1)
        playerList.remove(playerList.find { it.id == id })
        listener.onPlayerListChanged(playerList)
    }

    private fun editPlayer(player: Player) {
        playerList[playerList.indexOf(
            playerList.find { it.id == player.id }
        )] = player
        listener.onPlayerListChanged(playerList)
    }

    fun changeTeamOrRole(id: Int, role: String, team: String) {
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

    fun isHost(id: Int): Boolean {
        return playerList.find { it.id == id }?.isHost ?: false
    }

    fun restartGame() {
        gameState = GameState.reset()
        listener.onGameStateChanged(gameState)
    }

    fun getCurrentState(): GameState {
        return gameState
    }

    fun shuffleTeams() {
        if (gameState.state == GameState.GeneralState.PLAYING) {
            return
        }
        val withOutSpecs = playerList.filter { it.role != Role.SPECTATOR }.toMutableList()
        val resultList = mutableListOf<Player>()
        val size = withOutSpecs.size
        if (size == 0) {
            return
        }
        var redTeamSize = ceil((size / 2).toDouble()).toInt()
        var redHasMaster = false
        var blueHasMaster = false
        var blueTeamSize = size - redTeamSize
        while (redTeamSize > 0 || blueTeamSize > 0) {
            if (withOutSpecs.size == 0) {
                break
            }
            var side = Team.getRandom()
            if (blueTeamSize <= 0 && side == Team.BLUE){
                side = Team.RED
            }
            if (redTeamSize <= 0 && side == Team.RED){
                side = Team.BLUE
            }
            val isMaster = shouldPlayerBecomeMaster(Random.nextBoolean(), side, blueHasMaster, redHasMaster)
            var player = withOutSpecs.removeAt(Random.nextInt(0, withOutSpecs.size))
            if (side == Team.BLUE) {
                blueTeamSize--
                if (!blueHasMaster){
                    blueHasMaster = isMaster
                }
            }
            if (side == Team.RED) {
                redTeamSize--
                if (!redHasMaster){
                    redHasMaster = isMaster
                }
            }
            player = player.copy(
                team = side,
                role = if (isMaster) {
                    Role.MASTER
                } else {
                    Role.PLAYER
                }
            )
            resultList.add(player)
        }
        val red = resultList.filter { it.team == Team.RED }.toMutableList()
        val blue = resultList.filter { it.team == Team.BLUE }.toMutableList()
        if (!redHasMaster) {
            if (red.size != 0) {
                val index = Random.nextInt(0, red.size)
                red[index] = red[index].copy(role = Role.MASTER)
            }
        }
        if (!blueHasMaster) {
            if (blue.size != 0) {
                val index = Random.nextInt(0, blue.size)
                blue[index] = blue[index].copy(role = Role.MASTER)
            }
        }
        resultList.clear()
        resultList.addAll(red)
        resultList.addAll(blue)
        resultList.addAll(playerList.filter { it.role == Role.SPECTATOR })
        playerList.clear()
        playerList.addAll(resultList)
        listener.onPlayerListChanged(playerList)
    }

    fun pressWord(wordId: Int, playerId: Int, pressed: Boolean) {
        val player = playerList.find { it.id == playerId } ?: return
        val word = gameState.words.find { it.id == wordId } ?: return
        val list = mutableListOf<Player>().apply { addAll(word.usersPressed) }
        list.apply {
            if (pressed && list.indexOf(player) == -1) {
                add(player)
            } else {
                remove(player)
            }
        }
        val wordsList = mutableListOf<GameState.Word>().apply { addAll(gameState.words) }
        val index = wordsList.indexOf(wordsList.find { it.id == wordId })
        wordsList[index] = word.copy(usersPressed = list)
        gameState = gameState.copy(words = wordsList)
        checkWholeTeamClickedOnWord(wordId)
        checkIfAnotherWordIsClicked(playerId, wordId)
        listener.onGameStateChanged(gameState)
    }

    private fun checkWholeTeamClickedOnWord(wordId: Int) {
        val word = gameState.words.find { wordId == it.id } ?: return
        if (word.usersPressed.isEmpty()) {
            return
        }
        val team = word.usersPressed[0].team
        val teamList = playerList.filter { it.team == team }
        if (word.usersPressed.size == teamList.size) {
            //TODO Send word animation start command
        }
    }

    private fun checkIfAnotherWordIsClicked(playerId: Int, ignoreWordId: Int) {
        val finalList = gameState.words.toMutableList()
        finalList.forEachIndexed { index, it ->
            if (it.id == ignoreWordId){
                return@forEachIndexed
            }
            val mutList = it.usersPressed.toMutableList()
            val player = mutList.find { user -> user.id == playerId }
            mutList.remove(player)
            finalList[index] = it.copy(usersPressed = mutList)
        }

        gameState = gameState.copy(words = finalList)
    }
}