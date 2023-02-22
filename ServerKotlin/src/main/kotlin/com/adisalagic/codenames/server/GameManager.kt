package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.gamelogic.Game
import com.adisalagic.codenames.server.gamelogic.GameListener
import com.adisalagic.codenames.server.gamelogic.GameState
import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.game.PlayerInfo
import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.game.StartOpenWord
import com.adisalagic.codenames.utils.asNetGameState
import com.adisalagic.codenames.utils.asPlayerInfoItem

object GameManager {
    private val logger = Logger.getLogger(this::class)
    val connection = ConnectionManager
    private val listener = object : GameListener {
        override fun onPlayerListChanged(list: List<Player>) {
            val players = ArrayList<PlayerInfo.User>()
            list.forEach {
                players.add(it.asPlayerInfoItem())
            }
            val plrList = PlayerList(players)
            connection.sendMessage(plrList)
        }

        override fun onGameStateChanged(gameState: GameState) {
            connection.sendMessage(gameState.asNetGameState())
        }

        override fun onStartOpenWord(word: GameState.Word) {
            var tempPlus = TimerHandler.getTimer()
            val list = mutableListOf(tempPlus)
            repeat(3){
                tempPlus += 1000uL
                list.add(tempPlus)
            }
            logger.debug("Creating message for open word animation start")
            connection.sendMessage(StartOpenWord(StartOpenWord.Word(word.id, list)))
        }

        override fun onTurnTimer(newTime: Int, isRunning: Boolean) {

        }
    }
    val game = Game(listener)

    fun resetGame(){
        game.restartGame()
    }

}