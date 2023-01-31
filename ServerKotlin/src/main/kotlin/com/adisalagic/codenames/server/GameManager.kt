package com.adisalagic.codenames.server

import com.adisalagic.codenames.server.gamelogic.Game
import com.adisalagic.codenames.server.gamelogic.GameListener
import com.adisalagic.codenames.server.gamelogic.Player
import com.adisalagic.codenames.server.objects.EventConverter
import com.adisalagic.codenames.server.objects.game.PlayerList
import com.adisalagic.codenames.utils.asPlayerListItem

object GameManager {
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
    }
    val game = Game(listener)

    fun removeUser(id: Int){
        game.deleteUser(id)
    }

}