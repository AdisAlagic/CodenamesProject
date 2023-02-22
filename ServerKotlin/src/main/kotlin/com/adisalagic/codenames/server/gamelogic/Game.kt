package com.adisalagic.codenames.server.gamelogic

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.TimerHandler
import com.adisalagic.codenames.server.configuration.ConfigurationManager
import com.adisalagic.codenames.utils.generateColor
import com.adisalagic.codenames.utils.shouldPlayerBecomeMaster
import com.adisalagic.codenames.utils.toRole
import com.adisalagic.codenames.utils.toTeam
import java.util.Collections
import java.util.Timer
import kotlin.concurrent.timer
import kotlin.concurrent.timerTask
import kotlin.math.ceil
import kotlin.math.round
import kotlin.random.Random
import kotlin.random.nextInt

class Game(private val listener: GameListener) {
    private var wordTemp: Timer? = null
    private val logger = Logger.getLogger(this::class)
    private val playerList = ArrayList<Player>()
    private var host: String = ConfigurationManager.config.host
    private var gameState = GameState.reset()
    private var turnTimer: Timer? = null
    @Volatile
    private var turnTimerValue = 2 * 60 //seconds

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

    fun changeTeamOrRole(id: Int, role: Int, team: Int) {
        val player = playerList.find { it.id == id }
        editPlayer(player!!.copy(role = role.toRole(), team = team.toTeam()))
    }

    fun generatePlayer(id: Int, nick: String): Player {
        val player = Player(
            color = generateColor(id),
            id = id,
            isHost = nick == host && !alreadyHasHost(),
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
            if (blueTeamSize <= 0 && side == Team.BLUE) {
                side = Team.RED
            }
            if (redTeamSize <= 0 && side == Team.RED) {
                side = Team.BLUE
            }
            val isMaster = shouldPlayerBecomeMaster(Random.nextBoolean(), side, blueHasMaster, redHasMaster)
            var player = withOutSpecs.removeAt(Random.nextInt(0, withOutSpecs.size))
            if (side == Team.BLUE) {
                blueTeamSize--
                if (!blueHasMaster) {
                    blueHasMaster = isMaster
                }
            }
            if (side == Team.RED) {
                redTeamSize--
                if (!redHasMaster) {
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
        checkIfAnotherWordIsClicked(playerId, wordId)
        listener.onGameStateChanged(gameState)
        checkWholeTeamClickedOnWord(wordId)
    }

    private fun checkWholeTeamClickedOnWord(wordId: Int) {
        var word = gameState.words.find { wordId == it.id } ?: return
        if (word.usersPressed.isEmpty()) {
            return
        }
        val team = word.usersPressed[0].team
        val teamList = playerList.filter { it.team == team }
        if (word.usersPressed.size == teamList.size) {
            logger.debug("Whole team ${team.name} clicked on word ${word.name}")
            listener.onStartOpenWord(word)
            wordTemp = timer("Word opening", false, 4050, 1) {
                word = word.copy(visible = true, usersPressed = emptyList())
                wordTemp!!.cancel()
                wordTemp = null
                val words = gameState.words.toMutableList()
                words[words.indexOf(gameState.words.find { wordId == it.id }!!)] = word
                gameState = gameState.copy(words = words)
                checkOpenedWord(word, team)
                listener.onGameStateChanged(gameState)
            }
        } else {
            if (wordTemp == null) {
                return
            }
            wordTemp!!.cancel()
            wordTemp = null
        }
        listener.onGameStateChanged(gameState)
    }

    private fun checkIfAnotherWordIsClicked(playerId: Int, ignoreWordId: Int) {
        val finalList = gameState.words.toMutableList()
        finalList.forEachIndexed { index, it ->
            if (it.id == ignoreWordId) {
                return@forEachIndexed
            }
            val mutList = it.usersPressed.toMutableList()
            val player = mutList.find { user -> user.id == playerId }
            mutList.remove(player)
            finalList[index] = it.copy(usersPressed = mutList)
        }
        gameState = gameState.copy(words = finalList)
        if (wordTemp != null) {
            wordTemp?.cancel()
            wordTemp = null
        }
    }

    fun provideLog(userId: Int, log: String) {
        val player = playerList.find { it.id == userId }
        if (player?.role != Role.MASTER) {
            return
        }
        val redList = gameState.redScore.logs.toMutableList()
        val blueList = gameState.blueScore.logs.toMutableList()
        when (player.team) {
            Team.RED -> redList.add(log)
            Team.BLUE -> blueList.add(log)
            Team.NONE -> return
        }
        gameState = gameState.copy(
            blueScore = gameState.blueScore.copy(logs = blueList),
            redScore = gameState.redScore.copy(logs = redList),
            state = GameState.GeneralState.PLAYING
        )
        nextTurn()
    }

    private fun nextTurn() {
        var turn = gameState.turn
        turn = when (turn) {
            GameState.Turn.BlueMaster -> GameState.Turn.BluePlayers
            GameState.Turn.BluePlayers -> GameState.Turn.RedMaster
            GameState.Turn.RedMaster -> GameState.Turn.RedPlayers
            GameState.Turn.RedPlayers -> GameState.Turn.BlueMaster
            else -> return
        }
        gameState = gameState.copy(turn = turn)
        recreateTimer()
        listener.onGameStateChanged(gameState)
    }

    private fun checkOpenedWord(word: GameState.Word, teamSelected: Team) {
        when (word.side) {
            GameState.Side.BLUE -> {
                if (teamSelected == Team.BLUE) {
                    addFifteenSeconds()
                } else {
                    nextTurn()
                }
                gameState =
                    gameState.copy(blueScore = gameState.blueScore.copy(score = gameState.blueScore.score.dec()))
            }

            GameState.Side.RED -> {
                if (teamSelected == Team.RED) {
                    addFifteenSeconds()
                } else {
                    nextTurn()
                }
                gameState =
                    gameState.copy(redScore = gameState.redScore.copy(score = gameState.redScore.score.dec()))
            }

            GameState.Side.WHITE -> nextTurn()
            GameState.Side.BLACK -> gameState = gameState.copy(state = GameState.GeneralState.ENDED)
        }
        if (gameState.redScore.score == 0 || gameState.blueScore.score == 0) {

            gameState = gameState.copy(
                state = GameState.GeneralState.ENDED,
            )
        }
        if (gameState.state == GameState.GeneralState.ENDED) {
            val words = gameState.words.toMutableList()
            gameState = gameState.copy(
                words = words.onEachIndexed { index, w ->
                    words[index] = w.copy(visible = true)
                }
            )
        }
    }

    fun pauseResume() {
        val state = when (gameState.state) {
            GameState.GeneralState.NOT_STARTED -> {
                GameState.GeneralState.NOT_STARTED
            }

            GameState.GeneralState.PLAYING -> {
                TimerHandler.stop()
                turnTimer?.cancel()
                turnTimer = null
                GameState.GeneralState.PAUSED
            }

            GameState.GeneralState.PAUSED -> {
                TimerHandler.resume()
                recreateTimer(false)
                GameState.GeneralState.PLAYING
            }

            GameState.GeneralState.ENDED -> GameState.GeneralState.ENDED
        }
        gameState = gameState.copy(state = state)
        if (wordTemp != null) {
            wordTemp?.cancel()
            wordTemp = null
        }
        sendTimerInfo()
        listener.onGameStateChanged(gameState)
    }

    private fun alreadyHasHost(): Boolean {
        playerList.forEach {
            if (it.isHost) {
                return true
            }
        }
        return false
    }

    private fun createTurnTimer(): Timer {
        return timer("TurnTimer", false, 0, 1000) {
            turnTimerValue--
        }.apply {
            sendTimerInfo()
            schedule(timerTask {
                sendTimerInfo()
            }, 0, 500)
        }
    }

    private fun recreateTimer(dropTime: Boolean = true){
        if (turnTimer != null){
            turnTimer?.cancel()
            turnTimer = null
        }
        if (dropTime){
            turnTimerValue = 2 * 60
        }
        turnTimer = createTurnTimer()
    }

    private fun sendTimerInfo(){
        listener.onTurnTimer(turnTimerValue, turnTimer != null)
    }

    private fun addFifteenSeconds(){
        val fifteenSeconds = 15000
        if (turnTimer != null){
            synchronized(turnTimer!!){
                turnTimerValue += fifteenSeconds
            }
        }
    }
}