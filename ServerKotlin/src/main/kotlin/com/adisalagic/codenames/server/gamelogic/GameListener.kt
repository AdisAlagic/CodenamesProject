package com.adisalagic.codenames.server.gamelogic

interface GameListener {
    fun onPlayerListChanged(list: List<Player>)
}