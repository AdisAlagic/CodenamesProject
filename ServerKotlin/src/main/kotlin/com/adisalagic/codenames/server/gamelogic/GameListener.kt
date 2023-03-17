package com.adisalagic.codenames.server.gamelogic

interface GameListener {
    fun onPlayerListChanged(list: List<Player>)
    fun onGameStateChanged(gameState: GameState)
    fun onStartOpenWord(word: GameState.Word)
    fun onStartSkipWord()
    fun onTurnTimer(newTime: Int, isRunning: Boolean)
}