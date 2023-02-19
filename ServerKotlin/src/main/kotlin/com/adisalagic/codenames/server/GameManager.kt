package com.adisalagic.codenames.server

import com.adisalagic.codenames.Logger
import com.adisalagic.codenames.server.gamelogic.Game
import com.adisalagic.codenames.server.gamelogic.GameListener
import com.adisalagic.codenames.server.gamelogic.GameState
import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.server.objects.game.StartOpenWord
import com.adisalagic.codenames.utils.asNetGameState
import com.adisalagic.codenames.utils.asPlayerListItem
import kotlin.concurrent.timer

object GameManager {
    private val logger = Logger.getLogger(this::class)
    val connection = ConnectionManager
    private val listener = object : GameListener {
        override fun onPlayerListChanged(list: List<Player>) {
            val players = ArrayList<PlayerList.User>()
            list.forEach {
                players.add(it.asPlayerListItem())
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
    }
    val game = Game(listener)

    fun resetGame(){
        game.restartGame()
    }

}